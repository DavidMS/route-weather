package com.routeweather.application.service;

import com.routeweather.application.port.in.GetRouteWeatherUseCase;
import com.routeweather.application.port.in.RouteWeatherQuery;
import com.routeweather.application.port.out.RouteCalculatorPort;
import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.Route;
import com.routeweather.domain.model.RouteDetails;
import com.routeweather.domain.model.RouteWeatherReport;
import com.routeweather.domain.model.TimedWaypoint;
import com.routeweather.domain.model.WeatherPoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Application service implementing the main use case.
 *
 * Flow:
 *  1. Geocode origin and destination city names → Coordinates
 *  2. Build the domain Route entity
 *  3. Calculate route: returns road-following geometry + sampled weather waypoints + total duration
 *  4. Compute estimated arrival time at each weather waypoint based on departure time and
 *     the waypoint's fractional position along the route
 *  5. Fetch hourly weather forecast at each timed waypoint
 *  6. Assemble and return RouteWeatherReport (includes geometry for map display)
 *
 * Plain Java class — NO Spring annotations. Wired in BeanConfiguration.
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
                query.travelDate(),
                query.departureTime());

        RouteDetails routeDetails = routeCalculatorPort.calculateRoute(originCoords, destinationCoords);

        List<TimedWaypoint> timedWaypoints = buildTimedWaypoints(
                routeDetails.weatherWaypoints(),
                LocalDateTime.of(query.travelDate(), query.departureTime()),
                routeDetails.totalDurationSeconds());

        List<WeatherPoint> weatherPoints = weatherForecastPort.getForecast(timedWaypoints);

        return new RouteWeatherReport(route, weatherPoints, routeDetails.geometry());
    }

    /**
     * Distributes estimated arrival times across waypoints assuming uniform travel speed.
     *
     * Waypoint 0 gets the departure time; the last waypoint gets departure + total duration;
     * intermediate waypoints are interpolated linearly by their position index.
     */
    private List<TimedWaypoint> buildTimedWaypoints(
            List<Coordinates> waypoints,
            LocalDateTime departure,
            double totalDurationSeconds) {

        int n = waypoints.size();
        return IntStream.range(0, n)
                .mapToObj(i -> {
                    double fraction = (n > 1) ? (double) i / (n - 1) : 0.0;
                    long offsetSeconds = Math.round(fraction * totalDurationSeconds);
                    LocalDateTime arrival = departure.plusSeconds(offsetSeconds);
                    return new TimedWaypoint(waypoints.get(i), arrival);
                })
                .toList();
    }
}
