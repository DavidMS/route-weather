package com.routeweather.infrastructure.adapter.in.rest.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Outbound DTO: the full route weather report returned to the client.
 */
public record RouteWeatherResponse(
        String origin,
        String destination,
        LocalDate travelDate,
        List<WeatherPointResponse> weatherPoints
) {}
