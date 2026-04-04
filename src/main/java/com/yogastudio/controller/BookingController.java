package com.yogastudio.controller;

import com.yogastudio.dto.BookingRequest;
import com.yogastudio.dto.BookingResponse;
import com.yogastudio.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> findAll() {
        return bookingService.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<BookingResponse> findByStudent(@PathVariable Long studentId) {
        return bookingService.findByStudent(studentId);
    }

    @GetMapping("/class/{classId}")
    public List<BookingResponse> findByClass(@PathVariable Long classId) {
        return bookingService.findByYogaClass(classId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody BookingRequest request) {
        return bookingService.create(request);
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return bookingService.cancel(id);
    }
}
