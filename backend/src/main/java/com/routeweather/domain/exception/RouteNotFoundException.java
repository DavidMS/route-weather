package com.routeweather.domain.exception;

/**
 * Thrown when a route cannot be resolved (e.g., unknown city name).
 */
public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String message) {
        super(message);
    }

    public RouteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
