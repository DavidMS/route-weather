package com.routeweather.infrastructure.adapter.in.rest.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Outbound DTO: the full route weather report returned to the client.
 *
 * routeGeometry: road-following polyline points (for map display)
 * weatherPoints: forecast at each sampled waypoint (subset of the geometry)
 */
public record RouteWeatherResponse(
        String origin,
        String destination,
        LocalDate travelDate,
        List<WeatherPointResponse> weatherPoints,
        List<CoordinatesResponse> routeGeometry
) {}
