package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.dto.CreateWalletRequest;
import io.github.viktoralex2000.ledger.dto.LedgerEntryDto;
import io.github.viktoralex2000.ledger.dto.PageResponse;
import io.github.viktoralex2000.ledger.dto.WalletDto;
import io.github.viktoralex2000.ledger.repository.LedgerEntryRepository;
import io.github.viktoralex2000.ledger.security.UserPrincipal;
import io.github.viktoralex2000.ledger.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletDto> create(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateWalletRequest createWalletRequest) {
        return ResponseEntity.ok(walletService.create(
                userPrincipal.getUser().getId(),
                createWalletRequest));
    }

    @GetMapping("/list")
    public ResponseEntity<List<WalletDto>> list(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(walletService.list(userPrincipal.getUser().getId()));
    }

    @GetMapping("/get/{walletId}")
    public ResponseEntity<WalletDto> get(
            @PathVariable Long walletId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(walletService.get(
                walletId,
                userPrincipal.getUser().getId()));
    }

    @GetMapping("/ledger/{walletId}")
    public ResponseEntity<PageResponse<LedgerEntryDto>> ledger(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(walletService.ledger(
                userPrincipal.getUser().getId(),
                walletId,
                page,
                size));
    }
}
