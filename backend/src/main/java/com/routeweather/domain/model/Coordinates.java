package com.routeweather.domain.model;

/**
 * Value object representing a geographic position.
 * Self-validating: throws IllegalArgumentException on invalid ranges.
 *
 * No framework dependencies — pure domain code.
 */
public record Coordinates(double latitude, double longitude) {

    public Coordinates {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90, got: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180, got: " + longitude);
        }
    }
}
