package io.github.viktoralex2000.ledger.repository;

import io.github.viktoralex2000.ledger.model.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    List<WalletEntity> findByUserIdOrderByIdAsc(Long userId);

    Optional<WalletEntity> findByIdAndUserId(Long walletId, Long userId);

    boolean existsByUserIdAndCurrency(Long userId, String currency);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.id = :walletId")
    Optional<WalletEntity> findByIdForUpdate(Long walletId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.currency = :currency" +
            " AND w.user.email = :systemEmail")
    Optional<WalletEntity> findSystemWallet(String currency, String systemEmail);
}
