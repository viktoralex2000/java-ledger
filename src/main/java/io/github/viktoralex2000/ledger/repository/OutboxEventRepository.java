package io.github.viktoralex2000.ledger.repository;

import io.github.viktoralex2000.ledger.model.OutboxEventEntity;
import io.github.viktoralex2000.ledger.model.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {

    //TODO Разобрать отдельно
    @Query(value = """
        SELECT * FROM outbox_events 
        WHERE status = 'PENDING' 
          AND available_at <= :now 
        ORDER BY available_at ASC 
        LIMIT :limit 
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxEventEntity> lockNextBatch(@Param("now") Instant now, @Param("limit") int limit);

    long countByStatus(OutboxStatus status);

    Optional<OutboxEventEntity> findFirstByEventTypeOrderByIdDesc(String eventType);
}