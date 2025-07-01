package com.seme.wiseinvest.api.bo;

import lombok.Data;

@Data
public class RedemptionBO {
    private Long transactionId;
    private Integer productId;
    private Double redemptionShares;
} 