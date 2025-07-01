package com.seme.wiseinvest.transaction.service.impl;

import com.seme.wiseinvest.api.Bankcard;
import com.seme.wiseinvest.api.OurSystem;
import com.seme.wiseinvest.api.client.AccountClient;
import com.seme.wiseinvest.api.client.ProductClient;
import com.seme.wiseinvest.api.client.SettleClient;
import com.seme.wiseinvest.transaction.domain.Holding;
import com.seme.wiseinvest.transaction.domain.Redemption;
import com.seme.wiseinvest.transaction.domain.Subscription;
import com.seme.wiseinvest.transaction.domain.dto.RedemptionDTO;
import com.seme.wiseinvest.transaction.domain.dto.SubscriptionDTO;
import com.seme.wiseinvest.transaction.mapper.HoldingMapper;
import com.seme.wiseinvest.transaction.mapper.RedemptionMapper;
import com.seme.wiseinvest.transaction.mapper.SubscriptionMapper;
import com.seme.wiseinvest.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.*;

@SpringBootTest
class TransactionServiceImplTest {

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private RedemptionMapper redemptionMapper;

    @Mock
    private HoldingMapper holdingMapper;

    @Mock
    private ProductClient productClient;

    @Mock
    private AccountClient accountClient;

    @Mock
    private SettleClient settleClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

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
    void submitSubscription_shouldReturnError_whenBalanceInsufficient() {
        // 使用构造函数传递参数
        Bankcard bankcard = new Bankcard("54321", 50.0);  // 传入银行账号和余额
        when(accountClient.getBankcard(anyLong())).thenReturn(bankcard);

        long result = transactionService.submitSubscription(subscriptionDTO);

        verify(accountClient, times(1)).getBankcard(anyLong());

    }


    @Test
    void submitRedemption_shouldReturnError_whenSharesInsufficient() {
        Holding holding = new Holding();
        holding.setShares(20.0);
        when(holdingMapper.selectOne(any())).thenReturn(holding);

        long result = transactionService.submitRedemption(redemptionDTO);


    }

    @Test
    void confirmSubscriptionBatch_shouldReturnTrue() {
        Map<Long, Double> transactionMap = Map.of(1L, 100.0);
        boolean result = transactionService.confirmSubscriptionBatch(transactionMap);


    }

    @Test
    void confirmRedemptionBatch_shouldReturnTrue() {
        Map<Long, Double> transactionMap = Map.of(1L, 50.0);
        boolean result = transactionService.confirmRedemptionBatch(transactionMap);


    }

    @Test
    void cancelTransaction_shouldReturnTrue() {
        // 使用构造函数传递参数
        Subscription subscription = new Subscription(
                1L, 100.0, 12345L, 54321L, 1, "Product A", new Date(), false
        );

        when(subscriptionMapper.selectById(anyLong())).thenReturn(subscription);

        boolean result = transactionService.cancelTransaction(1L);

    }

}
