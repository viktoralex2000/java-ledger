package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.dto.PaymentTransactionDto;
import io.github.viktoralex2000.ledger.dto.TopUpRequest;
import io.github.viktoralex2000.ledger.dto.WithdrawRequest;
import io.github.viktoralex2000.ledger.exception.ResourceNotFoundException;
import io.github.viktoralex2000.ledger.gateway.GatewayResponse;
import io.github.viktoralex2000.ledger.gateway.MockGatewayClient;
import io.github.viktoralex2000.ledger.model.PaymentTransactionEntity;
import io.github.viktoralex2000.ledger.model.UserEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import io.github.viktoralex2000.ledger.repository.WalletRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentSagaService {

    private static final Logger log = LoggerFactory.getLogger(PaymentSagaService.class);

    private final WalletRepository walletRepository;
    private final MockGatewayClient gatewayClient;
    private final PaymentSagaSteps steps;

    public PaymentSagaService(WalletRepository walletRepository,
                              MockGatewayClient gatewayClient,
                              PaymentSagaSteps steps) {
        this.walletRepository = walletRepository;
        this.gatewayClient = gatewayClient;
        this.steps = steps;
    }

    public PaymentTransactionDto topUp(UserEntity user, Long walletId, TopUpRequest request, String idempotencyKey) {
        return runTopUp(user, walletId, request.amount(), idempotencyKey);
    }

    public PaymentTransactionDto withdraw(UserEntity user, Long walletId, WithdrawRequest request, String idempotencyKey) {
        return runWithdraw(user, walletId, request.amount(), idempotencyKey);
    }

    private PaymentTransactionDto runTopUp(UserEntity user, Long walletId, BigDecimal amount, String idempotencyKey) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        if (!wallet.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Wallet not found with id: " + walletId);
        }

        PaymentTransactionEntity payment = new PaymentTransactionEntity();
        payment.setIdempotencyKey(idempotencyKey);
        payment.setUser(user);
        payment.setWallet(wallet);
        payment.setType(PaymentTransactionEntity.PaymentType.TOP_UP);
        payment.setAmount(amount);
        payment.setCurrency(wallet.getCurrency());
        payment.setStatus(PaymentTransactionEntity.PaymentStatus.PENDING);

        PaymentTransactionEntity initiated = steps.initiate(wallet, payment);
        try {
            GatewayResponse gateway = gatewayClient.charge(initiated.getCurrency(),
                    "topup-" + initiated.getId(), amount);
            return PaymentTransactionDto.from(steps.settle(initiated.getId(), gateway));
        } catch (Exception e) {
            return PaymentTransactionDto.from(steps.compensate(initiated.getId(), e));
        }
    }

    private PaymentTransactionDto runWithdraw(UserEntity user, Long walletId, BigDecimal amount, String idempotencyKey) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        if (!wallet.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Wallet not found with id: " + walletId);
        }

        PaymentTransactionEntity payment = new PaymentTransactionEntity();
        payment.setIdempotencyKey(idempotencyKey);
        payment.setUser(user);
        payment.setWallet(wallet);
        payment.setType(PaymentTransactionEntity.PaymentType.WITHDRAW);
        payment.setAmount(amount);
        payment.setCurrency(wallet.getCurrency());
        payment.setStatus(PaymentTransactionEntity.PaymentStatus.PENDING);

        PaymentTransactionEntity initiated = steps.initiate(wallet, payment);
        try {
            GatewayResponse gateway = gatewayClient.payout(initiated.getCurrency(),
                    "withdraw-" + initiated.getId(), amount);
            return PaymentTransactionDto.from(steps.settle(initiated.getId(), gateway));
        } catch (Exception e) {
            return PaymentTransactionDto.from(steps.compensate(initiated.getId(), e));
        }
    }

    @Transactional(readOnly = true)
    public PaymentTransactionDto get(Long userId, Long paymentId) {
        return steps.getPayment(userId, paymentId);
    }

    @Transactional(readOnly = true)
    public java.util.List<PaymentTransactionDto> list(Long userId) {
        return steps.listPayments(userId);
    }

    @Transactional(readOnly = true)
    public java.util.List<PaymentTransactionDto> listAll() {
        return steps.listAllPayments();
    }
}