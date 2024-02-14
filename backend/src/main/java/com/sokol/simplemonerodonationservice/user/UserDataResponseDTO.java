package com.sokol.simplemonerodonationservice.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserDataResponseDTO(
        @NotNull
        @NotEmpty
        String username,

        @NotNull
        @NotEmpty
        String email
) { }
