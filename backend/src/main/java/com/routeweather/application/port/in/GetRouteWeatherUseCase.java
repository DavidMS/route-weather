package com.routeweather.application.port.in;

import com.routeweather.domain.model.RouteWeatherReport;

/**
 * Inbound port (driving side): the primary use case of the application.
 *
 * Given a RouteWeatherQuery (origin name, destination name, travel date), the
 * service geocodes the city names, calculates waypoints, fetches forecasts, and
 * returns a RouteWeatherReport.
 *
 * Implemented by: application/service/RouteWeatherService
 * Called by:      infrastructure/adapter/in/rest/RouteWeatherController
 */
public interface GetRouteWeatherUseCase {

    RouteWeatherReport getWeatherForRoute(RouteWeatherQuery query);
}
