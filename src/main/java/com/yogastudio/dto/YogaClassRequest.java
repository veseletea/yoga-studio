package com.yogastudio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record YogaClassRequest(
        @NotBlank(message = "Class name is required")
        String name,

        String description,

        @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @Min(value = 15, message = "Minimum duration is 15 minutes")
        int durationMinutes,

        @Min(value = 1, message = "Minimum capacity is 1")
        int maxCapacity,

        @NotNull(message = "Instructor is required")
        Long instructorId
) {}
