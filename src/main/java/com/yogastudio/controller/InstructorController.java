package com.yogastudio.controller;

import com.yogastudio.dto.InstructorRequest;
import com.yogastudio.dto.InstructorResponse;
import com.yogastudio.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public List<InstructorResponse> findAll() {
        return instructorService.findAll();
    }

    @GetMapping("/{id}")
    public InstructorResponse findById(@PathVariable Long id) {
        return instructorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstructorResponse create(@Valid @RequestBody InstructorRequest request) {
        return instructorService.create(request);
    }

    @PutMapping("/{id}")
    public InstructorResponse update(@PathVariable Long id, @Valid @RequestBody InstructorRequest request) {
        return instructorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        instructorService.delete(id);
    }
}
