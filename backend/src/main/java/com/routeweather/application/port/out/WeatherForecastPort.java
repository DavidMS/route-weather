package com.routeweather.application.port.out;

import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.WeatherPoint;

import java.time.LocalDate;
import java.util.List;

/**
 * Outbound port (driven side): fetches weather forecasts from an external source.
 *
 * Implemented by: infrastructure/adapter/out/weather/OpenMeteoWeatherAdapter
 */
public interface WeatherForecastPort {

    /**
     * Return weather forecasts for each of the given waypoints on the specified date.
     *
     * @param waypoints the list of geographic points to query
     * @param date      the travel date (forecasts are daily)
     * @return a WeatherPoint for each waypoint, in the same order
     */
    List<WeatherPoint> getForecast(List<Coordinates> waypoints, LocalDate date);
}
