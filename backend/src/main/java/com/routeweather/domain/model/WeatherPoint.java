package com.routeweather.domain.model;

import java.time.LocalDateTime;

/**
 * Value object: weather forecast at a specific geographic point and time.
 *
 * No framework dependencies — pure domain code.
 */
public record WeatherPoint(
        Coordinates coordinates,
        LocalDateTime forecastTime,
        double temperatureCelsius,
        double precipitationMm,
        int windSpeedKmh,
        WeatherCondition condition
) {}
