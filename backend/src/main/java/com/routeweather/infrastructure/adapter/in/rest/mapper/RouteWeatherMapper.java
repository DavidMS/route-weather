package com.routeweather.infrastructure.adapter.in.rest.mapper;

import com.routeweather.application.port.in.RouteWeatherQuery;
import com.routeweather.domain.model.Coordinates;
import com.routeweather.domain.model.RouteWeatherReport;
import com.routeweather.domain.model.WeatherPoint;
import com.routeweather.infrastructure.adapter.in.rest.dto.CoordinatesResponse;
import com.routeweather.infrastructure.adapter.in.rest.dto.RouteRequest;
import com.routeweather.infrastructure.adapter.in.rest.dto.RouteWeatherResponse;
import com.routeweather.infrastructure.adapter.in.rest.dto.WeatherPointResponse;

import java.util.List;

/**
 * Maps between REST DTOs and application/domain objects.
 * Lives exclusively in the infrastructure layer.
 */
public class RouteWeatherMapper {

    private RouteWeatherMapper() {}

    public static RouteWeatherQuery toQuery(RouteRequest request) {
        return new RouteWeatherQuery(
                request.origin(),
                request.destination(),
                request.travelDate(),
                request.departureTime());
    }

    public static RouteWeatherResponse toResponse(RouteWeatherReport report) {
        List<WeatherPointResponse> weatherPoints = report.getWeatherPoints().stream()
                .map(RouteWeatherMapper::toWeatherPointResponse)
                .toList();

        List<CoordinatesResponse> geometry = report.getRouteGeometry().stream()
                .map(c -> new CoordinatesResponse(c.latitude(), c.longitude()))
                .toList();

        return new RouteWeatherResponse(
                report.getRoute().getOriginName(),
                report.getRoute().getDestinationName(),
                report.getRoute().getTravelDate(),
                weatherPoints,
                geometry);
    }

    private static WeatherPointResponse toWeatherPointResponse(WeatherPoint point) {
        return new WeatherPointResponse(
                point.coordinates().latitude(),
                point.coordinates().longitude(),
                point.forecastTime(),
                point.temperatureCelsius(),
                point.precipitationMm(),
                point.windSpeedKmh(),
                point.condition().name());
    }
}
