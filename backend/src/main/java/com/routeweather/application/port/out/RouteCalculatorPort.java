package com.routeweather.application.port.out;

import com.routeweather.domain.model.Coordinates;

import java.util.List;

/**
 * Outbound port (driven side): resolves a route into a list of waypoints.
 *
 * The application layer depends on this interface; the actual HTTP call
 * lives in the infrastructure adapter (OpenRouteServiceAdapter).
 *
 * Also responsible for geocoding: converting city names → Coordinates.
 */
public interface RouteCalculatorPort {

    /**
     * Convert a place name (e.g., "Madrid") to geographic coordinates.
     *
     * @throws com.routeweather.domain.exception.RouteNotFoundException if the place cannot be found
     */
    Coordinates geocode(String placeName);

    /**
     * Return a list of waypoints along the driving route between two coordinates.
     * The list includes the origin as the first element and destination as the last.
     */
    List<Coordinates> calculateWaypoints(Coordinates origin, Coordinates destination);
}
