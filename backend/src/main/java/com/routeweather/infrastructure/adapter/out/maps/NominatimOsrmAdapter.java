package com.routeweather.infrastructure.adapter.out.maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.routeweather.application.port.out.RouteCalculatorPort;
import com.routeweather.domain.exception.RouteNotFoundException;
import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.RouteDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Outbound adapter: geocoding via Nominatim (OSM) and routing via OSRM.
 * Both are free and require no API key.
 *
 * Nominatim docs: https://nominatim.org/release-docs/develop/api/Search/
 * OSRM docs:      http://project-osrm.org/docs/v5.24.0/api/
 */
@Component
public class NominatimOsrmAdapter implements RouteCalculatorPort {

    private static final Logger log = LoggerFactory.getLogger(NominatimOsrmAdapter.class);

    private final String nominatimBaseUrl;
    private final String nominatimUserAgent;
    private final String osrmBaseUrl;
    private final int maxWaypoints;
    private final RestTemplate restTemplate;

    public NominatimOsrmAdapter(
            @Value("${external.nominatim.base-url}") String nominatimBaseUrl,
            @Value("${external.nominatim.user-agent}") String nominatimUserAgent,
            @Value("${external.osrm.base-url}") String osrmBaseUrl,
            @Value("${external.osrm.max-waypoints:6}") int maxWaypoints,
            RestTemplate restTemplate) {
        this.nominatimBaseUrl = nominatimBaseUrl;
        this.nominatimUserAgent = nominatimUserAgent;
        this.osrmBaseUrl = osrmBaseUrl;
        this.maxWaypoints = maxWaypoints;
        this.restTemplate = restTemplate;
    }

    /**
     * Converts a place name to coordinates using Nominatim.
     * Nominatim requires a descriptive User-Agent per OSM usage policy.
     */
    @Override
    public Coordinates geocode(String placeName) {
        String url = UriComponentsBuilder
                .fromHttpUrl(nominatimBaseUrl + "/search")
                .queryParam("q", placeName)
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", nominatimUserAgent);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            JsonNode response = restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class).getBody();

            if (response == null || response.isEmpty()) {
                throw new RouteNotFoundException("Place not found: " + placeName);
            }

            JsonNode first = response.get(0);
            double latitude = first.path("lat").asDouble();
            double longitude = first.path("lon").asDouble();
            log.debug("Geocoded '{}' → ({}, {})", placeName, latitude, longitude);
            return new Coordinates(latitude, longitude);

        } catch (RestClientException e) {
            throw new RouteNotFoundException("Geocoding failed for '" + placeName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Calls OSRM once with overview=simplified to get the road-following geometry,
     * then samples maxWaypoints evenly from it for weather forecast queries.
     *
     * overview=simplified gives a compact polyline (~20-50 points for long routes)
     * that still accurately follows the road, making it suitable for both map
     * display and as a source for evenly-sampled weather waypoints.
     */
    @Override
    public RouteDetails calculateRoute(Coordinates origin, Coordinates destination) {
        String coordParam = String.format("%s,%s;%s,%s",
                origin.longitude(), origin.latitude(),
                destination.longitude(), destination.latitude());

        String url = UriComponentsBuilder
                .fromHttpUrl(osrmBaseUrl + "/route/v1/driving/" + coordParam)
                .queryParam("overview", "simplified")
                .queryParam("geometries", "geojson")
                .build()
                .toUriString();

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            String code = response.path("code").asText();
            if (!"Ok".equals(code)) {
                log.warn("OSRM returned code '{}', falling back to origin+destination", code);
                List<Coordinates> fallback = List.of(origin, destination);
                return new RouteDetails(fallback, fallback, 0.0);
            }

            JsonNode route = response.path("routes").get(0);
            double totalDurationSeconds = route.path("duration").asDouble(0.0);
            JsonNode coordinates = route.path("geometry").path("coordinates");

            List<Coordinates> geometry = new ArrayList<>(coordinates.size());
            for (JsonNode coord : coordinates) {
                geometry.add(new Coordinates(coord.get(1).asDouble(), coord.get(0).asDouble()));
            }

            List<Coordinates> weatherWaypoints = sampleEvenly(geometry, maxWaypoints);
            log.debug("OSRM simplified: {} geometry points, {} weather waypoints, duration {}s",
                    geometry.size(), weatherWaypoints.size(), totalDurationSeconds);
            return new RouteDetails(geometry, weatherWaypoints, totalDurationSeconds);

        } catch (RestClientException e) {
            log.warn("OSRM routing failed: {}, falling back to origin+destination", e.getMessage());
            List<Coordinates> fallback = List.of(origin, destination);
            return new RouteDetails(fallback, fallback, 0.0);
        }
    }

    private List<Coordinates> sampleEvenly(List<Coordinates> points, int n) {
        if (points.size() <= n) return points;
        List<Coordinates> sampled = new ArrayList<>(n);
        double step = (double) (points.size() - 1) / (n - 1);
        for (int i = 0; i < n; i++) {
            sampled.add(points.get((int) Math.round(i * step)));
        }
        return sampled;
    }
}
