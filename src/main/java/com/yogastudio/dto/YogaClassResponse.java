package com.yogastudio.dto;

import com.yogastudio.entity.YogaClass;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record YogaClassResponse(
        Long id,
        String name,
        String description,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        int durationMinutes,
        int maxCapacity,
        int currentBookings,
        InstructorResponse instructor
) {
    public static YogaClassResponse from(YogaClass yogaClass) {
        return new YogaClassResponse(
                yogaClass.getId(),
                yogaClass.getName(),
                yogaClass.getDescription(),
                yogaClass.getDayOfWeek(),
                yogaClass.getStartTime(),
                yogaClass.getDurationMinutes(),
                yogaClass.getMaxCapacity(),
                yogaClass.getBookings().size(),
                InstructorResponse.from(yogaClass.getInstructor())
        );
    }
}
