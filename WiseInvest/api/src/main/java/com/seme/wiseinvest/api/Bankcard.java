package com.seme.wiseinvest.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
@AllArgsConstructor
public class Bankcard {
    private String bankcardNumber;
    private double balance;
}
