package com.routeweather.application.service;

import com.routeweather.application.port.in.GetRouteWeatherUseCase;
import com.routeweather.application.port.in.RouteWeatherQuery;
import com.routeweather.application.port.out.RouteCalculatorPort;
import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.Route;
import com.routeweather.domain.model.RouteWeatherReport;
import com.routeweather.domain.model.WeatherPoint;

import java.util.List;

/**
 * Application service implementing the main use case.
 *
 * Flow:
 *  1. Geocode origin and destination city names → Coordinates
 *  2. Build the domain Route entity
 *  3. Calculate waypoints along the driving route
 *  4. Fetch weather forecast at each waypoint
 *  5. Assemble and return RouteWeatherReport
 *
 * This is a plain Java class — NO Spring annotations.
 * It is wired as a Spring bean via BeanConfiguration.
 */
public class RouteWeatherService implements GetRouteWeatherUseCase {

    private final RouteCalculatorPort routeCalculatorPort;
    private final WeatherForecastPort weatherForecastPort;

    public RouteWeatherService(
            RouteCalculatorPort routeCalculatorPort,
            WeatherForecastPort weatherForecastPort) {
        this.routeCalculatorPort = routeCalculatorPort;
        this.weatherForecastPort = weatherForecastPort;
    }

    @Override
    public RouteWeatherReport getWeatherForRoute(RouteWeatherQuery query) {
        Coordinates originCoords = routeCalculatorPort.geocode(query.origin());
        Coordinates destinationCoords = routeCalculatorPort.geocode(query.destination());

        Route route = new Route(
                query.origin(),
                query.destination(),
                originCoords,
                destinationCoords,
                query.travelDate());

        List<Coordinates> waypoints = routeCalculatorPort.calculateWaypoints(
                originCoords, destinationCoords);

        List<WeatherPoint> weatherPoints = weatherForecastPort.getForecast(
                waypoints, query.travelDate());

        return new RouteWeatherReport(route, weatherPoints);
    }
}
