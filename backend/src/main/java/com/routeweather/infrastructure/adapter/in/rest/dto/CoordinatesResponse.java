package com.routeweather.infrastructure.adapter.in.rest.dto;

/**
 * Outbound DTO: a single lat/lng point in the route geometry polyline.
 */
public record CoordinatesResponse(double latitude, double longitude) {}
