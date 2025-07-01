package com.seme.wiseinvest.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seme.wiseinvest.account.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}