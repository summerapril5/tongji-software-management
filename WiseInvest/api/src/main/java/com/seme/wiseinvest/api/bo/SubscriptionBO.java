package com.seme.wiseinvest.api.bo;

import lombok.Data;

@Data
public class SubscriptionBO {
    private Long transactionId;
    private Integer productId;
    private Double amount;
}