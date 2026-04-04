package com.yogastudio.service;

import com.yogastudio.dto.YogaClassRequest;
import com.yogastudio.dto.YogaClassResponse;
import com.yogastudio.entity.Instructor;
import com.yogastudio.entity.YogaClass;
import com.yogastudio.exception.ResourceNotFoundException;
import com.yogastudio.repository.InstructorRepository;
import com.yogastudio.repository.YogaClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class YogaClassService {

    private final YogaClassRepository yogaClassRepository;
    private final InstructorRepository instructorRepository;

    public YogaClassService(YogaClassRepository yogaClassRepository, InstructorRepository instructorRepository) {
        this.yogaClassRepository = yogaClassRepository;
        this.instructorRepository = instructorRepository;
    }

    public List<YogaClassResponse> findAll() {
        return yogaClassRepository.findAll().stream()
                .map(YogaClassResponse::from)
                .toList();
    }

    public YogaClassResponse findById(Long id) {
        return yogaClassRepository.findById(id)
                .map(YogaClassResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Clasa cu id " + id + " nu a fost găsită"));
    }

    public List<YogaClassResponse> findByDay(DayOfWeek day) {
        return yogaClassRepository.findByDayOfWeek(day).stream()
                .map(YogaClassResponse::from)
                .toList();
    }

    @Transactional
    public YogaClassResponse create(YogaClassRequest request) {
        Instructor instructor = instructorRepository.findById(request.instructorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor cu id " + request.instructorId() + " nu a fost găsit"));

        var yogaClass = new YogaClass();
        yogaClass.setName(request.name());
        yogaClass.setDescription(request.description());
        yogaClass.setDayOfWeek(request.dayOfWeek());
        yogaClass.setStartTime(request.startTime());
        yogaClass.setDurationMinutes(request.durationMinutes());
        yogaClass.setMaxCapacity(request.maxCapacity());
        yogaClass.setInstructor(instructor);

        return YogaClassResponse.from(yogaClassRepository.save(yogaClass));
    }

    @Transactional
    public YogaClassResponse update(Long id, YogaClassRequest request) {
        YogaClass yogaClass = yogaClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clasa cu id " + id + " nu a fost găsită"));

        Instructor instructor = instructorRepository.findById(request.instructorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor cu id " + request.instructorId() + " nu a fost găsit"));

        yogaClass.setName(request.name());
        yogaClass.setDescription(request.description());
        yogaClass.setDayOfWeek(request.dayOfWeek());
        yogaClass.setStartTime(request.startTime());
        yogaClass.setDurationMinutes(request.durationMinutes());
        yogaClass.setMaxCapacity(request.maxCapacity());
        yogaClass.setInstructor(instructor);

        return YogaClassResponse.from(yogaClassRepository.save(yogaClass));
    }

    @Transactional
    public void delete(Long id) {
        if (!yogaClassRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clasa cu id " + id + " nu a fost găsită");
        }
        yogaClassRepository.deleteById(id);
    }
}
