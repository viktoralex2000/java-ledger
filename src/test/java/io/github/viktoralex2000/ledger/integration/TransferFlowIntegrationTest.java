package io.github.viktoralex2000.ledger.integration;


import tools.jackson.databind.JsonNode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import io.github.viktoralex2000.ledger.config.LedgerProperties.Transfer;
import io.github.viktoralex2000.ledger.exception.InsufficientFundsException;
import io.github.viktoralex2000.ledger.model.TransferEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** P2P transfers: money movement, FX, outbox, idempotent replay and validation. */
class TransferFlowIntegrationTest extends IntegrationTestSupport {

    @Test
    @DisplayName("transfer moves money, completes transfer and writes outbox event")
    void sameCurrencyTransfer() throws Exception {
        String token = accessToken(register(uniqueEmail(), "Password123"));
        long fromId = createWallet(token, "EUR").get("id").asLong();
        long toId = createWallet(token, "USD").get("id").asLong();
        creditWallet(fromId, new BigDecimal("100"));
        seedRate("EUR", "USD", "2.000000000000");

        JsonNode transfer = postTransfer(token, fromId, toId, "10.00", "it-transfer-1");

        assertEquals("COMPLETED", transfer.get("status").asString());
        assertEquals("EUR", transfer.get("currency").asString());
        assertEquals(0, transfer.get("amount").decimalValue().compareTo(new BigDecimal("10.00")));
        assertEquals(0, transfer.get("fee").decimalValue().compareTo(BigDecimal.ZERO));

        TransferEntity stored = transferRepository.findAll().get(0);
        assertEquals(TransferEntity.TransferStatus.COMPLETED, stored.getStatus());
        assertTrue(outboxRepository.findAll().stream()
                .anyMatch(e -> "TRANSFER_COMPLETED".equals(e.getEventType())));

        assertEquals(0, walletRepository.findById(fromId).orElseThrow().getBalance().compareTo(new BigDecimal("90")));
        // EUR -> USD with rate 2 from seedRate => 20 USD credited
        assertEquals(0, walletRepository.findById(toId).orElseThrow().getBalance().compareTo(new BigDecimal("20")));
        assertEquals(3, ledgerEntryRepository.count()); // 1 test funding + 2 transfer legs
    }

    @Test
    @DisplayName("same Idempotency-Key replay returns the same transfer and does not double-post")
    void idempotentReplay() throws Exception {
        String token = accessToken(register(uniqueEmail(), "Password123"));
        long fromId = createWallet(token, "EUR").get("id").asLong();
        long toId = createWallet(token, "USD").get("id").asLong();
        creditWallet(fromId, new BigDecimal("100"));
        seedRate("EUR", "USD", "2.000000000000");
        String key = "idem-" + UUID.randomUUID();

        JsonNode first = postTransfer(token, fromId, toId, "10.00", key);
        JsonNode replay = postTransfer(token, fromId, toId, "10.00", key);

        assertEquals(first.get("id").asLong(), replay.get("id").asLong());
        assertEquals(1, transferRepository.count());
        assertEquals(3, ledgerEntryRepository.count()); // 1 test funding + 2 transfer legs
        assertEquals(0, walletRepository.findById(fromId).orElseThrow().getBalance().compareTo(new BigDecimal("90")));
    }

    @Test
    @DisplayName("cross-currency transfer uses configured FX rate")
    void crossCurrencyTransferUsesRate() throws Exception {
        String token = accessToken(register(uniqueEmail(), "Password123"));
        long eurWallet = createWallet(token, "EUR").get("id").asLong();
        long usdWallet = createWallet(token, "USD").get("id").asLong();
        creditWallet(eurWallet, new BigDecimal("100"));
        seedRate("EUR", "USD", "2.000000000000");

        JsonNode transfer = postTransfer(token, eurWallet, usdWallet, "15.00", "fx-" + UUID.randomUUID());

        assertEquals(0, transfer.get("convertedAmount").decimalValue().compareTo(new BigDecimal("30")));
        assertEquals(0, transfer.get("fxRate").decimalValue().compareTo(new BigDecimal("2")));
        assertEquals(0, walletRepository.findById(eurWallet).orElseThrow().getBalance().compareTo(new BigDecimal("85")));
        assertEquals(0, walletRepository.findById(usdWallet).orElseThrow().getBalance().compareTo(new BigDecimal("30")));
    }

    @Test
    @DisplayName("insufficient funds leaves balances untouched and creates no completed transfer")
    void insufficientFunds() throws Exception {
        String token = accessToken(register(uniqueEmail(), "Password123"));
        long fromId = createWallet(token, "EUR").get("id").asLong();
        long toId = createWallet(token, "USD").get("id").asLong();

        expectDomainException(InsufficientFundsException.class,
                post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotency-Key", "insufficient-" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromWalletId\":" + fromId + ",\"toWalletId\":" + toId + ",\"amount\":10.00}"));

        assertEquals(0, transferRepository.count());
        assertEquals(0, ledgerEntryRepository.count());
        assertEquals(0, walletRepository.findById(fromId).orElseThrow().getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("invalid amount -> 400, missing token -> 401")
    void validationAndSecurity() throws Exception {
        String token = accessToken(register(uniqueEmail(), "Password123"));
        long fromId = createWallet(token, "EUR").get("id").asLong();
        long toId = createWallet(token, "USD").get("id").asLong();

        mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromWalletId\":" + fromId + ",\"toWalletId\":" + toId + ",\"amount\":0}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromWalletId\":" + fromId + ",\"toWalletId\":" + toId + ",\"amount\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/transfers returns only my transfers")
    void listIsScopedToUser() throws Exception {
        String token = accessToken(register(uniqueEmail(), "Password123"));
        long fromId = createWallet(token, "EUR").get("id").asLong();
        long toId = createWallet(token, "USD").get("id").asLong();
        creditWallet(fromId, new BigDecimal("100"));
        long transferId = postTransfer(token, fromId, toId, "1.00", "list-" + UUID.randomUUID()).get("id").asLong();

        mockMvc.perform(get("/api/transfers").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(transferId));

        mockMvc.perform(get("/api/transfers/" + transferId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    private JsonNode postTransfer(String token, long fromId, long toId, String amount, String key) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromWalletId\":" + fromId + ",\"toWalletId\":" + toId + ",\"amount\":" + amount + "}"))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }
}
