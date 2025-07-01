package com.seme.wiseinvest.account.domain.vo;

import com.seme.wiseinvest.account.domain.TradingAccount;
import lombok.Data;

@Data
public class BankcardVO {
    private Long tradingAccount;
    private String bankcardNumber;

    public BankcardVO(TradingAccount tradingAccount) {
        this.tradingAccount = tradingAccount.getTradingAccountId();
        this.bankcardNumber = tradingAccount.getBankcardNumber();
    }
}
