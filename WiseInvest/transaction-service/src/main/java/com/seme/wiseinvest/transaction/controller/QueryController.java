package com.seme.wiseinvest.transaction.controller;

import com.seme.wiseinvest.common.domain.Result; // 通用结果封装类
import com.seme.wiseinvest.transaction.domain.dto.HoldingDTO; // 持仓数据传输对象
import com.seme.wiseinvest.transaction.service.QueryService; // 查询服务接口
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 声明该类是一个 REST 控制器，返回值默认作为 JSON 响应体
@RequestMapping("/query") // 为该控制器的所有方法设置统一的请求前缀

public class QueryController {

    @Autowired
    private QueryService queryService; // 自动注入 QueryService，用于业务查询

    /**
     * 查询某个基金账户的交易记录
     * @param fundAccount 基金账户号（字符串类型，需转为 Long）
     * @return 交易记录列表的封装结果
     */
    @GetMapping("/transactions") // 映射 GET 请求 /query/transactions
    public Result getTransactions(String fundAccount) {
        return Result.success(queryService.getTransactions(Long.parseLong(fundAccount))); // 返回封装的交易记录数据
    }

    /**
     * 查询某个基金账户的持仓信息
     * @param fundAccount 基金账户号（通过 @RequestParam 显式绑定参数）
     * @return 持仓数据列表的封装结果
     */
    @GetMapping("/holdings") // 映射 GET 请求 /query/holdings
    public Result getHoldingsByFundAccount(@RequestParam Long fundAccount) {
        List<HoldingDTO> holdings = queryService.getHoldingsByFundAccount(fundAccount); // 调用服务层获取持仓数据
        return Result.success(holdings); // 返回封装的持仓数据结果
    }
}
