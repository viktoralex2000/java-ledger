package io.github.viktoralex2000.ledger.dto;

import io.github.viktoralex2000.ledger.model.TransferEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferDto(
        Long id,
        Long fromWalletId,
        Long toWalletId,
        String currency,
        BigDecimal amount,
        BigDecimal convertedAmount,
        BigDecimal fxRate,
        BigDecimal fee,
        String status,
        String errorMessage,
        Instant createdAt,
        Instant completedAt
) {
    public static TransferDto from(TransferEntity transfer) {
        WalletEntity fromWalletEntity = transfer.getFromWallet();
        Long fromWalletId = (fromWalletEntity != null) ? fromWalletEntity.getId() : null;
        WalletEntity toWalletEntity = transfer.getToWallet();
        Long toWalletId = (toWalletEntity != null) ? toWalletEntity.getId() : null;
        return new TransferDto(
                transfer.getId(),
                fromWalletId,
                toWalletId,
                transfer.getCurrency(),
                transfer.getAmount(),
                transfer.getConvertedAmount(),
                transfer.getFxRate(),
                transfer.getFee(),
                transfer.getStatus().name(),
                transfer.getErrorMessage(),
                transfer.getCreatedAt(),
                transfer.getCompletedAt()
        );
    }
}
