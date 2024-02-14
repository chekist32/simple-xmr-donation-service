package com.sokol.simplemonerodonationservice.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(
        @NotBlank(message = "Username should not be empty")
        @Size(min = 1, max = 254, message = "Max character length is 254")
        String principal,

        @NotBlank(message = "Password should not be empty")
        @Size(min = 1, max = 64, message = "Max character length is 64")
        String password
) { }
