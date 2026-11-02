package io.github.viktoralex2000.ledger.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ExchangeRateUpdateRequest(

		@NotBlank 
		@Pattern(regexp = "[A-Z]{3}")
        BigDecimal rate,

        @NotBlank 
        @Pattern(regexp = "[A-Z]{3}")
        String baseCurrency,

        @NotNull 
        @DecimalMin(value = "0.000000000001", message = "Positive only")
        String quoteCurrency

) {
}
