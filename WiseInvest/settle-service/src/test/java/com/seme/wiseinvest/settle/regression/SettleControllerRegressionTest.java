package com.seme.wiseinvest.settle.regression;

import com.seme.wiseinvest.api.OurSystem;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.settle.controller.SettleController;
import com.seme.wiseinvest.settle.service.SettleService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SettleController.class)
public class SettleControllerRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SettleService settleService;

    /**
     * 测试获取系统状态接口 /settle
     * 应返回 200 状态码和包含交易日期的 OurSystem 对象
     */
    @Test
    public void testGetSystem_shouldReturnValidSystemInfo() throws Exception {
        OurSystem mockSystem = new OurSystem();
        mockSystem.setTransactionDate(new Date());

        when(settleService.getSystem()).thenReturn(mockSystem);

        mockMvc.perform(get("/settle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.transactionDate").exists());
    }

    /**
     * 测试初始化交易日接口 /settle/init
     * 应返回成功的 Result 响应
     */
    @Test
    public void testInitializeDay_shouldReturnSuccess() throws Exception {
        when(settleService.initializeDay()).thenReturn(true);

        mockMvc.perform(post("/settle/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试接收市场行情数据接口 /settle/market
     * 应返回成功的响应
     */
    @Test
    public void testReceiveMarketData_shouldReturnSuccess() throws Exception {
        when(settleService.receiveMarketData()).thenReturn(true);

        mockMvc.perform(post("/settle/market"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试确认申购接口 /settle/subscription
     * 应返回成功结果
     */
    @Test
    public void testConfirmSubscriptions_shouldReturnSuccess() throws Exception {
        when(settleService.confirmSubscriptions()).thenReturn(true);

        mockMvc.perform(post("/settle/subscription"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试确认赎回接口 /settle/redemption
     * 应返回成功结果
     */
    @Test
    public void testConfirmRedemptions_shouldReturnSuccess() throws Exception {
        when(settleService.confirmRedemptions()).thenReturn(true);

        mockMvc.perform(post("/settle/redemption"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试停止接收当日交易申请接口 /settle/stop
     * 应返回成功结果
     */
    @Test
    public void testStopDailyApplications_shouldReturnSuccess() throws Exception {
        when(settleService.stopDailyApplications()).thenReturn(true);

        mockMvc.perform(post("/settle/stop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试导出数据接口 /settle/export
     * 应返回成功结果
     */
    @Test
    public void testExportData_shouldReturnSuccess() throws Exception {
        when(settleService.exportData()).thenReturn(true);

        mockMvc.perform(post("/settle/export"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 测试获取交易日期字符串接口 /settle/system/transaction-date
     * 应返回格式化后的交易日期字符串
     */
    @Test
    public void testGetTransactionDate_shouldReturnFormattedDate() throws Exception {
        OurSystem mockSystem = new OurSystem();
        mockSystem.setTransactionDate(new Date());

        when(settleService.getNetValueSystem()).thenReturn(mockSystem);

        mockMvc.perform(get("/settle/system/transaction-date"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isString());
    }
}
