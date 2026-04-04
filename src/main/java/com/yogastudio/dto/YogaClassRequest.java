package com.yogastudio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record YogaClassRequest(
        @NotBlank(message = "Numele clasei este obligatoriu")
        String name,

        String description,

        @NotNull(message = "Ziua este obligatorie")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Ora de start este obligatorie")
        LocalTime startTime,

        @Min(value = 15, message = "Durata minimă este 15 minute")
        int durationMinutes,

        @Min(value = 1, message = "Capacitatea minimă este 1")
        int maxCapacity,

        @NotNull(message = "Instructorul este obligatoriu")
        Long instructorId
) {}
