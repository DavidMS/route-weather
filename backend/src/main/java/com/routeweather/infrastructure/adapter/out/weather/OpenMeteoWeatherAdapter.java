package com.routeweather.infrastructure.adapter.out.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.TimedWaypoint;
import com.routeweather.domain.model.WeatherCondition;
import com.routeweather.domain.model.WeatherPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Outbound adapter: fetches hourly weather forecasts from Open-Meteo.
 * Free API, no key required. Covers up to ~16 days in advance.
 *
 * Each waypoint receives a forecast for the hour matching its estimated arrival time,
 * so travellers see the weather they will actually encounter at each point.
 *
 * API docs: https://open-meteo.com/en/docs
 * WMO weather interpretation codes: https://open-meteo.com/en/docs#weathervariables
 */
@Component
public class OpenMeteoWeatherAdapter implements WeatherForecastPort {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherAdapter.class);

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public OpenMeteoWeatherAdapter(
            @Value("${external.openmeteo.base-url}") String baseUrl,
            RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<WeatherPoint> getForecast(List<TimedWaypoint> waypoints) {
        return waypoints.stream()
                .map(this::fetchForecastForPoint)
                .toList();
    }

    private WeatherPoint fetchForecastForPoint(TimedWaypoint waypoint) {
        Coordinates coords = waypoint.coordinates();
        LocalDateTime arrivalDateTime = waypoint.estimatedArrival();
        LocalDate date = arrivalDateTime.toLocalDate();
        String dateStr = date.toString(); // YYYY-MM-DD

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/forecast")
                .queryParam("latitude", coords.latitude())
                .queryParam("longitude", coords.longitude())
                .queryParam("hourly", "temperature_2m,precipitation,windspeed_10m,weathercode")
                .queryParam("start_date", dateStr)
                .queryParam("end_date", dateStr)
                .queryParam("timezone", "auto")
                .build()
                .toUriString();

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response == null || response.path("error").asBoolean(false)) {
                String reason = response != null ? response.path("reason").asText("unknown") : "null response";
                log.warn("Open-Meteo error for ({},{}): {}", coords.latitude(), coords.longitude(), reason);
                return stubWeatherPoint(coords, arrivalDateTime);
            }

            JsonNode hourly = response.path("hourly");
            int hourIndex = findHourIndex(hourly.path("time"), arrivalDateTime);

            double temperature = hourly.path("temperature_2m").get(hourIndex).asDouble();
            double precipitation = hourly.path("precipitation").get(hourIndex).asDouble();
            int windSpeed = hourly.path("windspeed_10m").get(hourIndex).asInt();
            int weatherCode = hourly.path("weathercode").get(hourIndex).asInt();

            WeatherCondition condition = mapWeatherCode(weatherCode);

            log.debug("Forecast ({},{}) at {}: {}°C, {}", coords.latitude(), coords.longitude(), arrivalDateTime, temperature, condition);
            return new WeatherPoint(coords, arrivalDateTime, temperature, precipitation, windSpeed, condition);

        } catch (RestClientException e) {
            log.warn("Failed to fetch forecast for ({},{}): {}", coords.latitude(), coords.longitude(), e.getMessage());
            return stubWeatherPoint(coords, arrivalDateTime);
        }
    }

    /**
     * Finds the index in the hourly time array that best matches the target datetime.
     *
     * Open-Meteo returns time strings in "YYYY-MM-DDTHH:mm" format. We match on the
     * hour; if no exact match is found, the first index (midnight) is used as a fallback.
     */
    private int findHourIndex(JsonNode timeArray, LocalDateTime target) {
        String targetHourStr = target.toLocalDate() + "T" + String.format("%02d:00", target.getHour());
        for (int i = 0; i < timeArray.size(); i++) {
            if (targetHourStr.equals(timeArray.get(i).asText())) {
                return i;
            }
        }
        log.warn("Could not find exact hour {} in Open-Meteo response, using index 0", targetHourStr);
        return 0;
    }

    /**
     * Maps WMO weather interpretation codes to our domain WeatherCondition.
     * Full code table: https://open-meteo.com/en/docs#weathervariables
     */
    private WeatherCondition mapWeatherCode(int code) {
        if (code == 0)                         return WeatherCondition.CLEAR;
        if (code <= 2)                         return WeatherCondition.PARTLY_CLOUDY;
        if (code == 3)                         return WeatherCondition.CLOUDY;
        if (code == 45 || code == 48)          return WeatherCondition.FOGGY;
        if (code >= 51 && code <= 57)          return WeatherCondition.RAINY;         // drizzle
        if (code >= 61 && code <= 63)          return WeatherCondition.RAINY;         // moderate rain
        if (code == 65 || code == 67)          return WeatherCondition.HEAVY_RAIN;   // heavy rain / freezing
        if (code >= 71 && code <= 77)          return WeatherCondition.SNOWY;
        if (code >= 80 && code <= 81)          return WeatherCondition.RAINY;         // showers
        if (code == 82)                        return WeatherCondition.HEAVY_RAIN;   // violent showers
        if (code == 85 || code == 86)          return WeatherCondition.SNOWY;         // snow showers
        if (code >= 95)                        return WeatherCondition.STORMY;        // thunderstorm
        return WeatherCondition.CLOUDY;
    }

    private WeatherPoint stubWeatherPoint(Coordinates coords, LocalDateTime arrivalDateTime) {
        return new WeatherPoint(coords, arrivalDateTime, 20.0, 0.0, 15, WeatherCondition.CLEAR);
    }
}
