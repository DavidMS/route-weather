package com.routeweather.infrastructure.adapter.out.weather;

import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.WeatherCondition;
import com.routeweather.domain.model.WeatherPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbound adapter: fetches weather forecasts from Open-Meteo (free, no API key).
 *
 * API docs: https://open-meteo.com/en/docs
 * Endpoint: GET /v1/forecast?latitude={lat}&longitude={lon}&daily=temperature_2m_max,...
 *
 * TODO: implement real HTTP call using RestTemplate or WebClient.
 *       Replace the stub below once the routing adapter is also working end-to-end.
 */
@Component
public class OpenMeteoWeatherAdapter implements WeatherForecastPort {

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
        // TODO: call Open-Meteo API for each waypoint
        // For now, return a stub with placeholder data so the app starts and the
        // architecture can be tested end-to-end before real API integration.
        return waypoints.stream()
                .map(coords -> stubWeatherPoint(coords, date))
                .toList();
    }

    private WeatherPoint stubWeatherPoint(Coordinates coords, LocalDate date) {
        return new WeatherPoint(
                coords,
                LocalDateTime.of(date, java.time.LocalTime.NOON),
                20.0,
                0.0,
                15,
                WeatherCondition.CLEAR);
    }
}
