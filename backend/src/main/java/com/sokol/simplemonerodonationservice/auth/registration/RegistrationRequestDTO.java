package com.sokol.simplemonerodonationservice.auth.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequestDTO(
        @NotBlank(message = "Username should not be empty")
        @Size(min = 1, max = 16, message = "Max character length is 16")
        String username,
        @NotBlank(message = "Email should not be empty")
        @Email(message = "Provide a correct email (example@email.com)", regexp = ".+[@].+[\\.].+")
        @Size(min = 1, max = 254, message = "Max character length is 254")
        String email,
        @NotBlank(message = "Password should not be empty")
        @Size(min = 1, max = 64, message = "Max character length is 64")
        String password
) { }
