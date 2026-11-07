package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.config.LedgerProperties;
import io.github.viktoralex2000.ledger.dto.TransferDto;
import io.github.viktoralex2000.ledger.dto.TransferRequest;
import io.github.viktoralex2000.ledger.security.RateLimitService;
import io.github.viktoralex2000.ledger.service.IdempotencyService;
import io.github.viktoralex2000.ledger.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final IdempotencyService idempotencyService;
    private final RateLimitService rateLimitService;
    private final LedgerProperties ledgerProperties;

    //todo исправить как дойдём до безопасности
    @PostMapping("/execute")
    public ResponseEntity<?> transfer(
            /*UserEntity user,*/
            @Valid @RequestBody TransferRequest transferRequest,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey) {
        return null;
    }

    //todo исправить как дойдём до безопасности
    @GetMapping("/list")
    public ResponseEntity<List<TransferDto>> list(
            /*UserEntity user*/) {
        return null;
    }

    //todo исправить как дойдём до безопасности
    @GetMapping("/get/{transferId}")
    public ResponseEntity<TransferDto> get(
            /*UserEntity user,*/
            @PathVariable Long transferId) {
        return null;
    }
}
