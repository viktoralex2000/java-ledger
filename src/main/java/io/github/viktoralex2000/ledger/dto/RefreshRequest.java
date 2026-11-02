package io.github.viktoralex2000.ledger.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

        @NotBlank
        String refreshToken

) {
}
