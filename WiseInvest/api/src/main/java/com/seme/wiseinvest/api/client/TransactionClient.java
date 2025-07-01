package com.seme.wiseinvest.api.client;

import com.seme.wiseinvest.api.bo.SubscriptionBO;
import com.seme.wiseinvest.api.bo.RedemptionBO;
import com.seme.wiseinvest.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@FeignClient("transaction-service")
public interface TransactionClient {
    @PostMapping("/transaction/subscriptions/valid")
    List<SubscriptionBO> getValidSubscriptionBOs(@RequestParam("date") Date date);

    @PostMapping("/transaction/redemptions/valid")
    List<RedemptionBO> getValidRedemptionBOs(@RequestParam("date") Date date);

    @PostMapping("/transaction/subscription/confirm-batch")
    boolean confirmSubscriptionBatch(@RequestBody Map<Long, Double> transactionIdToShares);

    @PostMapping("/transaction/redemption/confirm-batch")
    boolean confirmRedemptionBatch(@RequestBody Map<Long, Double> transactionIdToAmount);

    @GetMapping("/query/holdings")
    Result getHoldingsByFundAccount(@RequestParam("fundAccount") Long fundAccount);
} 