package com.routeweather.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Domain aggregate: the result of the main use case.
 * Combines a Route with the weather forecasts at each waypoint along it.
 *
 * No framework dependencies — pure domain code.
 */
public class RouteWeatherReport {

    private final Route route;
    private final List<WeatherPoint> weatherPoints;

    public RouteWeatherReport(Route route, List<WeatherPoint> weatherPoints) {
        this.route = Objects.requireNonNull(route, "route is required");
        this.weatherPoints = List.copyOf(
                Objects.requireNonNull(weatherPoints, "weatherPoints is required"));
    }

    public Route getRoute() { return route; }
    public List<WeatherPoint> getWeatherPoints() { return weatherPoints; }
}
