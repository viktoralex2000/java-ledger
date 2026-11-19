package io.github.viktoralex2000.ledger.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ExchangeRateUpdateRequest(

        
        @DecimalMin(value = "0.000000000001", message = "Positive only")
        BigDecimal rate,

        
        @Pattern(regexp = "[A-Z]{3}")
        String baseCurrency,

        
        @Pattern(regexp = "[A-Z]{3}")
        String quoteCurrency
        //Коммит тут
) {
}
