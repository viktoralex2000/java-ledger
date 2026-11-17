package io.github.viktoralex2000.ledger.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        String firstName,

        String lastName,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password

) {
}
