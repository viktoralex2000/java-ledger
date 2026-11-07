package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.config.LedgerProperties;
import io.github.viktoralex2000.ledger.dto.PaymentTransactionDto;
import io.github.viktoralex2000.ledger.dto.TopUpRequest;
import io.github.viktoralex2000.ledger.dto.WithdrawRequest;
import io.github.viktoralex2000.ledger.security.RateLimitService;
import io.github.viktoralex2000.ledger.service.IdempotencyService;
import io.github.viktoralex2000.ledger.service.PaymentSagaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentSagaService sagaService;
    private final IdempotencyService idempotencyService;
    private final RateLimitService rateLimitService;
    private final LedgerProperties ledgerProperties;

    //todo исправить как дойдём до безопасности
    @PostMapping("/topUp/{walletId}")
    public ResponseEntity<?> topUp(
            /*UserEntity user,*/
            @PathVariable Long walletId,
            @Valid @RequestBody TopUpRequest topUpRequest,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey) {
        return null;
    }

    //todo исправить как дойдём до безопасности
    @PostMapping("/withdraw/{walletId}")
    public ResponseEntity<?> withdraw(
            /*UserEntity user,*/
            @PathVariable Long walletId,
            @Valid @RequestBody WithdrawRequest withdrawRequest,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey) {
        return null;
    }

    //todo исправить как дойдём до безопасности
    @GetMapping("/list")
    public ResponseEntity<List<PaymentTransactionDto>> list(
            /*UserEntity user*/) {
        return null;
    }

    //todo исправить как дойдём до безопасности
    @GetMapping("/get/{transactionId}")
    public ResponseEntity<PaymentTransactionDto> get(
            /*UserEntity user,*/
            @PathVariable Long transactionId) {
        return null;
    }
}
