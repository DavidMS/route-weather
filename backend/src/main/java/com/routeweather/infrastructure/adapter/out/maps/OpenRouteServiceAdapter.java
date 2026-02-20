package com.routeweather.infrastructure.adapter.out.maps;

import com.routeweather.application.port.out.RouteCalculatorPort;
import com.routeweather.domain.exception.RouteNotFoundException;
import com.routeweather.domain.model.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Replaced by NominatimOsrmAdapter.
 * Kept for reference. Not registered as a Spring bean (@Component removed).
 */
public class OpenRouteServiceAdapter implements RouteCalculatorPort {

    private static final Logger log = LoggerFactory.getLogger(OpenRouteServiceAdapter.class);

    private final String baseUrl;
    private final String apiKey;
    private final RestTemplate restTemplate;

    public OpenRouteServiceAdapter(
            @Value("${external.openrouteservice.base-url}") String baseUrl,
            @Value("${external.openrouteservice.api-key:}") String apiKey,
            RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public Coordinates geocode(String placeName) {
        if (isStubMode()) {
            log.warn("ORS_API_KEY not set — returning stub coordinates for '{}'", placeName);
            // Return Madrid's coordinates as a universal stub
            return new Coordinates(40.4168, -3.7038);
        }

        // TODO: call ORS Geocoding API
        // GET {baseUrl}/geocode/search?api_key={apiKey}&text={placeName}
        throw new RouteNotFoundException("Geocoding not yet implemented. Set ORS_API_KEY.");
    }

    @Override
    public List<Coordinates> calculateWaypoints(Coordinates origin, Coordinates destination) {
        if (isStubMode()) {
            log.warn("ORS_API_KEY not set — returning stub waypoints (origin + destination only)");
            return List.of(origin, destination);
        }

        // TODO: call ORS Directions API to get route geometry, then sample waypoints
        // POST {baseUrl}/directions/driving-car
        // Body: { "coordinates": [[lon,lat], [lon,lat]] }
        return List.of(origin, destination);
    }

    private boolean isStubMode() {
        return apiKey == null || apiKey.isBlank();
    }
}
