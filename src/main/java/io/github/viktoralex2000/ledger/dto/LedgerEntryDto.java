package io.github.viktoralex2000.ledger.dto;

import io.github.viktoralex2000.ledger.model.LedgerEntryEntity;
import io.github.viktoralex2000.ledger.model.PaymentTransactionEntity;
import io.github.viktoralex2000.ledger.model.TransferEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public record LedgerEntryDto(
        Long id,
        Long walletId,
        Long transferId,
        Long paymentId,
        String entryType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String currency,
        String description,
        String operationKey,
        Instant createdAt
) {
    public static LedgerEntryDto from(LedgerEntryEntity ledgerEntry) {
        return new LedgerEntryDto(
                ledgerEntry.getId(),
                Optional.ofNullable(ledgerEntry.getWallet()).map(WalletEntity::getId).orElse(null),
                Optional.ofNullable(ledgerEntry.getTransfer()).map(TransferEntity::getId).orElse(null),
                Optional.ofNullable(ledgerEntry.getPayment()).map(PaymentTransactionEntity::getId).orElse(null),
                ledgerEntry.getEntryType().name(),
                ledgerEntry.getAmount(),
                ledgerEntry.getBalanceAfter(),
                ledgerEntry.getCurrency(),
                ledgerEntry.getDescription(),
                ledgerEntry.getOperationKey(),
                ledgerEntry.getCreatedAt()
        );
    }
}
