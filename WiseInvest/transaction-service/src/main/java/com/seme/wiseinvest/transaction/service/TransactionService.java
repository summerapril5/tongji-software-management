package com.seme.wiseinvest.transaction.service;

import com.seme.wiseinvest.transaction.domain.Holding;
import com.seme.wiseinvest.transaction.domain.dto.RedemptionDTO;
import com.seme.wiseinvest.transaction.domain.dto.SubscriptionDTO;
import com.seme.wiseinvest.api.bo.SubscriptionBO;
import com.seme.wiseinvest.api.bo.RedemptionBO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    long submitSubscription(SubscriptionDTO subscriptionDTO);
    Holding getHolding(long tradingAccountId, int productId);
    long submitRedemption(RedemptionDTO redemptionDTO);
    boolean cancelTransaction(long transactionId);
    List<SubscriptionBO> getValidSubscriptionBOs(Date date);
    List<RedemptionBO> getValidRedemptionBOs(Date date);
    boolean confirmSubscriptionBatch(Map<Long, Double> transactionIdToShares);
    boolean confirmRedemptionBatch(Map<Long, Double> transactionIdToAmount);
}
