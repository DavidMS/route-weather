package com.routeweather.infrastructure.adapter.in.rest.mapper;

import com.routeweather.application.port.in.RouteWeatherQuery;
import com.routeweather.domain.model.RouteWeatherReport;
import com.routeweather.domain.model.WeatherPoint;
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

    /**
     * Convert a validated REST request into an application query.
     * City name geocoding and Route construction happen inside the service.
     */
    public static RouteWeatherQuery toQuery(RouteRequest request) {
        return new RouteWeatherQuery(
                request.origin(),
                request.destination(),
                request.travelDate());
    }

    public static RouteWeatherResponse toResponse(RouteWeatherReport report) {
        List<WeatherPointResponse> points = report.getWeatherPoints().stream()
                .map(RouteWeatherMapper::toPointResponse)
                .toList();

        return new RouteWeatherResponse(
                report.getRoute().getOriginName(),
                report.getRoute().getDestinationName(),
                report.getRoute().getTravelDate(),
                points);
    }

    private static WeatherPointResponse toPointResponse(WeatherPoint point) {
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
