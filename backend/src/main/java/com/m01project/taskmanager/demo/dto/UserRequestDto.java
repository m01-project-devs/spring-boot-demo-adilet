package com.m01project.taskmanager.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is mandatory")
        String email,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
        String password,

        @NotBlank(message = "First name is mandatory")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        String lastName,

        @NotBlank(message = "Phone number is mandatory")
        @Pattern(regexp = "\\d{4,17}", message = "Phone number must be 7 to 15 digits")
        String phoneNumber
) {
}
