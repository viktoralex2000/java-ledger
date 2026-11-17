package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.dto.PaymentTransactionDto;
import io.github.viktoralex2000.ledger.dto.TopUpRequest;
import io.github.viktoralex2000.ledger.dto.WithdrawRequest;
import io.github.viktoralex2000.ledger.model.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentSagaService {

    public PaymentTransactionDto topUp(
            UserEntity user,
            Long walletId,
            TopUpRequest topUpRequest,
            String idempotencyKey) {
        return null;
    }

    public PaymentTransactionDto withDraw(
            UserEntity user,
            Long walletId,
            WithdrawRequest topUpRequest,
            String idempotencyKey) {
        return null;
    }

    public PaymentTransactionDto get(Long paymentId, Long userId) {
        return null;
    }

    public List<PaymentTransactionDto> list(Long userId) {
        return null;
    }

    public List<PaymentTransactionDto> listAll() {
        return null;
    }

}
