package com.seme.wiseinvest.api.client;

import com.seme.wiseinvest.api.Bankcard;
import com.seme.wiseinvest.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("account-service")
public interface AccountClient {
    @PostMapping("/account/bankcard")
    Bankcard getBankcard(@RequestParam long tradingAccountId);

    @PatchMapping("/account/balance")
    boolean updateBalance(@RequestBody Bankcard bankcard);

    @GetMapping("/account/customer/byFundAccount")
    Result getCustomerByFundAccount(@RequestParam("fundAccount") Long fundAccount);

    @GetMapping("/account/trading_accounts")
    Result getTradingAccounts(@RequestParam String fundAccount);
//    @GetMapping("/account/balance")
//    double getBalance(@RequestParam String bankcardNumber);
}

