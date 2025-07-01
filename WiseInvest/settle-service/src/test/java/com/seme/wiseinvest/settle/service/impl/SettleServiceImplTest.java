package com.seme.wiseinvest.settle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seme.wiseinvest.api.OurSystem;
import com.seme.wiseinvest.api.NetValue;
import com.seme.wiseinvest.api.bo.SubscriptionBO;
import com.seme.wiseinvest.api.bo.RedemptionBO;
import com.seme.wiseinvest.settle.mapper.SystemMapper;
import com.seme.wiseinvest.api.client.ProductClient;
import com.seme.wiseinvest.api.client.TransactionClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SettleServiceImplTest {

    @Mock
    private SystemMapper systemMapper;

    @Mock
    private ProductClient productClient;

    @Mock
    private TransactionClient transactionClient;

    @InjectMocks
    private SettleServiceImpl settleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitializeDay_exportNotDone() {
        OurSystem system = new OurSystem();
        system.setHasExportedApplicationData(false);
        when(systemMapper.selectOne(any(QueryWrapper.class))).thenReturn(system);

        boolean result = settleService.initializeDay();
        assertFalse(result);
    }

    @Test
    void testReceiveMarketData_success() {
        OurSystem system = new OurSystem();
        system.setTransactionDate(new Date());
        system.setHasReceivedMarketData(false);

        when(systemMapper.selectOne(any(QueryWrapper.class))).thenReturn(system);

        NetValue nv = new NetValue();
        nv.setNetValue(1.0);
        nv.setProductId(1L);

        when(productClient.getLatestNetValues()).thenReturn(List.of(nv));
        when(systemMapper.update(any(), any())).thenReturn(1);

        boolean result = settleService.receiveMarketData();
        assertTrue(result);
    }

    @Test
    void testConfirmSubscriptions_noPreviousDate() {
        OurSystem system = new OurSystem();
        system.setHasReceivedMarketData(true);
        system.setTransactionDate(new Date());

        when(systemMapper.selectOne(any(QueryWrapper.class))).thenReturn(system);
        when(systemMapper.selectOne(argThat(q -> q.getCustomSqlSegment().contains("offset 1")))).thenReturn(null);

        boolean result = settleService.confirmSubscriptions();
        assertFalse(result);
    }

    @Test
    void testStopDailyApplications_success() {
        OurSystem system = new OurSystem();
        system.setTransactionDate(new Date());

        when(systemMapper.selectOne(any(QueryWrapper.class))).thenReturn(system);
        when(systemMapper.update(any(), any())).thenReturn(1);

        boolean result = settleService.stopDailyApplications();
        assertTrue(result);
    }

    @Test
    void testExportData_notStopped() {
        OurSystem system = new OurSystem();
        system.setHasStoppedApplication(false);

        when(systemMapper.selectOne(any(QueryWrapper.class))).thenReturn(system);

        boolean result = settleService.exportData();
        assertFalse(result);
    }
}
