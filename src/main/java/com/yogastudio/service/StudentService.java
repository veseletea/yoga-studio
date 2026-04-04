package com.yogastudio.service;

import com.yogastudio.dto.StudentRequest;
import com.yogastudio.dto.StudentResponse;
import com.yogastudio.entity.Student;
import com.yogastudio.exception.DuplicateResourceException;
import com.yogastudio.exception.ResourceNotFoundException;
import com.yogastudio.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(StudentResponse::from)
                .toList();
    }

    public StudentResponse findById(Long id) {
        return studentRepository.findById(id)
                .map(StudentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Student cu id " + id + " nu a fost găsit"));
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email-ul " + request.email() + " este deja folosit");
        }

        var student = new Student(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                LocalDate.now()
        );

        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student cu id " + id + " nu a fost găsit"));

        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setPhone(request.phone());

        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student cu id " + id + " nu a fost găsit");
        }
        studentRepository.deleteById(id);
    }
}
