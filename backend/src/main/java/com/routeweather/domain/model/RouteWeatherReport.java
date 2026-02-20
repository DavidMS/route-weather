package com.routeweather.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Domain aggregate: the result of the main use case.
 *
 * Contains:
 *  - route: origin, destination, travel date
 *  - weatherPoints: forecast at each sampled waypoint
 *  - routeGeometry: road-following polyline for map display
 */
public class RouteWeatherReport {

    private final Route route;
    private final List<WeatherPoint> weatherPoints;
    private final List<Coordinates> routeGeometry;

    public RouteWeatherReport(Route route, List<WeatherPoint> weatherPoints, List<Coordinates> routeGeometry) {
        this.route = Objects.requireNonNull(route, "route is required");
        this.weatherPoints = List.copyOf(Objects.requireNonNull(weatherPoints, "weatherPoints is required"));
        this.routeGeometry = List.copyOf(Objects.requireNonNull(routeGeometry, "routeGeometry is required"));
    }

    public Route getRoute() { return route; }
    public List<WeatherPoint> getWeatherPoints() { return weatherPoints; }
    public List<Coordinates> getRouteGeometry() { return routeGeometry; }
}
