package com.seme.wiseinvest.recommendation.controller;

import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.product.domain.Product;
import com.seme.wiseinvest.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 获取当前登录用户的个性化推荐
     * fundAccount (用户ID) 从网关解析并放入请求头中
     * @param fundAccount 用户ID
     * @return 推荐的产品列表
     */
    @GetMapping
    public Result getMyRecommendations(@RequestHeader("X-Fund-Account") Long fundAccount) {
        // Service 层返回具体的 List<Product>
        List<Product> recommendedProducts = recommendationService.getPersonalizedRecommendations(fundAccount);

        // Result.success(Object data) 方法接收 Object 类型的参数
        // List<Product> 可以被隐式转换为 Object
        return Result.success(recommendedProducts);
    }
}