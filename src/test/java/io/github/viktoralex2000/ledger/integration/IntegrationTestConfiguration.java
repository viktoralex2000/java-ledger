package io.github.viktoralex2000.ledger.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test-only infrastructure beans.
 *
 * <p>Jackson 2 is still used by production services (outbox, idempotency and refresh tokens),
 * while Spring Boot 4 auto-configures Jackson 3 — we provide an explicit Jackson 2 mapper here.
 * Redis is replaced by a tiny in-memory stub so integration tests need no Redis server.</p>
 */
@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    @Primary
    ObjectMapper jackson2ObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    RedisTemplate<String, Object> inMemoryRedisTemplate() {
        RedisTemplate<String, Object> template = mock(RedisTemplate.class);
        ValueOperations<String, Object> values = mock(ValueOperations.class);
        Map<String, Object> store = new ConcurrentHashMap<>();
        Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

        when(template.opsForValue()).thenReturn(values);
        when(values.get(anyString())).thenAnswer(inv -> store.get(inv.getArgument(0)));
        when(values.setIfAbsent(anyString(), any(), any(Duration.class)))
                .thenAnswer(inv -> store.putIfAbsent(inv.getArgument(0), inv.getArgument(1)) == null);
        doAnswer(inv -> {
            store.put(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(values).set(anyString(), any(), any(Duration.class));
        when(values.increment(anyString()))
                .thenAnswer(inv -> counters.computeIfAbsent(inv.getArgument(0), k -> new AtomicLong()).incrementAndGet());
        when(template.expire(anyString(), any(Duration.class))).thenReturn(true);
        when(template.delete(anyString())).thenAnswer(inv -> {
            String key = inv.getArgument(0);
            store.remove(key);
            counters.remove(key);
            return true;
        });
        when(template.delete(any(Collection.class))).thenAnswer(inv -> 0L);
        return template;
    }
}
