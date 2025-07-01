package com.seme.wiseinvest.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.seme.wiseinvest.account.domain.TradingAccount;
import com.seme.wiseinvest.account.domain.dto.BankcardDTO;
import com.seme.wiseinvest.account.domain.vo.BankcardVO;
import com.seme.wiseinvest.account.mapper.BankcardMapper;
import com.seme.wiseinvest.account.mapper.TradingAccountMapper;
import com.seme.wiseinvest.api.Bankcard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingAccountServiceImplTest {

    @Mock
    private BankcardMapper bankcardMapper;

    @Mock
    private TradingAccountMapper tradingAccountMapper;

    @InjectMocks
    private TradingAccountServiceImpl tradingAccountService;

    @Test
    void addBankcard_success_newAccount() {
        BankcardDTO bankcardDTO = new BankcardDTO();
        bankcardDTO.setFundAccount(1L);
        bankcardDTO.setBankcardNumber("1234567890");

        // Mock: card not active elsewhere
        when(tradingAccountMapper.selectCount(argThat(qw -> qw.getSqlSelect().contains("is_deleted = 0") && qw.getSqlSelect().contains("bankcard_number = '1234567890'")))).thenReturn(0L);
        // Mock: no existing record for this fundAccount and bankcardNumber combination
        when(tradingAccountMapper.selectOne(argThat(qw -> qw.getSqlSelect().contains("fund_account = 1") && qw.getSqlSelect().contains("bankcard_number = '1234567890'")))).thenReturn(null);
        // Mock: bankcard does not exist in bankcard table
        when(bankcardMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        // Mock insert behavior for tradingAccountMapper
        when(tradingAccountMapper.insert(any(TradingAccount.class))).thenAnswer(invocation -> {
            TradingAccount ta = invocation.getArgument(0);
            ta.setTradingAccountId(100L); // Simulate ID generation
            return 1;
        });

        String result = tradingAccountService.addBankcard(bankcardDTO);

        assertEquals("100", result);
        verify(tradingAccountMapper, times(1)).insert(any(TradingAccount.class));
        verify(bankcardMapper, times(1)).insert(any(Bankcard.class));
    }

    @Test
    void addBankcard_success_reactivateDeletedAccount() {
        BankcardDTO bankcardDTO = new BankcardDTO();
        bankcardDTO.setFundAccount(1L);
        bankcardDTO.setBankcardNumber("1234567890");

        TradingAccount deletedAccount = new TradingAccount(100L, 1L, "1234567890", true);

        // Mock: card not active elsewhere
        when(tradingAccountMapper.selectCount(argThat(qw -> qw.getSqlSelect().contains("is_deleted = 0") && qw.getSqlSelect().contains("bankcard_number = '1234567890'")))).thenReturn(0L);
        // Mock: existing deleted record for this fundAccount and bankcardNumber combination
        when(tradingAccountMapper.selectOne(argThat(qw -> qw.getSqlSelect().contains("fund_account = 1") && qw.getSqlSelect().contains("bankcard_number = '1234567890'")))).thenReturn(deletedAccount);
        when(tradingAccountMapper.updateById(any(TradingAccount.class))).thenReturn(1);

        String result = tradingAccountService.addBankcard(bankcardDTO);

        assertEquals("100", result);
        assertFalse(deletedAccount.isDeleted()); // Check if isDeleted is set to false
        verify(tradingAccountMapper, times(1)).updateById(deletedAccount);
        verify(bankcardMapper, never()).insert(any(Bankcard.class)); // Should not insert into bankcard table again
    }

    @Test
    void addBankcard_failure_cardAlreadyActive() {
        BankcardDTO bankcardDTO = new BankcardDTO();
        bankcardDTO.setFundAccount(1L);
        bankcardDTO.setBankcardNumber("1234567890");

        // Mock: card is active elsewhere
        when(tradingAccountMapper.selectCount(argThat(qw -> qw.getSqlSelect().contains("is_deleted = 0") && qw.getSqlSelect().contains("bankcard_number = '1234567890'")))).thenReturn(1L);

        String result = tradingAccountService.addBankcard(bankcardDTO);

        assertEquals("该卡号已有交易账户", result);
        verify(tradingAccountMapper, never()).insert(any(TradingAccount.class));
        verify(tradingAccountMapper, never()).updateById(any(TradingAccount.class));
    }

    @Test
    void addBankcard_failure_unknownError_accountExistsAndNotDeleted() {
        BankcardDTO bankcardDTO = new BankcardDTO();
        bankcardDTO.setFundAccount(1L);
        bankcardDTO.setBankcardNumber("1234567890");

        TradingAccount existingActiveAccount = new TradingAccount(100L, 1L, "1234567890", false);

        // Mock: card not active elsewhere (this condition won't be hit if the next one is true)
        when(tradingAccountMapper.selectCount(argThat(qw -> qw.getSqlSelect().contains("is_deleted = 0") && qw.getSqlSelect().contains("bankcard_number = '1234567890'")))).thenReturn(0L);
        // Mock: existing active record for this fundAccount and bankcardNumber combination
        when(tradingAccountMapper.selectOne(argThat(qw -> qw.getSqlSelect().contains("fund_account = 1") && qw.getSqlSelect().contains("bankcard_number = '1234567890'")))).thenReturn(existingActiveAccount);

        String result = tradingAccountService.addBankcard(bankcardDTO);

        assertEquals("未知错误", result);
        verify(tradingAccountMapper, never()).insert(any(TradingAccount.class));
        verify(tradingAccountMapper, never()).updateById(any(TradingAccount.class));
    }


    @Test
    void deleteBankcard_success() {
        long tradingAccountId = 100L;
        when(tradingAccountMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(1);

        boolean result = tradingAccountService.deleteBankcard(tradingAccountId);

        assertTrue(result);
        ArgumentCaptor<UpdateWrapper<TradingAccount>> captor = ArgumentCaptor.forClass(UpdateWrapper.class);
        verify(tradingAccountMapper, times(1)).update(isNull(), captor.capture());
        assertTrue(captor.getValue().getSqlSet().contains("is_deleted = true"));
        assertTrue(captor.getValue().getSqlSegment().contains("trading_account_id = " + tradingAccountId));
    }

    @Test
    void deleteBankcard_failure() {
        long tradingAccountId = 100L;
        when(tradingAccountMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(0);

        boolean result = tradingAccountService.deleteBankcard(tradingAccountId);

        assertFalse(result);
    }

    @Test
    void getBankcards_success() {
        long fundAccount = 1L;
        TradingAccount ta1 = new TradingAccount(100L, fundAccount, "card1", false);
        TradingAccount ta2 = new TradingAccount(101L, fundAccount, "card2", false);
        when(tradingAccountMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(ta1, ta2));

        List<BankcardVO> result = tradingAccountService.getBankcards(fundAccount);

        assertEquals(2, result.size());
        assertEquals("card1", result.get(0).getBankcardNumber());
        assertEquals("card2", result.get(1).getBankcardNumber());
        verify(tradingAccountMapper, times(1)).selectList(argThat(qw -> qw.getSqlSelect().contains("fund_account = " + fundAccount) && qw.getSqlSelect().contains("is_deleted = 0")));
    }

    @Test
    void getBankcardByTradingAccountId_success() {
        long tradingAccountId = 100L;
        Bankcard bankcard = new Bankcard("card1", 200.0);
        when(bankcardMapper.getBankcardByTradingAccountId(tradingAccountId)).thenReturn(bankcard);

        Bankcard result = tradingAccountService.getBankcardByTradingAccountId(tradingAccountId);

        assertNotNull(result);
        assertEquals("card1", result.getBankcardNumber());
    }

    @Test
    void updateBalance_success() {
        Bankcard bankcard = new Bankcard("card1", 300.0);
        when(bankcardMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(1);

        boolean result = tradingAccountService.updateBalance(bankcard);

        assertTrue(result);
        ArgumentCaptor<UpdateWrapper<Bankcard>> captor = ArgumentCaptor.forClass(UpdateWrapper.class);
        verify(bankcardMapper, times(1)).update(isNull(), captor.capture());
        assertTrue(captor.getValue().getSqlSet().contains("balance = 300.0"));
        assertTrue(captor.getValue().getSqlSegment().contains("bankcard_number = 'card1'"));
    }

    @Test
    void getBalance_success() {
        String bankcardNumber = "card1";
        Bankcard bankcard = new Bankcard(bankcardNumber, 500.0);
        when(bankcardMapper.selectOne(any(QueryWrapper.class))).thenReturn(bankcard);

        double balance = tradingAccountService.getBalance(bankcardNumber);

        assertEquals(500.0, balance);
        verify(bankcardMapper, times(1)).selectOne(argThat(qw -> qw.getSqlSelect().contains("bankcard_number = '"+bankcardNumber+"'")));
    }

    @Test
    void getTradingAccounts_success() {
        Long fundAccount = 1L;
        TradingAccount ta1 = new TradingAccount(100L, fundAccount, "card1", false);
        TradingAccount ta2 = new TradingAccount(101L, fundAccount, "card2", false);
        when(tradingAccountMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(ta1, ta2));

        List<String> result = tradingAccountService.getTradingAccounts(fundAccount);

        assertEquals(2, result.size());
        assertEquals("100", result.get(0));
        assertEquals("101", result.get(1));
        verify(tradingAccountMapper, times(1)).selectList(argThat(qw -> qw.getSqlSelect().contains("fund_account = " + fundAccount) && qw.getSqlSelect().contains("is_deleted = 0")));
    }
}