package io.github.viktoralex2000.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateWalletRequest(

		
        @Pattern(regexp = "[A-Z]{3}", message = "At least 3 letters")
        
        String currency

) {
}
