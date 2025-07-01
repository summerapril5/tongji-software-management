package com.seme.wiseinvest.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.transaction.domain.dto.RedemptionDTO;
import com.seme.wiseinvest.transaction.domain.dto.SubscriptionDTO;
import com.seme.wiseinvest.transaction.service.TransactionService;
import com.seme.wiseinvest.api.bo.SubscriptionBO;
import com.seme.wiseinvest.api.bo.RedemptionBO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private SubscriptionDTO subscriptionDTO;
    private RedemptionDTO redemptionDTO;

    @BeforeEach
    void setUp() {
        subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setAmount(100.0);
        subscriptionDTO.setTradingAccountId("12345");
        subscriptionDTO.setFundAccount(54321L);
        subscriptionDTO.setProductId(1);
        subscriptionDTO.setProductName("Product A");

        redemptionDTO = new RedemptionDTO();
        redemptionDTO.setShares(50.0);
        redemptionDTO.setTradingAccountId("12345");
        redemptionDTO.setFundAccount(54321L);
        redemptionDTO.setProductId(1);
        redemptionDTO.setProductName("Product A");
    }

    @Test
    void submitSubscription_shouldReturnError_whenBalanceInsufficient() throws Exception {
        when(transactionService.submitSubscription(any(SubscriptionDTO.class))).thenReturn(1L);

        mockMvc.perform(post("/transaction/subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("余额不足"));
    }

    @Test
    void submitRedemption_shouldReturnError_whenSharesInsufficient() throws Exception {
        when(transactionService.submitRedemption(any(RedemptionDTO.class))).thenReturn(1L);

        mockMvc.perform(post("/transaction/redemption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(redemptionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.message").value("份额不足"));
    }

    @Test
    void cancelTransaction_shouldReturnSuccess() throws Exception {
        // 将 transactionId 转换为 long 类型
        long transactionId = Long.parseLong("123");

        when(transactionService.cancelTransaction(transactionId)).thenReturn(true);

        mockMvc.perform(post("/transaction/cancel")
                        .param("transactionId", String.valueOf(transactionId)))  // 传递转换后的 String 参数
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }


    @Test
    void getValidSubscriptionBOs_shouldReturnList() throws Exception {
        // 确保使用包装类型 Long, Integer, Double
        List<SubscriptionBO> subscriptionBOList = List.of(new SubscriptionBO());

        when(transactionService.getValidSubscriptionBOs(any(Date.class))).thenReturn(subscriptionBOList);

        mockMvc.perform(post("/transaction/subscriptions/valid")
                        .param("date", "2023-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].transactionId").value(1L));
    }

    @Test
    void getValidRedemptionBOs_shouldReturnList() throws Exception {
        // 确保使用包装类型 Long, Integer, Double
        List<RedemptionBO> redemptionBOList = List.of(new RedemptionBO());

        when(transactionService.getValidRedemptionBOs(any(Date.class))).thenReturn(redemptionBOList);

        mockMvc.perform(post("/transaction/redemptions/valid")
                        .param("date", "2023-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].transactionId").value(1L));
    }


    @Test
    void confirmSubscriptionBatch_shouldReturnTrue() throws Exception {
        Map<Long, Double> transactionMap = Map.of(1L, 100.0);
        when(transactionService.confirmSubscriptionBatch(any(Map.class))).thenReturn(true);

        mockMvc.perform(post("/transaction/subscription/confirm-batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(true));
    }

    @Test
    void confirmRedemptionBatch_shouldReturnTrue() throws Exception {
        Map<Long, Double> transactionMap = Map.of(1L, 50.0);
        when(transactionService.confirmRedemptionBatch(any(Map.class))).thenReturn(true);

        mockMvc.perform(post("/transaction/redemption/confirm-batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(true));
    }
}
