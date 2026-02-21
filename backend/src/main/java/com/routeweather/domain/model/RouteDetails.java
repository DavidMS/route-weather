package com.routeweather.domain.model;

import java.util.List;

/**
 * Value object returned by RouteCalculatorPort.calculateRoute().
 *
 * Separates the two concerns of the routing call:
 *  - geometry: the full road-following polyline (used to draw the map)
 *  - weatherWaypoints: a small evenly-sampled subset (used to query the weather API)
 */
public record RouteDetails(
        List<Coordinates> geometry,
        List<Coordinates> weatherWaypoints,
        double totalDurationSeconds
) {}
