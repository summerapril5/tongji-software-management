package com.seme.wiseinvest.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.api.OurSystem;
import com.seme.wiseinvest.api.client.AccountClient;
import com.seme.wiseinvest.api.client.SettleClient;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.transaction.domain.Holding; // Added
import com.seme.wiseinvest.transaction.domain.Redemption;
import com.seme.wiseinvest.transaction.domain.Subscription;
import com.seme.wiseinvest.transaction.domain.dto.HoldingDTO; // Added
import com.seme.wiseinvest.transaction.domain.vo.TransactionVO;
import com.seme.wiseinvest.transaction.mapper.HoldingMapper; // Added
import com.seme.wiseinvest.transaction.mapper.RedemptionMapper;
import com.seme.wiseinvest.transaction.mapper.SubscriptionMapper;
import com.seme.wiseinvest.transaction.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class); // Added logger

    private final SubscriptionMapper subscriptionMapper;
    private final RedemptionMapper redemptionMapper;
    private final SettleClient settleClient;
    private final HoldingMapper holdingMapper;
    private final AccountClient accountClient;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Added ObjectMapper

    @Override
    public List<TransactionVO> getTransactions(Long fundAccount) { // Renamed from getTransactionsByFundAccount to match your QueryService
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Moved formatter here or make it a static final field
        List<Subscription> subscriptions = subscriptionMapper.selectList(new QueryWrapper<Subscription>().eq("fund_account", fundAccount));
        List<Redemption> redemptions = redemptionMapper.selectList(new QueryWrapper<Redemption>().eq("fund_account", fundAccount));
        OurSystem system = settleClient.getSystem();
        List<TransactionVO> transactions = subscriptions.stream().map(
                subscription -> new TransactionVO(subscription.getTransactionId().toString(), subscription.getTradingAccountId().toString(),
                        subscription.getFundAccount().toString(),subscription.getProductId(),subscription.getProductName(),
                        subscription.getApplicationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter),
                        subscription.isCancel(),
                        !subscription.isCancel() && !system.isHasExportedApplicationData()
                                && system.getTransactionDate().getTime() - subscription.getApplicationTime().getTime() < 1000 * 60 * 60 * 24,
                        true,subscription.getSubscriptionAmount(),0)).collect(Collectors.toList());
        transactions.addAll(redemptions.stream().map(
                redemption -> new TransactionVO(redemption.getTransactionId().toString(), redemption.getTradingAccountId().toString(),
                        redemption.getFundAccount().toString(),redemption.getProductId(),redemption.getProductName(),
                        redemption.getApplicationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter),
                        redemption.isCancel(),
                        !redemption.isCancel() && !system.isHasExportedApplicationData()
                                && system.getTransactionDate().getTime() - redemption.getApplicationTime().getTime() < 1000 * 60 * 60 * 24,
                        false,0,redemption.getRedemptionShares())).collect(Collectors.toList()));
        transactions.sort(Comparator.comparing(TransactionVO::getApplicationTime));
        return transactions;
    }

    // Implementation for the new method in QueryService interface
    // This method returns List<HoldingDTO> directly.
    // The QueryController will wrap this list in Result.success().
    @Override
    public List<HoldingDTO> getHoldingsByFundAccount(Long fundAccount) {
        if (fundAccount == null) {
            logger.warn("getHoldingsByFundAccount called with null fundAccount.");
            return Collections.emptyList();
        }
        logger.info("Fetching holdings for fundAccount: {}", fundAccount);

        List<Long> tradingAccountIds = new ArrayList<>();
        try {
            Result tradingAccountsResult = accountClient.getTradingAccounts(fundAccount.toString());
            if (tradingAccountsResult != null && tradingAccountsResult.getCode() == 0 && tradingAccountsResult.getData() != null) {
                try {
                    logger.info("Raw tradingAccountsResult.getData(): {}", tradingAccountsResult.getData());
                    logger.info("Raw tradingAccountsResult.getData() class: {}", tradingAccountsResult.getData().getClass());

                    List<Long> tradingAccountIdList = objectMapper.convertValue(
                            tradingAccountsResult.getData(),
                            new TypeReference<List<Long>>() {}
                    );
                    tradingAccountIds.addAll(tradingAccountIdList);
                } catch (Exception e) {
                    logger.error("Error processing trading accounts data for fundAccount {}: {}", fundAccount, e.getMessage(), e);
                }
            } else {
                logger.warn("No trading accounts found or error from AccountClient for fundAccount {}. Response: code={}, msg={}",
                        fundAccount,
                        tradingAccountsResult != null ? tradingAccountsResult.getCode() : "null",
                        tradingAccountsResult != null ? tradingAccountsResult.getMsg() : "null");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Error calling AccountClient.getTradingAccounts for fundAccount {}:", fundAccount, e);
            return Collections.emptyList();
        }

        if (tradingAccountIds.isEmpty()) {
            logger.info("No trading accounts linked to fundAccount: {} after AccountClient call.", fundAccount);
            return Collections.emptyList();
        }

        List<Holding> holdings = holdingMapper.selectList(
                new QueryWrapper<Holding>().in("trading_account_id", tradingAccountIds)
        );

        if (holdings.isEmpty()) {
            logger.info("No holdings found in DB for tradingAccountIds: {} (fundAccount: {})", tradingAccountIds, fundAccount);
            return Collections.emptyList();
        }
        List<HoldingDTO> holdingDTOs = holdings.stream().map(holdingEntity -> {
            HoldingDTO dto = new HoldingDTO();
            dto.setTradingAccountId(holdingEntity.getTradingAccountId());
            dto.setProductId(holdingEntity.getProductId());
            return dto;
        }).collect(Collectors.toList());

        logger.info("Returning {} holding DTOs for fundAccount: {}", holdingDTOs.size(), fundAccount);
        return holdingDTOs;
    }
}