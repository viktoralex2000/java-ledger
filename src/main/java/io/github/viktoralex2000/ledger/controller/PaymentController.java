package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.config.LedgerProperties;
import io.github.viktoralex2000.ledger.dto.PaymentTransactionDto;
import io.github.viktoralex2000.ledger.dto.TopUpRequest;
import io.github.viktoralex2000.ledger.dto.WithdrawRequest;
import io.github.viktoralex2000.ledger.security.RateLimitService;
import io.github.viktoralex2000.ledger.security.UserPrincipal;
import io.github.viktoralex2000.ledger.service.IdempotencyService;
import io.github.viktoralex2000.ledger.service.PaymentSagaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    //todo защита от повтороного запроса на тот же перевод
    private final IdempotencyService idempotencyService;
    private final PaymentSagaService paymentSagaService;
    //todo защита от множественных запросов в короткое время
    private final RateLimitService rateLimitService;
    //todo вероятно надо убрать в сервис
    private final LedgerProperties ledgerProperties;

    @PostMapping("/topUp/{walletId}")
    public ResponseEntity<PaymentTransactionDto> topUp(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long walletId,
            @Valid @RequestBody TopUpRequest topUpRequest,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey) {
        return ResponseEntity.ok(paymentSagaService.topUp(
                userPrincipal.getUser(),
                walletId,
                topUpRequest,
                idempotencyKey));
    }

    @PostMapping("/withdraw/{walletId}")
    public ResponseEntity<PaymentTransactionDto> withdraw(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long walletId,
            @Valid @RequestBody WithdrawRequest withdrawRequest,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey) {
        return ResponseEntity.ok(paymentSagaService.withDraw(
                userPrincipal.getUser(),
                walletId,
                withdrawRequest,
                idempotencyKey));
    }

    @GetMapping("/list")
    public ResponseEntity<List<PaymentTransactionDto>> list(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(paymentSagaService.list(userPrincipal.getUser().getId()));
    }

    @GetMapping("/get/{transactionId}")
    public ResponseEntity<PaymentTransactionDto> get(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(paymentSagaService.get(transactionId,
                userPrincipal.getUser().getId()));
    }
}
