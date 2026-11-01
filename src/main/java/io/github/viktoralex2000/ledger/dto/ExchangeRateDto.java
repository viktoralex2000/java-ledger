package io.github.viktoralex2000.ledger.dto;

import io.github.viktoralex2000.ledger.model.ExchangeRateEntity;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRateDto(
        String baseCurrency,
        String quoteCurrency,
        BigDecimal rate,
        Instant updatedAt
) {
    public static ExchangeRateDto from(ExchangeRateEntity exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getQuoteCurrency(),
                exchangeRate.getRate(),
                exchangeRate.getUpdatedAt()
        );
    }
}
