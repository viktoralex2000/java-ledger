package io.github.viktoralex2000.ledger.repository;

import io.github.viktoralex2000.ledger.model.PaymentTransactionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {
    Optional<PaymentTransactionEntity> findByIdAndUserId(Long paymentTransactionId, Long userId);

    List<PaymentTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<PaymentTransactionEntity> findByIdempotencyKey(String ik);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentTransactionEntity p WHERE p.id = :paymentTransactionId")
    Optional<PaymentTransactionEntity> findByIdForUpdate(Long paymentTransactionId);
}
