package io.github.viktoralex2000.ledger.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record TopUpRequest(

		@NotNull
        @DecimalMin(value = "0.01", message = "Amount at least 0.01")
        @DecimalMax(value = "1000000", message = "Amount too large")
        @Digits(integer = 10, fraction = 2)
        BigDecimal amount,

        String cardToken

) {
}
