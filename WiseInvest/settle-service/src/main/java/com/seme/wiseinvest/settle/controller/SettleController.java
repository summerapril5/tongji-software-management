package com.seme.wiseinvest.settle.controller;

import com.seme.wiseinvest.api.OurSystem; // 系统信息类
import com.seme.wiseinvest.api.client.SettleClient; // 调用结算相关API的客户端
import com.seme.wiseinvest.settle.service.SettleService; // 结算服务类
import lombok.RequiredArgsConstructor; // Lombok注解，自动注入 final 字段
import com.seme.wiseinvest.common.domain.Result; // 通用结果封装类

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@RestController // 标识为 REST 控制器，方法默认返回 JSON 格式
@RequestMapping("/settle") // 设置当前控制器的请求路径前缀
@RequiredArgsConstructor // 自动为 final 字段注入构造函数参数（用于 settleService）
public class SettleController {

    private final SettleService settleService; // 结算业务服务

    // 用于格式化日期为 yyyy-MM-dd 格式
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private SettleClient settleClient; // 调用远程结算系统的客户端

    /**
     * 获取当前系统状态（由后端结算模块提供）
     */
    @PostMapping("/system")
    public OurSystem getSystem() {
        return settleService.getSystem();
    }

    /**
     * 初始化交易日
     * @return 初始化成功或失败信息
     */
    @PostMapping("/init")
    public Result initializeDay() {
        return settleService.initializeDay() ? Result.success() : Result.error("日初始化失败");
    }

    /**
     * 获取前端系统状态
     * @return 包装的系统信息
     */
    @GetMapping
    public Result getFrontSystem() {
        return Result.success(settleService.getSystem());
    }

    /**
     * 接收市场行情数据
     * @return 是否接收成功
     */
    @PostMapping("/market")
    public Result receiveMarketData() {
        return settleService.receiveMarketData() ? Result.success() : Result.error("接收行情失败");
    }

    /**
     * 确认所有有效申购交易
     * @return 是否确认成功
     */
    @PostMapping("/subscription")
    public Result confirmSubscriptions() {
        return settleService.confirmSubscriptions() ? Result.success() : Result.error("申购确认失败");
    }

    /**
     * 确认所有有效赎回交易
     * @return 是否确认成功
     */
    @PostMapping("/redemption")
    public Result confirmRedemptions() {
        return settleService.confirmRedemptions() ? Result.success() : Result.error("赎回确认失败");
    }

    /**
     * 停止接受当日交易申请
     * @return 是否操作成功
     */
    @PostMapping("/stop")
    public Result stopDailyApplications() {
        return settleService.stopDailyApplications() ? Result.success() : Result.error("停止申请失败");
    }

    /**
     * 导出结算相关数据
     * @return 是否导出成功
     */
    @PostMapping("/export")
    public Result exportData() {
        return settleService.exportData() ? Result.success() : Result.error("数据导出失败");
    }

    /**
     * 获取系统中记录的交易日期（格式化为字符串）
     * @return 格式化后的交易日期或异常信息
     */
    @GetMapping("/system/transaction-date")
    public Result getTransactionDate() {
        try {
            // 从服务层获取系统对象
            OurSystem ourSystem = settleService.getNetValueSystem();
            // 从系统对象中提取交易日期，并格式化为字符串
            String rawDateStr = ourSystem.getTransactionDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(formatter);
            return Result.success(rawDateStr);
        } catch (DateTimeParseException e) {
            return Result.error("Failed to parse date: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("An error occurred while fetching the transaction date: " + e.getMessage());
        }
    }
}
