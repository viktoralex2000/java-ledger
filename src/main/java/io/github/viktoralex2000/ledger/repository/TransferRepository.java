package io.github.viktoralex2000.ledger.repository;

import io.github.viktoralex2000.ledger.model.TransferEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
    Optional<TransferEntity> findByIdAndUserId(Long transferId, Long userId);

    List<TransferEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<TransferEntity> findByIdempotencyKey(String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TransferEntity t WHERE t.id = :transferId")
    Optional<TransferEntity> findByIdForUpdate(Long transferId);
}
