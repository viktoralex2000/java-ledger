package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.config.LedgerProperties;
import io.github.viktoralex2000.ledger.dto.TransferDto;
import io.github.viktoralex2000.ledger.dto.TransferRequest;
import io.github.viktoralex2000.ledger.exception.BadRequestException;
import io.github.viktoralex2000.ledger.exception.InsufficientFundsException;
import io.github.viktoralex2000.ledger.exception.ResourceNotFoundException;
import io.github.viktoralex2000.ledger.model.LedgerEntryEntity;
import io.github.viktoralex2000.ledger.model.TransferEntity;
import io.github.viktoralex2000.ledger.model.UserEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import io.github.viktoralex2000.ledger.repository.TransferRepository;
import io.github.viktoralex2000.ledger.repository.WalletRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransferService {

    private static final int MONEY_SCALE = 8;

    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;
    private final LedgerService ledgerService;
    private final FxService fxService;
    private final OutboxService outboxService;
    private final LedgerProperties properties;

    public TransferService(TransferRepository transferRepository,
                           WalletRepository walletRepository,
                           LedgerService ledgerService,
                           FxService fxService,
                           OutboxService outboxService,
                           LedgerProperties properties) {
        this.transferRepository = transferRepository;
        this.walletRepository = walletRepository;
        this.ledgerService = ledgerService;
        this.fxService = fxService;
        this.outboxService = outboxService;
        this.properties = properties;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransferDto transfer(UserEntity user, TransferRequest request) {
        if (request.fromWalletId().equals(request.toWalletId())) {
            throw new BadRequestException("Source and destination wallets must differ");
        }

        WalletEntity from = walletRepository.findByIdForUpdate(request.fromWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + request.fromWalletId()));
        WalletEntity to = walletRepository.findByIdForUpdate(request.toWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + request.toWalletId()));

        if (!from.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Wallet not found with id: " + request.fromWalletId());
        }
        requireActive(from);
        requireActive(to);

        BigDecimal amount = request.amount();
        BigDecimal fee = computeFee(amount);
        BigDecimal totalDebit = amount.add(fee).setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        if (from.getBalance().compareTo(totalDebit) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds: required " + totalDebit + " " + from.getCurrency()
                            + ", available " + from.getBalance() + " " + from.getCurrency());
        }

        BigDecimal rate;
		
			rate = fxService.rate(from.getCurrency(), to.getCurrency());
		
        BigDecimal converted;
		
			converted = fxService.convert(amount, from.getCurrency(), to.getCurrency());
		

        TransferEntity transfer = new TransferEntity();
        transfer.setIdempotencyKey(java.util.UUID.randomUUID().toString());
        transfer.setUser(user);
        transfer.setFromWallet(from);
        transfer.setToWallet(to);
        transfer.setCurrency(from.getCurrency());
        transfer.setAmount(amount);
        transfer.setConvertedAmount(converted);
        transfer.setFxRate(rate);
        transfer.setFee(fee);
        transferRepository.save(transfer);

        String opKey = "transfer:" + transfer.getId();
        ledgerService.post(from, LedgerEntryEntity.EntryType.DEBIT, totalDebit, opKey,
                "Transfer to wallet " + to.getId() + " (+fee " + fee + " " + from.getCurrency() + ")");
        ledgerService.post(to, LedgerEntryEntity.EntryType.CREDIT, converted, opKey,
                "Transfer from wallet " + from.getId());

        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            WalletEntity feeWallet = walletRepository.findSystemWallet(properties.app().feeWalletOwnerEmail(), from.getCurrency())
                    .orElse(null);
            if (feeWallet != null && !feeWallet.getId().equals(from.getId())) {
                walletRepository.findByIdForUpdate(feeWallet.getId()).ifPresent(locked -> {
                    ledgerService.post(locked, LedgerEntryEntity.EntryType.CREDIT, fee, opKey,
                            "Transfer fee from wallet " + from.getId());
                });
            }
        }

        transfer.setStatus(TransferEntity.TransferStatus.COMPLETED);
        transfer.setCompletedAt(Instant.now());
        transferRepository.save(transfer);

        outboxService.emit("transfer", String.valueOf(transfer.getId()), "TRANSFER_COMPLETED", eventPayload(transfer));

        return TransferDto.from(transfer);
    }

    @Transactional(readOnly = true)
    public TransferDto get(Long userId, Long transferId) {
        return transferRepository.findByIdAndUserId(transferId, userId)
                .map(TransferDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + transferId));
    }

    @Transactional(readOnly = true)
    public java.util.List<TransferDto> list(Long userId) {
        return transferRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(TransferDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<TransferDto> listAll() {
        return transferRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 500))
                .map(TransferDto::from).stream().toList();
    }

    private BigDecimal computeFee(BigDecimal amount) {
        BigDecimal percent = properties.transfer().feePercent();
        return amount.multiply(percent).divide(BigDecimal.valueOf(100), MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private void requireActive(WalletEntity wallet) {
        if (wallet.getStatus() != WalletEntity.WalletStatus.ACTIVE) {
            throw new BadRequestException("Wallet " + wallet.getId() + " is not active");
        }
    }

    private Map<String, Object> eventPayload(TransferEntity t) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("transferId", t.getId());
        payload.put("fromWalletId", t.getFromWallet().getId());
        payload.put("toWalletId", t.getToWallet().getId());
        payload.put("amount", t.getAmount());
        payload.put("currency", t.getCurrency());
        payload.put("convertedAmount", t.getConvertedAmount());
        payload.put("fxRate", t.getFxRate());
        payload.put("fee", t.getFee());
        payload.put("status", t.getStatus().name());
        payload.put("userId", t.getUser().getId());
        return payload;
    }
}
