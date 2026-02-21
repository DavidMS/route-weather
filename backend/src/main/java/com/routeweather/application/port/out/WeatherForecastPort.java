package com.routeweather.application.port.out;

import com.routeweather.domain.model.TimedWaypoint;
import com.routeweather.domain.model.WeatherPoint;

import java.util.List;

/**
 * Outbound port (driven side): fetches weather forecasts from an external source.
 *
 * Implemented by: infrastructure/adapter/out/weather/OpenMeteoWeatherAdapter
 */
public interface WeatherForecastPort {

    /**
     * Return hourly weather forecasts for each timed waypoint.
     *
     * Each waypoint carries both the geographic position and the estimated arrival time,
     * so the adapter can fetch the forecast for the exact hour the traveller will be there.
     *
     * @param waypoints timed waypoints in route order
     * @return a WeatherPoint for each waypoint, in the same order
     */
    List<WeatherPoint> getForecast(List<TimedWaypoint> waypoints);
}
