package com.yogastudio.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Class ID is required")
        Long yogaClassId
) {}
