package com.routeweather.application.port.out;

import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.RouteDetails;

/**
 * Outbound port (driven side): geocoding and route calculation.
 *
 * Implemented by: infrastructure/adapter/out/maps/NominatimOsrmAdapter
 */
public interface RouteCalculatorPort {

    /**
     * Convert a place name (e.g., "Madrid") to geographic coordinates.
     *
     * @throws com.routeweather.domain.exception.RouteNotFoundException if the place cannot be found
     */
    Coordinates geocode(String placeName);

    /**
     * Calculate a driving route between two coordinates.
     * Returns both the road-following geometry (for map display) and a small
     * set of evenly-sampled waypoints (for weather forecast queries).
     */
    RouteDetails calculateRoute(Coordinates origin, Coordinates destination);
}
