package com.seme.wiseinvest.transaction.controller;

import com.seme.wiseinvest.common.domain.Result; // 通用返回结果封装类
import com.seme.wiseinvest.transaction.domain.dto.RedemptionDTO; // 赎回请求数据传输对象
import com.seme.wiseinvest.transaction.domain.dto.SubscriptionDTO; // 申购请求数据传输对象
import com.seme.wiseinvest.transaction.service.TransactionService; // 交易相关业务逻辑接口
import com.seme.wiseinvest.api.bo.SubscriptionBO; // 有效申购业务对象
import com.seme.wiseinvest.api.bo.RedemptionBO; // 有效赎回业务对象
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController // 标识为REST风格的控制器，所有方法默认返回JSON格式
@RequestMapping("/transaction") // 为控制器下所有接口统一设置请求前缀
public class TransactionController {

    @Autowired
    private TransactionService transactionService; // 注入交易服务类

    /**
     * 提交申购请求
     * @param subscriptionDTO 申购请求数据
     * @return 返回申购执行结果
     */
    @PostMapping("/subscription")
    public Result submitSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        long result = transactionService.submitSubscription(subscriptionDTO);
        // 返回值为1表示余额不足
        if(result == 1L)
            return Result.error("余额不足");
        return Result.success(result); // 返回成功信息及结果
    }

    /**
     * 提交赎回请求
     * @param redemptionDTO 赎回请求数据
     * @return 返回赎回执行结果
     */
    @PostMapping("/redemption")
    public Result submitRedemption(@RequestBody RedemptionDTO redemptionDTO) {
        long result = transactionService.submitRedemption(redemptionDTO);
        // 返回值为1表示份额不足
        if(result == 1L)
            return Result.error("份额不足");
        return Result.success(result); // 返回成功信息及结果
    }

    /**
     * 撤销交易请求
     * @param transactionId 要撤销的交易ID
     * @return 是否撤销成功
     */
    @PostMapping("/cancel")
    public Result cancelTransaction(@RequestParam String transactionId) {
        return transactionService.cancelTransaction(Long.parseLong(transactionId)) ? Result.success() : Result.error("当前交易已超时");
    }

    /**
     * 获取指定日期的所有有效申购交易（用于批量确认）
     * @param date 指定日期
     * @return 有效申购业务对象列表
     */
    @PostMapping("/subscriptions/valid")
    public List<SubscriptionBO> getValidSubscriptionBOs(@RequestParam Date date) {
        return transactionService.getValidSubscriptionBOs(date);
    }

    /**
     * 获取指定日期的所有有效赎回交易（用于批量确认）
     * @param date 指定日期
     * @return 有效赎回业务对象列表
     */
    @PostMapping("/redemptions/valid")
    public List<RedemptionBO> getValidRedemptionBOs(@RequestParam Date date) {
        return transactionService.getValidRedemptionBOs(date);
    }

    /**
     * 申购确认批处理（更新持仓信息）
     * 逻辑：根据 trading_account 和 product_id 修改 holding 表的份额信息
     * @param transactionIdToShares 交易ID到确认份额的映射
     * @return 是否确认成功
     */
    @PostMapping("/subscription/confirm-batch")
    public boolean confirmSubscriptionBatch(@RequestBody Map<Long, Double> transactionIdToShares) {
        return transactionService.confirmSubscriptionBatch(transactionIdToShares);
    }

    /**
     * 赎回确认批处理（更新银行卡余额）
     * 逻辑：根据 trading_account 找到 bankcard_number，并修改其余额
     * @param transactionIdToAmount 交易ID到到账金额的映射
     * @return 是否确认成功
     */
    @PostMapping("/redemption/confirm-batch")
    public boolean confirmRedemptionBatch(@RequestBody Map<Long, Double> transactionIdToAmount) {
        return transactionService.confirmRedemptionBatch(transactionIdToAmount);
    }

}
