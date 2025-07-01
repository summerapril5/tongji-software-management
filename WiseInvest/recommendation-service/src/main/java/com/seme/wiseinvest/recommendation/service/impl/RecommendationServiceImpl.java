package com.seme.wiseinvest.recommendation.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.account.domain.Customer;
import com.seme.wiseinvest.api.client.AccountClient;
import com.seme.wiseinvest.api.client.ProductClient;
import com.seme.wiseinvest.api.client.TransactionClient;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.product.domain.Product;
import com.seme.wiseinvest.recommendation.service.RecommendationService;
import com.seme.wiseinvest.transaction.domain.dto.HoldingDTO; // 使用您定义的HoldingDTO
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final AccountClient accountClient;
    private final ProductClient productClient;
    private final TransactionClient transactionClient;

    private static final int DEFAULT_RECOMMENDATION_COUNT = 6;
    private static final int DEFAULT_NORMAL_PRODUCT_STATUS = 1;

    private final int recommendationCount;
    private final int normalProductStatus;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于安全的类型转换

    @Autowired
    public RecommendationServiceImpl(AccountClient accountClient,
                                     ProductClient productClient,
                                     TransactionClient transactionClient) {
        this.accountClient = accountClient;
        this.productClient = productClient;
        this.transactionClient = transactionClient;
        this.recommendationCount = DEFAULT_RECOMMENDATION_COUNT;
        this.normalProductStatus = DEFAULT_NORMAL_PRODUCT_STATUS;
    }

    @Override
    public List<Product> getPersonalizedRecommendations(Long fundAccount) {
        if (fundAccount == null) {
            logger.warn("FundAccount is null, cannot provide personalized recommendations.");
            return Collections.emptyList();
        }
        logger.info("Starting personalized recommendations for fundAccount: {}", fundAccount);

        // 步骤 1: 获取用户风险等级
        Customer customer = null;
        try {
            logger.debug("Calling AccountClient.getCustomerByFundAccount for fundAccount: {}", fundAccount);
            Result customerResultRaw = accountClient.getCustomerByFundAccount(fundAccount);
            if (customerResultRaw != null && customerResultRaw.getCode() == 0 && customerResultRaw.getData() != null) {
                // 假设 AccountClient 返回的 Result.data 中是 Customer 对象或其Map表示
                customer = objectMapper.convertValue(customerResultRaw.getData(), Customer.class);
                logger.debug("Successfully fetched customer data for fundAccount: {}", fundAccount);
            } else {
                logger.warn("Failed to fetch customer data or data is null for fundAccount: {}. Response: code={}, msg={}",
                        fundAccount,
                        customerResultRaw != null ? customerResultRaw.getCode() : "null",
                        customerResultRaw != null ? customerResultRaw.getMsg() : "null");
            }
        } catch (Exception e) {
            logger.error("Error calling AccountClient.getCustomerByFundAccount for fundAccount: {}", fundAccount, e);
        }

        // 步骤 2: 获取用户已持仓的产品ID列表
        Set<String> localHeldProductIds = new HashSet<>(); // 使用局部变量，其最终值将赋给 effectively final 变量
        try {
            logger.debug("Calling TransactionClient.getHoldingsByFundAccount for fundAccount: {}", fundAccount);
            Result holdingsResultRaw = transactionClient.getHoldingsByFundAccount(fundAccount);
            if (holdingsResultRaw != null && holdingsResultRaw.getCode() == 0 && holdingsResultRaw.getData() != null) {
                try {
                    // 使用 ObjectMapper 将 Result.data (Object) 转换为 List<HoldingDTO>
                    // HoldingDTO 是您定义的包含 productId 和 tradingAccountId 的类
                    List<HoldingDTO> holdings = objectMapper.convertValue(
                            holdingsResultRaw.getData(),
                            new TypeReference<List<HoldingDTO>>(){}
                    );
                    if (holdings != null) {
                        // 从 HoldingDTO 中提取 productId (int)，并转换为 String 放入 Set
                        localHeldProductIds = holdings.stream()
                                .map(dto -> String.valueOf(dto.getProductId())) // 使用 dto.getProductId()
                                .collect(Collectors.toSet());
                        logger.debug("Successfully fetched {} product IDs from holdings for fundAccount: {}", localHeldProductIds.size(), fundAccount);
                    }
                } catch (Exception e) {
                    logger.error("Failed to cast/process holdings data for fundAccount: {}. Data: {}", fundAccount, holdingsResultRaw.getData(), e);
                }
            } else {
                logger.warn("Failed to fetch holdings data or data is null for fundAccount: {}. Response: code={}, msg={}",
                        fundAccount,
                        holdingsResultRaw != null ? holdingsResultRaw.getCode() : "null",
                        holdingsResultRaw != null ? holdingsResultRaw.getMsg() : "null");
            }
        } catch (Exception e) {
            logger.error("Error calling TransactionClient.getHoldingsByFundAccount for fundAccount: {}", fundAccount, e);
        }

        // 将最终确定的持仓ID列表赋给一个 effectively final 变量
        final Set<String> effectivelyFinalHeldProductIds = localHeldProductIds;

        // 步骤 3: 获取所有产品
        List<Product> allProducts = Collections.emptyList();
        try {
            logger.debug("Calling ProductClient.getAllProducts");
            Result allProductsResultRaw = productClient.getAllProducts();
            if (allProductsResultRaw != null && allProductsResultRaw.getCode() == 0 && allProductsResultRaw.getData() != null) {
                try {
                    allProducts = objectMapper.convertValue(
                            allProductsResultRaw.getData(),
                            new TypeReference<List<Product>>(){}
                    );
                    logger.debug("Successfully fetched {} products.", allProducts.size());
                } catch (Exception e) { // Catch specific IllegalArgumentException from convertValue or broader
                    logger.error("Failed to cast all products data. Data: {}", allProductsResultRaw.getData(), e);
                }
            } else {
                logger.warn("Failed to fetch all products data or data is null. Response: code={}, msg={}",
                        allProductsResultRaw != null ? allProductsResultRaw.getCode() : "null",
                        allProductsResultRaw != null ? allProductsResultRaw.getMsg() : "null");
            }
        } catch (Exception e) {
            logger.error("Error calling ProductClient.getAllProducts", e);
        }

        if (allProducts.isEmpty()) {
            logger.warn("No products available to recommend.");
            return Collections.emptyList();
        }

        // 步骤 4: 过滤和排序
        List<Product> recommendedProducts;
        if (customer == null) { // 确保 customer 和 riskLevel 都有效
            logger.info("Customer or risk level not found for fundAccount: {}. Returning hot picks.", fundAccount);
            recommendedProducts = getHotPicks(allProducts, effectivelyFinalHeldProductIds, this.recommendationCount);
        } else {
            Integer userRiskLevel = customer.getRiskLevel();
            logger.info("Personalized recommendation for fundAccount: {}, userRiskLevel: {}", fundAccount, userRiskLevel);

            List<Product> candidateProducts = allProducts.stream()
                    .filter(product -> product.getProductStatus() != null && product.getProductStatus().equals(this.normalProductStatus))
                    .filter(product -> product.getRiskLevel() != null && product.getRiskLevel() <= userRiskLevel)
                    // 使用 effectivelyFinalHeldProductIds
                    .filter(product -> !effectivelyFinalHeldProductIds.contains(String.valueOf(product.getProductId())))
                    .collect(Collectors.toList());

            logger.debug("Found {} candidate products for fundAccount: {} after initial filtering.", candidateProducts.size(), fundAccount);

            candidateProducts.sort(
                    Comparator.comparing(Product::getProductRating, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(Product::getProductAum, Comparator.nullsLast(Comparator.reverseOrder()))
            );

            recommendedProducts = candidateProducts.stream().limit(this.recommendationCount).collect(Collectors.toList());

            if (recommendedProducts.size() < this.recommendationCount) {
                // 为补充热门推荐准备排除列表
                final Set<String> excludedForHotPicks = new HashSet<>(effectivelyFinalHeldProductIds);
                recommendedProducts.forEach(p -> excludedForHotPicks.add(String.valueOf(p.getProductId())));

                int needed = this.recommendationCount - recommendedProducts.size();
                List<Product> hotPicks = getHotPicks(allProducts, excludedForHotPicks, needed);
                recommendedProducts.addAll(hotPicks);
            }
        }
        logger.info("Returning {} recommendations for fundAccount: {}", recommendedProducts.size(), fundAccount);
        return recommendedProducts;
    }

    private List<Product> getHotPicks(List<Product> allProducts, Set<String> excludedProductIds, int count) {
        if (allProducts == null || allProducts.isEmpty() || count <= 0) {
            return Collections.emptyList();
        }
        final Set<String> finalExcludedProductIds = excludedProductIds == null ? Collections.emptySet() : excludedProductIds;

        Stream<Product> productStream = allProducts.stream();
        productStream = productStream.filter(p -> p.getProductStatus() != null && p.getProductStatus().equals(this.normalProductStatus));

        if (!finalExcludedProductIds.isEmpty()) {
            productStream = productStream.filter(p -> !finalExcludedProductIds.contains(String.valueOf(p.getProductId())));
        }

        return productStream
                .sorted(Comparator.comparing(Product::getProductRating, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Product::getProductAum, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }
}