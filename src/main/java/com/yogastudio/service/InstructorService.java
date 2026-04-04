package com.yogastudio.service;

import com.yogastudio.dto.InstructorRequest;
import com.yogastudio.dto.InstructorResponse;
import com.yogastudio.entity.Instructor;
import com.yogastudio.exception.DuplicateResourceException;
import com.yogastudio.exception.ResourceNotFoundException;
import com.yogastudio.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public List<InstructorResponse> findAll() {
        return instructorRepository.findAll().stream()
                .map(InstructorResponse::from)
                .toList();
    }

    public InstructorResponse findById(Long id) {
        return instructorRepository.findById(id)
                .map(InstructorResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor cu id " + id + " nu a fost găsit"));
    }

    @Transactional
    public InstructorResponse create(InstructorRequest request) {
        if (instructorRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email-ul " + request.email() + " este deja folosit");
        }

        var instructor = new Instructor(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.specialization()
        );

        return InstructorResponse.from(instructorRepository.save(instructor));
    }

    @Transactional
    public InstructorResponse update(Long id, InstructorRequest request) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor cu id " + id + " nu a fost găsit"));

        instructor.setFirstName(request.firstName());
        instructor.setLastName(request.lastName());
        instructor.setEmail(request.email());
        instructor.setPhone(request.phone());
        instructor.setSpecialization(request.specialization());

        return InstructorResponse.from(instructorRepository.save(instructor));
    }

    @Transactional
    public void delete(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor cu id " + id + " nu a fost găsit");
        }
        instructorRepository.deleteById(id);
    }
}
