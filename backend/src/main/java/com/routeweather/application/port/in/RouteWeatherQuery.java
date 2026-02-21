package com.routeweather.application.port.in;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Input command for the GetRouteWeatherUseCase.
 *
 * Carries the raw user input (city names + date + departure time). Coordinates are NOT
 * part of this object — they are resolved by the service using RouteCalculatorPort.geocode().
 *
 * Lives in the application layer so it can be used by both the use case interface
 * and any inbound adapter (REST, CLI, etc.) without leaking domain objects outward.
 */
public record RouteWeatherQuery(
        String origin,
        String destination,
        LocalDate travelDate,
        LocalTime departureTime
) {}
