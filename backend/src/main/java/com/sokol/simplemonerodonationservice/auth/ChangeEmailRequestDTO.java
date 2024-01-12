package com.sokol.simplemonerodonationservice.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeEmailRequestDTO(
        @NotBlank(message = "Email should not be empty")
        @Email(message = "Provide a correct email (example@email.com)", regexp = ".+[@].+[\\.].+")
        @Size(min = 1, max = 254, message = "Max character length is 254")
        String newEmail,
        @NotBlank(message = "Email should not be empty")
        @Email(message = "Provide a correct email (example@email.com)", regexp = ".+[@].+[\\.].+")
        @Size(min = 1, max = 254, message = "Max character length is 254")
        String repeatNewEmail
) { }
