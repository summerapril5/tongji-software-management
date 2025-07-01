package com.seme.wiseinvest.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seme.wiseinvest.account.domain.Customer;
import com.seme.wiseinvest.account.domain.dto.CustomerDTO;
import com.seme.wiseinvest.account.domain.dto.UpdateInfoDTO;
import com.seme.wiseinvest.account.domain.vo.CustomerVO;

import java.util.List;

public interface CustomerService extends IService<Customer> {
    Long createAccount(CustomerDTO customerDTO);

    boolean updateRiskLevel(Long fundAccount, int riskLevel);

    boolean updateInfo(UpdateInfoDTO updateInfoDTO);

    List<CustomerVO> getCustomers(int pageNum, int pageSize, String key);

    Customer findCustomerByFundAccount(Long fundAccount);
}