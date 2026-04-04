package com.yogastudio.dto;

import com.yogastudio.entity.Instructor;

public record InstructorResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String specialization
) {
    public static InstructorResponse from(Instructor instructor) {
        return new InstructorResponse(
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getEmail(),
                instructor.getPhone(),
                instructor.getSpecialization()
        );
    }
}
