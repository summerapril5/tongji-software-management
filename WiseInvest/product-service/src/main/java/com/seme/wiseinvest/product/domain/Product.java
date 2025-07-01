package com.seme.wiseinvest.product.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    @TableId
    private Integer productId;
    private String productName;
    private String productType;
    private Integer riskLevel;
    private Integer productStatus;
    // 新增的字段
    private BigDecimal productAum;
    private Integer productRating;
    private String productTheme;
}
