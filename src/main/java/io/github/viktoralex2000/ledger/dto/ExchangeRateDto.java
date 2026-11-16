package io.github.viktoralex2000.ledger.dto;

import io.github.viktoralex2000.ledger.model.ExchangeRateEntity;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRateDto(
        BigDecimal rate,
        String baseCurrency,
        String quoteCurrency,
        Instant updatedAt
) {
    public static ExchangeRateDto from(ExchangeRateEntity exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getRate(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getQuoteCurrency(),
                exchangeRate.getUpdatedAt()
        );
    }
}
