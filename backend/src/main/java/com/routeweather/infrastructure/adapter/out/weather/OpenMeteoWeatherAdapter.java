package com.routeweather.infrastructure.adapter.out.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.domain.model.Coordinates;
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
 * Outbound adapter: fetches daily weather forecasts from Open-Meteo.
 * Free API, no key required. Covers up to ~16 days in advance.
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
    public List<WeatherPoint> getForecast(List<Coordinates> waypoints, LocalDate date) {
        return waypoints.stream()
                .map(coords -> fetchForecastForPoint(coords, date))
                .toList();
    }

    private WeatherPoint fetchForecastForPoint(Coordinates coords, LocalDate date) {
        String dateStr = date.toString(); // YYYY-MM-DD

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/forecast")
                .queryParam("latitude", coords.latitude())
                .queryParam("longitude", coords.longitude())
                .queryParam("daily", "weathercode,temperature_2m_max,temperature_2m_min,precipitation_sum,windspeed_10m_max")
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
                return stubWeatherPoint(coords, date);
            }

            JsonNode daily = response.path("daily");
            double tempMax = daily.path("temperature_2m_max").get(0).asDouble();
            double tempMin = daily.path("temperature_2m_min").get(0).asDouble();
            double precipitation = daily.path("precipitation_sum").get(0).asDouble();
            int windSpeed = daily.path("windspeed_10m_max").get(0).asInt();
            int weatherCode = daily.path("weathercode").get(0).asInt();

            double avgTemp = (tempMax + tempMin) / 2.0;
            WeatherCondition condition = mapWeatherCode(weatherCode);

            log.debug("Forecast ({},{}) date={}: {}°C, {}", coords.latitude(), coords.longitude(), date, avgTemp, condition);
            return new WeatherPoint(coords, LocalDateTime.of(date, LocalTime.NOON), avgTemp, precipitation, windSpeed, condition);

        } catch (RestClientException e) {
            log.warn("Failed to fetch forecast for ({},{}): {}", coords.latitude(), coords.longitude(), e.getMessage());
            return stubWeatherPoint(coords, date);
        }
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

    private WeatherPoint stubWeatherPoint(Coordinates coords, LocalDate date) {
        return new WeatherPoint(coords, LocalDateTime.of(date, LocalTime.NOON), 20.0, 0.0, 15, WeatherCondition.CLEAR);
    }
}
