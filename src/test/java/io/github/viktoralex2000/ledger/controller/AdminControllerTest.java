package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.dto.*;
import io.github.viktoralex2000.ledger.security.JwtAuthenticationFilter;
import io.github.viktoralex2000.ledger.service.ExchangeRateService;
import io.github.viktoralex2000.ledger.service.PaymentSagaService;
import io.github.viktoralex2000.ledger.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(
        controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    ExchangeRateService exchangeRateService;
    @MockitoBean
    TransferService transferService;
    @MockitoBean
    PaymentSagaService paymentSagaService;

    @Test
    @DisplayName("Успешное добавление или замена курса валют")
    void upsertRate_success() throws Exception {

        ExchangeRateUpdateRequest eruRequest = new ExchangeRateUpdateRequest(
                new BigDecimal("10"),
                "JTA",
                "RDE"
        );

        ExchangeRateDto erDto = new ExchangeRateDto(
                new BigDecimal("10"),
                "JTA",
                "RDE",
                Instant.now()
        );

        Mockito.when(exchangeRateService.upsert(ArgumentMatchers.any(ExchangeRateUpdateRequest.class)))
                .thenReturn(erDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/upsertRate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eruRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.baseCurrency").value("JTA"))
                .andExpect(jsonPath("$.quoteCurrency").value("RDE"))
                .andExpect(jsonPath("$.rate").value("10"));

        Mockito.verify(exchangeRateService, Mockito.times(1))
                .upsert(any(ExchangeRateUpdateRequest.class));

    }

    /*
    @DeleteMapping("/exchange-rates/{base}/{quote}")
    public ResponseEntity<Void> deleteRate(@PathVariable String base, @PathVariable String quote) {
        exchangeRateService.delete(base, quote);
        return ResponseEntity.noContent().build();
    }
     */

    @Test
    @DisplayName("Успешное удаление курса валют")
    void deleteRate_success() throws Exception {
        Mockito.doNothing().when(exchangeRateService).delete(
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/deleteRate/JTA/RDE"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(exchangeRateService, Mockito.times(1))
                .delete("JTA", "RDE");
    }

    @Test
    @DisplayName("Успешное получение всех трансферов")
    void allTransfers_success() throws Exception {

        List<TransferDto> listTransferDto = new ArrayList<>();
        TransferDto transferDto = new TransferDto(
                1L,
                3L,
                4L,
                "RDN",
                new BigDecimal("60"),
                new BigDecimal("6"),
                new BigDecimal("10"),
                new BigDecimal("0"),
                "COMPLETED",
                "message",
                Instant.now(),
                Instant.now()
        );
        listTransferDto.add(transferDto);

        Mockito.when(transferService.listAll()).thenReturn(listTransferDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/allTransfers"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fromWalletId").value(3L))
                .andExpect(jsonPath("$[0].toWalletId").value(4L))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        Mockito.verify(transferService, Mockito.times(1)).listAll();

    }

    @Test
    @DisplayName("Успешное получение всех платежей")
    void allPayments_success() throws Exception {

        List<PaymentTransactionDto> listPaymentDto = new ArrayList<>();
        PaymentTransactionDto paymentDto = new PaymentTransactionDto(
                3L,
                4L,
                "TOP_UP",
                new BigDecimal("60"),
                "RDN",
                "COMPLETED",
                "1234567890",
                "reason",
                Instant.now(),
                Instant.now()
        );
        listPaymentDto.add(paymentDto);

        Mockito.when(paymentSagaService.listAll()).thenReturn(listPaymentDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/allPayments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].walletId").value(4L))
                .andExpect(jsonPath("$[0].type").value("TOP_UP"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        Mockito.verify(paymentSagaService, Mockito.times(1)).listAll();

    }
}