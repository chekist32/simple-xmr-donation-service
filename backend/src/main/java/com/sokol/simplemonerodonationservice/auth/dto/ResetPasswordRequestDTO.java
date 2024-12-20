package com.sokol.simplemonerodonationservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "Email should not be empty")
        @Email(message = "Provide a correct email (example@email.com)", regexp = ".+[@].+[\\.].+")
        @Size(min = 1, max = 254, message = "Max character length is 254")
        String email
) { }
