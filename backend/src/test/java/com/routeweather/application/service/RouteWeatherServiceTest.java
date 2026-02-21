package com.routeweather.application.service;

import com.routeweather.application.port.in.RouteWeatherQuery;
import com.routeweather.application.port.out.RouteCalculatorPort;
import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteWeatherServiceTest {

    @Mock private RouteCalculatorPort routeCalculatorPort;
    @Mock private WeatherForecastPort weatherForecastPort;

    private RouteWeatherService service;

    @BeforeEach
    void setUp() {
        service = new RouteWeatherService(routeCalculatorPort, weatherForecastPort);
    }

    @Test
    void getWeatherForRoute_returnsReportWithWeatherPointsAndGeometry() {
        Coordinates madrid    = new Coordinates(40.4168, -3.7038);
        Coordinates midpoint  = new Coordinates(41.0, -1.5);
        Coordinates barcelona = new Coordinates(41.3851, 2.1734);
        LocalDate travelDate  = LocalDate.of(2026, 3, 1);
        LocalTime departure   = LocalTime.of(8, 0);

        RouteWeatherQuery query = new RouteWeatherQuery("Madrid", "Barcelona", travelDate, departure);

        List<Coordinates> geometry         = List.of(madrid, midpoint, barcelona);
        List<Coordinates> weatherWaypoints = List.of(madrid, barcelona);
        double totalDurationSeconds        = 3600.0; // 1 hour
        RouteDetails routeDetails = new RouteDetails(geometry, weatherWaypoints, totalDurationSeconds);

        WeatherPoint madridWeather    = new WeatherPoint(madrid,    LocalDateTime.of(2026, 3, 1,  8, 0), 12.0, 0.0, 10, WeatherCondition.CLEAR);
        WeatherPoint barcelonaWeather = new WeatherPoint(barcelona, LocalDateTime.of(2026, 3, 1,  9, 0), 14.0, 0.0,  8, WeatherCondition.PARTLY_CLOUDY);

        when(routeCalculatorPort.geocode("Madrid")).thenReturn(madrid);
        when(routeCalculatorPort.geocode("Barcelona")).thenReturn(barcelona);
        when(routeCalculatorPort.calculateRoute(madrid, barcelona)).thenReturn(routeDetails);
        when(weatherForecastPort.getForecast(any())).thenReturn(List.of(madridWeather, barcelonaWeather));

        RouteWeatherReport report = service.getWeatherForRoute(query);

        assertThat(report.getRoute().getOriginName()).isEqualTo("Madrid");
        assertThat(report.getWeatherPoints()).hasSize(2);
        assertThat(report.getWeatherPoints().get(0).condition()).isEqualTo(WeatherCondition.CLEAR);
        assertThat(report.getRouteGeometry()).hasSize(3);
        assertThat(report.getRouteGeometry().get(1)).isEqualTo(midpoint);
    }

    @Test
    void getWeatherForRoute_computesArrivalTimesProportionallyAlongRoute() {
        Coordinates origin      = new Coordinates(40.4168, -3.7038);
        Coordinates destination = new Coordinates(41.3851, 2.1734);
        LocalDate travelDate    = LocalDate.of(2026, 3, 1);
        LocalTime departure     = LocalTime.of(9, 0);

        RouteWeatherQuery query = new RouteWeatherQuery("Madrid", "Barcelona", travelDate, departure);

        double totalDurationSeconds = 7200.0; // 2 hours
        RouteDetails routeDetails = new RouteDetails(
                List.of(origin, destination),
                List.of(origin, destination),
                totalDurationSeconds);

        when(routeCalculatorPort.geocode("Madrid")).thenReturn(origin);
        when(routeCalculatorPort.geocode("Barcelona")).thenReturn(destination);
        when(routeCalculatorPort.calculateRoute(origin, destination)).thenReturn(routeDetails);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TimedWaypoint>> captor = ArgumentCaptor.forClass(List.class);
        when(weatherForecastPort.getForecast(captor.capture())).thenReturn(
                List.of(
                        new WeatherPoint(origin,      LocalDateTime.of(2026, 3, 1,  9, 0), 10.0, 0.0, 5, WeatherCondition.CLEAR),
                        new WeatherPoint(destination, LocalDateTime.of(2026, 3, 1, 11, 0), 15.0, 0.0, 5, WeatherCondition.CLEAR)
                )
        );

        service.getWeatherForRoute(query);

        List<TimedWaypoint> timedWaypoints = captor.getValue();
        assertThat(timedWaypoints).hasSize(2);

        // Origin gets the departure time exactly
        assertThat(timedWaypoints.get(0).estimatedArrival())
                .isEqualTo(LocalDateTime.of(2026, 3, 1, 9, 0));

        // Destination gets departure + totalDuration (2 hours later)
        assertThat(timedWaypoints.get(1).estimatedArrival())
                .isEqualTo(LocalDateTime.of(2026, 3, 1, 11, 0));
    }
}
