package com.sokol.simplemonerodonationservice.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserDataResponseDTO(
        String username,

        String email
) { }
