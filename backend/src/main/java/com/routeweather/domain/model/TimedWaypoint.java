package com.routeweather.domain.model;

import java.time.LocalDateTime;

/**
 * Value object pairing a geographic waypoint with the estimated time of arrival.
 *
 * Used by WeatherForecastPort so that each waypoint can receive an hourly forecast
 * matching when the traveller is expected to pass through that point.
 *
 * No framework dependencies — pure domain code.
 */
public record TimedWaypoint(Coordinates coordinates, LocalDateTime estimatedArrival) {}
