package com.routeweather.infrastructure.config;

import com.routeweather.application.port.in.GetRouteWeatherUseCase;
import com.routeweather.application.port.out.RouteCalculatorPort;
import com.routeweather.application.port.out.WeatherForecastPort;
import com.routeweather.application.service.RouteWeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Central wiring configuration.
 *
 * This is the ONLY place where Spring is allowed to know about application
 * service classes. Domain and application services remain annotation-free;
 * Spring dependency injection is handled here exclusively.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GetRouteWeatherUseCase getRouteWeatherUseCase(
            RouteCalculatorPort routeCalculatorPort,
            WeatherForecastPort weatherForecastPort) {
        return new RouteWeatherService(routeCalculatorPort, weatherForecastPort);
    }
}
