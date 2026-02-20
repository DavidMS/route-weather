package com.routeweather.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;

/**
 * Outbound DTO: a single weather forecast point returned to the client.
 */
public record WeatherPointResponse(
        double latitude,
        double longitude,
        LocalDateTime forecastTime,
        double temperatureCelsius,
        double precipitationMm,
        int windSpeedKmh,
        String condition
) {}
