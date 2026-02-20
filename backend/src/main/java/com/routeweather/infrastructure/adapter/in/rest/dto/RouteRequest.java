package com.routeweather.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Inbound DTO: the JSON body the client sends when requesting a route weather report.
 * Stays in the infrastructure layer — never passed to domain or application services.
 */
public record RouteRequest(

        @NotBlank(message = "origin is required")
        String origin,

        @NotBlank(message = "destination is required")
        String destination,

        @NotNull(message = "travelDate is required")
        @FutureOrPresent(message = "travelDate must be today or in the future")
        LocalDate travelDate
) {}
