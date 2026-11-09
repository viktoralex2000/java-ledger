package io.github.viktoralex2000.ledger.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.viktoralex2000.ledger.config.LedgerProperties;
import io.github.viktoralex2000.ledger.dto.PaymentTransactionDto;
import io.github.viktoralex2000.ledger.dto.TopUpRequest;
import io.github.viktoralex2000.ledger.dto.WithdrawRequest;
import io.github.viktoralex2000.ledger.model.UserEntity;
import io.github.viktoralex2000.ledger.security.RateLimitService;
import io.github.viktoralex2000.ledger.service.IdempotencyService;
import io.github.viktoralex2000.ledger.service.PaymentSagaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentSagaService sagaService;
    private final IdempotencyService idempotencyService;
    private final RateLimitService rateLimitService;
    private final LedgerProperties ledgerProperties;

    public PaymentController(PaymentSagaService sagaService,
            IdempotencyService idempotencyService,
            RateLimitService rateLimitService,
            LedgerProperties ledgerProperties) {
this.sagaService = sagaService;
this.idempotencyService = idempotencyService;
this.rateLimitService = rateLimitService;
this.ledgerProperties = ledgerProperties;
}
    
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
    @GetMapping("/{paymentId}")
    //@Operation(summary = "Get one payment")
    public ResponseEntity<PaymentTransactionDto> get(@AuthenticationPrincipal UserEntity user, @PathVariable Long paymentId) {
        return ResponseEntity.ok(sagaService.get(user.getId(), paymentId));
    }
}
