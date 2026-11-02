package io.github.viktoralex2000.ledger.dto;

import io.github.viktoralex2000.ledger.model.WalletEntity;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletDto(
        Long id,
        String currency,
        String status,
        BigDecimal balance,
        Instant createdAt
) {
    public static WalletDto of(WalletEntity wallet, BigDecimal balance) {
        return new WalletDto(
                wallet.getId(),
                wallet.getCurrency(),
                wallet.getStatus() != null ? wallet.getStatus().name() : null,
                balance,
                wallet.getCreatedAt()
        );
    }
}
