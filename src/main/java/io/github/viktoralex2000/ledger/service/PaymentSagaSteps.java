package io.github.viktoralex2000.ledger.service;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import io.github.viktoralex2000.ledger.dto.PaymentTransactionDto;
import io.github.viktoralex2000.ledger.exception.BadRequestException;
import io.github.viktoralex2000.ledger.exception.InsufficientFundsException;
import io.github.viktoralex2000.ledger.exception.ResourceNotFoundException;
import io.github.viktoralex2000.ledger.gateway.GatewayResponse;
import io.github.viktoralex2000.ledger.model.LedgerEntryEntity;
import io.github.viktoralex2000.ledger.model.PaymentTransactionEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import io.github.viktoralex2000.ledger.repository.PaymentTransactionRepository;
import io.github.viktoralex2000.ledger.repository.WalletRepository;

@Service
public class PaymentSagaSteps {

    private static final Logger log = LoggerFactory.getLogger(PaymentSagaSteps.class);

    private final PaymentTransactionRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final LedgerService ledgerService;
    private final OutboxService outboxService;

    public PaymentSagaSteps(PaymentTransactionRepository paymentRepository,
                            WalletRepository walletRepository,
                            LedgerService ledgerService,
                            OutboxService outboxService) {
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
        this.ledgerService = ledgerService;
        this.outboxService = outboxService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PaymentTransactionEntity initiate(WalletEntity wallet, PaymentTransactionEntity payment) {
        WalletEntity locked = walletRepository.findByIdForUpdate(wallet.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + wallet.getId()));
        if (locked.getStatus() != WalletEntity.WalletStatus.ACTIVE) {
            throw new BadRequestException("Wallet " + wallet.getId() + " is not active");
        }
        payment.setWallet(locked);
        paymentRepository.save(payment);

        if (payment.getType() == PaymentTransactionEntity.PaymentType.WITHDRAW) {
            if (locked.getBalance().compareTo(payment.getAmount()) < 0) {
                throw new InsufficientFundsException(
                        "Insufficient funds: required " + payment.getAmount() + " " + locked.getCurrency()
                                + ", available " + locked.getBalance() + " " + locked.getCurrency());
            }
            ledgerService.post(locked, LedgerEntryEntity.EntryType.DEBIT, payment.getAmount(),
                    "payment:" + payment.getId(),
                    "Withdrawal reservation to external bank account");
        }
        return payment;
    }

    @Transactional
    public PaymentTransactionEntity settle(Long paymentId, GatewayResponse gateway) {
        PaymentTransactionEntity payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        if (payment.getStatus() != PaymentTransactionEntity.PaymentStatus.PENDING) {
            return payment;
        }
        payment.setAttempts(payment.getAttempts() + 1);
        payment.setExternalPaymentId(gateway.id());

        if (payment.getType() == PaymentTransactionEntity.PaymentType.TOP_UP) {
            ledgerService.post(payment.getWallet(), LedgerEntryEntity.EntryType.CREDIT, payment.getAmount(),
                    "payment:" + payment.getId(), "Top-up via external gateway " + gateway.id());
        }
        payment.setStatus(PaymentTransactionEntity.PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        outboxService.emit("payment", String.valueOf(payment.getId()), "PAYMENT_COMPLETED", eventPayload(payment));
        log.info("Payment {} settled: {} {} {}", payment.getId(), payment.getAmount(), payment.getCurrency(),
                payment.getStatus());
        return payment;
    }

    @Transactional
    public PaymentTransactionEntity compensate(Long paymentId, Throwable failure) {
        PaymentTransactionEntity payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        if (payment.getStatus() != PaymentTransactionEntity.PaymentStatus.PENDING) {
            return payment;
        }
        payment.setAttempts(payment.getAttempts() + 1);
        payment.setFailureReason(failure.getMessage());

        if (payment.getType() == PaymentTransactionEntity.PaymentType.WITHDRAW) {
            ledgerService.post(payment.getWallet(), LedgerEntryEntity.EntryType.CREDIT, payment.getAmount(),
                    "payment:" + payment.getId() + ":refund",
                    "Compensating refund of failed withdrawal");
        }
        payment.setStatus(PaymentTransactionEntity.PaymentStatus.ROLLED_BACK);
        paymentRepository.save(payment);

        outboxService.emit("payment", String.valueOf(payment.getId()), "PAYMENT_FAILED", eventPayload(payment));
        log.warn("Payment {} rolled back: {}", payment.getId(), failure.getMessage());
        return payment;
    }

    @Transactional(readOnly = true)
    public PaymentTransactionDto getPayment(Long userId, Long paymentId) {
        return paymentRepository.findByIdAndUserId(paymentId, userId)
                .map(PaymentTransactionDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
    }

    @Transactional(readOnly = true)
    public java.util.List<PaymentTransactionDto> listPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PaymentTransactionDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<PaymentTransactionDto> listAllPayments() {
        return paymentRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 500))
                .map(PaymentTransactionDto::from).stream().toList();
    }

    private Map<String, Object> eventPayload(PaymentTransactionEntity payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentId", payment.getId());
        payload.put("userId", payment.getUser().getId());
        payload.put("walletId", payment.getWallet().getId());
        payload.put("type", payment.getType().name());
        payload.put("amount", payment.getAmount());
        payload.put("currency", payment.getCurrency());
        payload.put("status", payment.getStatus().name());
        payload.put("externalPaymentId", payment.getExternalPaymentId());
        return payload;
    }
}
