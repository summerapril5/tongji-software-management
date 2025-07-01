package com.seme.wiseinvest.settle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.api.OurSystem;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.settle.service.SettleService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SettleController.class)
public class SettleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SettleService settleService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testInitializeDay_success() throws Exception {
        when(settleService.initializeDay()).thenReturn(true);
        mockMvc.perform(post("/settle/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testGetSystem_success() throws Exception {
        OurSystem system = new OurSystem();
        system.setTransactionDate(new Date());
        when(settleService.getSystem()).thenReturn(system);
        mockMvc.perform(post("/settle/system"))
                .andExpect(status().isOk());
    }

    @Test
    void testReceiveMarketData_failure() throws Exception {
        when(settleService.receiveMarketData()).thenReturn(false);
        mockMvc.perform(post("/settle/market"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testConfirmSubscriptions_success() throws Exception {
        when(settleService.confirmSubscriptions()).thenReturn(true);
        mockMvc.perform(post("/settle/subscription"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testExportData_success() throws Exception {
        when(settleService.exportData()).thenReturn(true);
        mockMvc.perform(post("/settle/export"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testGetTransactionDate_success() throws Exception {
        OurSystem system = new OurSystem();
        system.setTransactionDate(new Date());
        when(settleService.getNetValueSystem()).thenReturn(system);

        mockMvc.perform(get("/settle/system/transaction-date"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
