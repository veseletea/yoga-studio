package com.yogastudio.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "ID-ul studentului este obligatoriu")
        Long studentId,

        @NotNull(message = "ID-ul clasei este obligatoriu")
        Long yogaClassId
) {}
