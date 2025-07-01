package com.seme.wiseinvest.account.service;

import com.seme.wiseinvest.account.domain.dto.BankcardDTO;
import com.seme.wiseinvest.api.Bankcard;
import com.seme.wiseinvest.account.domain.vo.BankcardVO;

import java.util.List;

public interface TradingAccountService {
    String addBankcard(BankcardDTO bankcardDTO);

    boolean deleteBankcard(long tradingAccountId);

    List<BankcardVO> getBankcards(long fundAccount);

    Bankcard getBankcardByTradingAccountId(long tradingAccountId);

    boolean updateBalance(Bankcard bankcard);

    double getBalance(String bankcardNumber);

    List<String> getTradingAccounts(Long fundAccount);
}
