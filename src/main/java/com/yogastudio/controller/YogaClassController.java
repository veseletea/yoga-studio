package com.yogastudio.controller;

import com.yogastudio.dto.YogaClassRequest;
import com.yogastudio.dto.YogaClassResponse;
import com.yogastudio.service.YogaClassService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class YogaClassController {

    private final YogaClassService yogaClassService;

    public YogaClassController(YogaClassService yogaClassService) {
        this.yogaClassService = yogaClassService;
    }

    @GetMapping
    public List<YogaClassResponse> findAll() {
        return yogaClassService.findAll();
    }

    @GetMapping("/{id}")
    public YogaClassResponse findById(@PathVariable Long id) {
        return yogaClassService.findById(id);
    }

    @GetMapping("/day/{day}")
    public List<YogaClassResponse> findByDay(@PathVariable DayOfWeek day) {
        return yogaClassService.findByDay(day);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public YogaClassResponse create(@Valid @RequestBody YogaClassRequest request) {
        return yogaClassService.create(request);
    }

    @PutMapping("/{id}")
    public YogaClassResponse update(@PathVariable Long id, @Valid @RequestBody YogaClassRequest request) {
        return yogaClassService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        yogaClassService.delete(id);
    }
}
