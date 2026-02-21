package com.routeweather.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Domain entity representing a travel route.
 *
 * No framework dependencies — pure domain code.
 */
public class Route {

    private final String originName;
    private final String destinationName;
    private final Coordinates originCoordinates;
    private final Coordinates destinationCoordinates;
    private final LocalDate travelDate;
    private final LocalTime departureTime;

    public Route(
            String originName,
            String destinationName,
            Coordinates originCoordinates,
            Coordinates destinationCoordinates,
            LocalDate travelDate,
            LocalTime departureTime) {

        this.originName = Objects.requireNonNull(originName, "originName is required");
        this.destinationName = Objects.requireNonNull(destinationName, "destinationName is required");
        this.originCoordinates = Objects.requireNonNull(originCoordinates, "originCoordinates is required");
        this.destinationCoordinates = Objects.requireNonNull(destinationCoordinates, "destinationCoordinates is required");
        this.travelDate = Objects.requireNonNull(travelDate, "travelDate is required");
        this.departureTime = Objects.requireNonNull(departureTime, "departureTime is required");
    }

    public String getOriginName() { return originName; }
    public String getDestinationName() { return destinationName; }
    public Coordinates getOriginCoordinates() { return originCoordinates; }
    public Coordinates getDestinationCoordinates() { return destinationCoordinates; }
    public LocalDate getTravelDate() { return travelDate; }
    public LocalTime getDepartureTime() { return departureTime; }
}
