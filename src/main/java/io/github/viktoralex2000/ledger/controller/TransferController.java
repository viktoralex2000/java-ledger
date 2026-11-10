package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.config.LedgerProperties;
import io.github.viktoralex2000.ledger.dto.TransferDto;
import io.github.viktoralex2000.ledger.dto.TransferRequest;
import io.github.viktoralex2000.ledger.security.RateLimitService;
import io.github.viktoralex2000.ledger.security.UserPrincipal;
import io.github.viktoralex2000.ledger.service.IdempotencyService;
import io.github.viktoralex2000.ledger.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    //todo проработать нижние 3 сервиса
    private final IdempotencyService idempotencyService;
    private final RateLimitService rateLimitService;
    private final LedgerProperties ledgerProperties;


    @PostMapping("/execute")
    public ResponseEntity<TransferDto> transfer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TransferRequest transferRequest,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey) {
        return ResponseEntity.ok(transferService.transfer(
                userPrincipal.getUser(),
                transferRequest,
                idempotencyKey
        ));
    }

    @GetMapping("/list")
    public ResponseEntity<List<TransferDto>> list(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(transferService.list(userPrincipal.getUser().getId()));
    }

    @GetMapping("/get/{transferId}")
    public ResponseEntity<TransferDto> get(
            @PathVariable Long transferId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(transferService.get(
                transferId,
                userPrincipal.getUser().getId()));
    }
}
