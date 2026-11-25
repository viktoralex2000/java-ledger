package io.github.viktoralex2000.ledger.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import io.github.viktoralex2000.ledger.dto.RegisterRequest;
import io.github.viktoralex2000.ledger.exception.ApiException;
import io.github.viktoralex2000.ledger.gateway.MockGatewayClient;
import io.github.viktoralex2000.ledger.model.ExchangeRateEntity;
import io.github.viktoralex2000.ledger.model.LedgerEntryEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import io.github.viktoralex2000.ledger.repository.ExchangeRateRepository;
import io.github.viktoralex2000.ledger.repository.LedgerEntryRepository;
import io.github.viktoralex2000.ledger.repository.OutboxEventRepository;
import io.github.viktoralex2000.ledger.repository.PaymentTransactionRepository;
import io.github.viktoralex2000.ledger.repository.TransferRepository;
import io.github.viktoralex2000.ledger.repository.UserRepository;
import io.github.viktoralex2000.ledger.repository.WalletRepository;
import io.github.viktoralex2000.ledger.service.FxService;
import io.github.viktoralex2000.ledger.service.LedgerService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Common setup for full-context integration tests (real MVC, security, services and JPA on H2). */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Import(IntegrationTestConfiguration.class)
public abstract class IntegrationTestSupport {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper json;
    @Autowired protected UserRepository userRepository;
    @Autowired protected WalletRepository walletRepository;
    @Autowired protected TransferRepository transferRepository;
    @Autowired protected PaymentTransactionRepository paymentRepository;
    @Autowired protected LedgerEntryRepository ledgerEntryRepository;
    @Autowired protected OutboxEventRepository outboxRepository;
    @Autowired protected ExchangeRateRepository exchangeRateRepository;
    @Autowired protected FxService fxService;
    @Autowired protected LedgerService ledgerService;

    /** External payment gateway is replaced by a stub; no HTTP call leaves the test. */
    @MockitoBean protected MockGatewayClient gatewayClient;

    @BeforeEach
    void resetIntegrationState() {
        ledgerEntryRepository.deleteAll();
        transferRepository.deleteAll();
        paymentRepository.deleteAll();
        walletRepository.deleteAll();
        outboxRepository.deleteAll();
        exchangeRateRepository.deleteAll();
        // Users are intentionally kept: the admin account is seeded once when the context starts.
    }

    protected String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }

    protected JsonNode register(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                new RegisterRequest("Test", "User", email, password))))
                .andExpect(status().isCreated())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    protected JsonNode login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new LoginRequestStub(email, password))))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    protected String accessToken(JsonNode auth) {
        return auth.get("accessToken").asText();
    }

    protected String refreshToken(JsonNode auth) {
        return auth.get("refreshToken").asText();
    }

    protected JsonNode me(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    protected JsonNode createWallet(String token, String currency) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/wallets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currency\":\"" + currency + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    protected void seedRate(String base, String quote, String rate) {
        ExchangeRateEntity entity = exchangeRateRepository.findByBaseCurrencyAndQuoteCurrency(base, quote)
                .orElseGet(() -> {
                    ExchangeRateEntity r = new ExchangeRateEntity();
                    r.setBaseCurrency(base);
                    r.setQuoteCurrency(quote);
                    return r;
                });
        entity.setRate(new BigDecimal(rate));
        exchangeRateRepository.save(entity);
        fxService.refresh();
    }

    protected void creditWallet(Long walletId, BigDecimal amount) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new AssertionError("Wallet " + walletId + " not found"));
        ledgerService.post(wallet, LedgerEntryEntity.EntryType.CREDIT, amount,
                "test-credit:" + UUID.randomUUID(), "Test funding");
        walletRepository.save(wallet);
    }

    protected String adminToken() throws Exception {
        return accessToken(login("admin@ledger.com", "admin123"));
    }

    /**
     * This project has no {@code @RestControllerAdvice}, so MockMvc propagates domain
     * exceptions as {@code ServletException}. Assert the real exception instead of a
     * fabricated HTTP status.
     */
    protected <T extends ApiException> T expectDomainException(Class<T> type,
                                                               MockHttpServletRequestBuilder request) {
        Throwable thrown = assertThrows(Throwable.class, () -> mockMvc.perform(request));
        Throwable current = thrown;
        while (current != null && !type.isInstance(current)) {
            current = current.getCause();
        }
        assertTrue(type.isInstance(current),
                () -> "Expected " + type.getSimpleName() + " but got " + thrown);
        return type.cast(current);
    }

    /** Avoids importing the production DTO just for login JSON in helper methods. */
    private record LoginRequestStub(String email, String password) {
    }
}
