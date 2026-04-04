package com.yogastudio.dto;

import com.yogastudio.entity.Booking;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        String studentName,
        String className,
        LocalDateTime bookedAt,
        Booking.BookingStatus status
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStudent().getFirstName() + " " + booking.getStudent().getLastName(),
                booking.getYogaClass().getName(),
                booking.getBookedAt(),
                booking.getStatus()
        );
    }
}
