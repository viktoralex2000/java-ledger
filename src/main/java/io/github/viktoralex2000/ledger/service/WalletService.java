package io.github.viktoralex2000.ledger.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.viktoralex2000.ledger.dto.CreateWalletRequest;
import io.github.viktoralex2000.ledger.dto.WalletDto;
import io.github.viktoralex2000.ledger.exception.BadRequestException;
import io.github.viktoralex2000.ledger.exception.ConflictException;
import io.github.viktoralex2000.ledger.exception.ResourceNotFoundException;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import io.github.viktoralex2000.ledger.repository.UserRepository;
import io.github.viktoralex2000.ledger.repository.WalletRepository;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final LedgerService ledgerService;
    private final FxService fxService;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository, LedgerService ledgerService, FxService fxService) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.ledgerService = ledgerService;
        this.fxService = fxService;
    }

    @Transactional
    public WalletDto create(Long userId, CreateWalletRequest request) {
        String currency = request.currency().toUpperCase();
        if (!fxService.supportedCurrencies().contains(currency)) {
            throw new BadRequestException("Unsupported currency: " + currency);
        }
        if (walletRepository.existsByUserIdAndCurrency(userId, currency)) {
            throw new ConflictException("Wallet for " + currency + " already exists");
        }
        WalletEntity wallet = new WalletEntity();
        wallet.setUser(userRepository.getReferenceById(userId));
        wallet.setCurrency(currency);
        walletRepository.save(wallet);
        return WalletDto.of(wallet, BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public List<WalletDto> list(Long userId) {
        return walletRepository.findByUserIdOrderByIdAsc(userId).stream()
                .map(w -> WalletDto.of(w, ledgerService.currentBalance(w)))
                .toList();
    }

    @Transactional(readOnly = true)
    public WalletDto get(Long userId, Long walletId) {
        WalletEntity wallet = findOwned(userId, walletId);
        return WalletDto.of(wallet, ledgerService.currentBalance(wallet));
    }

    @Transactional(readOnly = true)
    public WalletEntity requireOwned(Long userId, Long walletId) {
        return findOwned(userId, walletId);
    }

    public WalletEntity findOwned(Long userId, Long walletId) {
        return walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
    }
}
