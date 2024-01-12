package com.sokol.simplemonerodonationservice.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
        @NotBlank(message = "Password should not be empty")
        @Size(min = 1, max = 64, message = "Max character length is 64")
        String newPassword,
        @NotBlank(message = "Password should not be empty")
        @Size(min = 1, max = 64, message = "Max character length is 64")
        String repeatNewPassword
) { }
