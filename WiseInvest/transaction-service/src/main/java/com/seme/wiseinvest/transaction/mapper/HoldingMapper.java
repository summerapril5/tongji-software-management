package com.seme.wiseinvest.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seme.wiseinvest.transaction.domain.Holding;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HoldingMapper extends BaseMapper<Holding> {
}
