package com.routeweather.infrastructure.adapter.in.rest;

import com.routeweather.application.port.in.GetRouteWeatherUseCase;
import com.routeweather.application.port.in.RouteWeatherQuery;
import com.routeweather.domain.model.RouteWeatherReport;
import com.routeweather.infrastructure.adapter.in.rest.dto.RouteRequest;
import com.routeweather.infrastructure.adapter.in.rest.dto.RouteWeatherResponse;
import com.routeweather.infrastructure.adapter.in.rest.mapper.RouteWeatherMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST adapter (inbound): exposes the GetRouteWeatherUseCase over HTTP.
 *
 * POST /api/routes/weather
 *   Body: { "origin": "Madrid", "destination": "Barcelona", "travelDate": "2025-06-15" }
 *   Returns: RouteWeatherResponse with weather at each waypoint
 */
@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "${frontend.cors.origin:http://localhost:5173}")
public class RouteWeatherController {

    private final GetRouteWeatherUseCase getRouteWeatherUseCase;

    public RouteWeatherController(GetRouteWeatherUseCase getRouteWeatherUseCase) {
        this.getRouteWeatherUseCase = getRouteWeatherUseCase;
    }

    @PostMapping("/weather")
    public ResponseEntity<RouteWeatherResponse> getRouteWeather(
            @Valid @RequestBody RouteRequest request) {

        RouteWeatherQuery query = RouteWeatherMapper.toQuery(request);
        RouteWeatherReport report = getRouteWeatherUseCase.getWeatherForRoute(query);
        RouteWeatherResponse response = RouteWeatherMapper.toResponse(report);

        return ResponseEntity.ok(response);
    }
}
