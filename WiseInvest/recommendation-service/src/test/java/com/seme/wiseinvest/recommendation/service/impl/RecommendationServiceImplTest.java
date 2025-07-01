package com.seme.wiseinvest.recommendation.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.account.domain.Customer;
import com.seme.wiseinvest.api.client.AccountClient;
import com.seme.wiseinvest.api.client.ProductClient;
import com.seme.wiseinvest.api.client.TransactionClient;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.product.domain.Product;
import com.seme.wiseinvest.transaction.domain.dto.HoldingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecommendationServiceImplTest {

    @Mock private AccountClient accountClient;
    @Mock private ProductClient productClient;
    @Mock private TransactionClient transactionClient;

    @InjectMocks private RecommendationServiceImpl recommendationService;

    @Captor ArgumentCaptor<Long> longCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPersonalizedRecommendations_shouldReturnFallbackHotPicks_whenCustomerNotFound() {
        Long fundAccount = 10086L;

        // Mock返回：客户信息为空
        when(accountClient.getCustomerByFundAccount(fundAccount))
                .thenReturn(Result.error("Not found"));

        // Mock持仓为空
        when(transactionClient.getHoldingsByFundAccount(fundAccount))
                .thenReturn(Result.success(List.of()));

        Product hot1 = new Product();
        hot1.setProductId(1L);
        hot1.setProductRating(5.0);
        hot1.setProductAum(10000.0);
        hot1.setProductStatus(1);

        Product hot2 = new Product();
        hot2.setProductId(2L);
        hot2.setProductRating(4.8);
        hot2.setProductAum(9000.0);
        hot2.setProductStatus(1);

        when(productClient.getAllProducts()).thenReturn(Result.success(List.of(hot1, hot2)));

        List<Product> result = recommendationService.getPersonalizedRecommendations(fundAccount);

        assertEquals(2, result.size());
        assertTrue(result.contains(hot1));
    }

    @Test
    void getPersonalizedRecommendations_shouldFilterAndLimitByRisk() {
        Long fundAccount = 10001L;

        // Mock客户信息
        Customer customer = new Customer();
        customer.setRiskLevel(3);
        when(accountClient.getCustomerByFundAccount(fundAccount))
                .thenReturn(Result.success(customer));

        // Mock持仓
        HoldingDTO h = new HoldingDTO();
        h.setProductId(2);
        when(transactionClient.getHoldingsByFundAccount(fundAccount))
                .thenReturn(Result.success(List.of(h)));

        // Mock产品列表
        Product p1 = new Product(); // 合规产品
        p1.setProductId(1L);
        p1.setProductStatus(1);
        p1.setRiskLevel(2);
        p1.setProductRating(4.9);
        p1.setProductAum(9000.0);

        Product p2 = new Product(); // 已持仓，应被过滤
        p2.setProductId(2L);
        p2.setProductStatus(1);
        p2.setRiskLevel(2);

        Product p3 = new Product(); // 风险等级过高
        p3.setProductId(3L);
        p3.setProductStatus(1);
        p3.setRiskLevel(5);

        when(productClient.getAllProducts()).thenReturn(Result.success(List.of(p1, p2, p3)));

        List<Product> result = recommendationService.getPersonalizedRecommendations(fundAccount);

        assertEquals(1, result.size());
        assertEquals(p1.getProductId(), result.get(0).getProductId());

    }
}
