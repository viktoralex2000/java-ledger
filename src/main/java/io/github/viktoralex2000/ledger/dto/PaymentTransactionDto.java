package io.github.viktoralex2000.ledger.dto;

import io.github.viktoralex2000.ledger.model.PaymentTransactionEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public record PaymentTransactionDto(
        Long id,
        Long walletId,
        String type,
        BigDecimal amount,
        String currency,
        String status,
        String externalPayment,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
    public static PaymentTransactionDto from(PaymentTransactionEntity paymentTransaction) {
        return new PaymentTransactionDto(
                paymentTransaction.getId(),
                Optional.ofNullable(paymentTransaction.getWallet())
                        .map(WalletEntity::getId).orElse(null),
                paymentTransaction.getType().name(),
                paymentTransaction.getAmount(),
                paymentTransaction.getCurrency(),
                paymentTransaction.getStatus().name(),
                paymentTransaction.getExternalPaymentId(),
                paymentTransaction.getFailureReason(),
                paymentTransaction.getCreatedAt(),
                paymentTransaction.getUpdatedAt()
        );
    }

}
