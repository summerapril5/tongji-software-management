package com.seme.wiseinvest.api.client;

import com.seme.wiseinvest.api.NetValue;
import com.seme.wiseinvest.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Date;

@FeignClient("product-service")
public interface ProductClient {
//    @PostMapping("/product/level")
//    int getLevel(@RequestParam Integer productId);

    @PostMapping("/product/net_values")
    List<NetValue> getLatestNetValues();

    @PostMapping("/product/net_value/insert")
    boolean insertNetValue(@RequestBody NetValue netValue);

    @PostMapping("/product/net_value/{productId}/{date}")
    Double getNetValue(@PathVariable("productId") Integer productId,
                       @PathVariable("date") Date date);
    @GetMapping("/product/list")
    Result getAllProducts();
}
