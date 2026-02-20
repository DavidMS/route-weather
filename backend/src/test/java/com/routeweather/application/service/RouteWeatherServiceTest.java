package com.routeweather.application.service;

import com.routeweather.application.port.in.RouteWeatherQuery;
import com.routeweather.application.port.out.RouteCalculatorPort;
import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit test for the application service.
 * No Spring context — pure unit test with mocked ports.
 */
@ExtendWith(MockitoExtension.class)
class RouteWeatherServiceTest {

    @Mock
    private RouteCalculatorPort routeCalculatorPort;

    @Mock
    private WeatherForecastPort weatherForecastPort;

    private RouteWeatherService service;

    @BeforeEach
    void setUp() {
        service = new RouteWeatherService(routeCalculatorPort, weatherForecastPort);
    }

    @Test
    void getWeatherForRoute_returnsReportWithWeatherPoints() {
        // Given
        Coordinates madrid = new Coordinates(40.4168, -3.7038);
        Coordinates barcelona = new Coordinates(41.3851, 2.1734);
        LocalDate travelDate = LocalDate.of(2025, 6, 15);

        RouteWeatherQuery query = new RouteWeatherQuery("Madrid", "Barcelona", travelDate);

        List<Coordinates> waypoints = List.of(madrid, barcelona);
        WeatherPoint madridWeather = new WeatherPoint(
                madrid, LocalDateTime.of(2025, 6, 15, 12, 0),
                25.0, 0.0, 10, WeatherCondition.CLEAR);
        WeatherPoint barcelonaWeather = new WeatherPoint(
                barcelona, LocalDateTime.of(2025, 6, 15, 12, 0),
                28.0, 0.0, 8, WeatherCondition.PARTLY_CLOUDY);

        when(routeCalculatorPort.geocode("Madrid")).thenReturn(madrid);
        when(routeCalculatorPort.geocode("Barcelona")).thenReturn(barcelona);
        when(routeCalculatorPort.calculateWaypoints(madrid, barcelona)).thenReturn(waypoints);
        when(weatherForecastPort.getForecast(waypoints, travelDate))
                .thenReturn(List.of(madridWeather, barcelonaWeather));

        // When
        RouteWeatherReport report = service.getWeatherForRoute(query);

        // Then
        assertThat(report.getRoute().getOriginName()).isEqualTo("Madrid");
        assertThat(report.getRoute().getDestinationName()).isEqualTo("Barcelona");
        assertThat(report.getWeatherPoints()).hasSize(2);
        assertThat(report.getWeatherPoints().get(0).condition()).isEqualTo(WeatherCondition.CLEAR);
        assertThat(report.getWeatherPoints().get(1).condition()).isEqualTo(WeatherCondition.PARTLY_CLOUDY);
    }
}
