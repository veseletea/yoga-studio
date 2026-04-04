package com.yogastudio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InstructorRequest(
        @NotBlank(message = "Prenumele este obligatoriu")
        String firstName,

        @NotBlank(message = "Numele este obligatoriu")
        String lastName,

        @NotBlank(message = "Email-ul este obligatoriu")
        @Email(message = "Email invalid")
        String email,

        String phone,
        String specialization
) {}
