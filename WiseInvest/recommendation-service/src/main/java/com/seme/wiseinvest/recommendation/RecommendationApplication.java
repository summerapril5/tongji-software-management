package com.seme.wiseinvest.recommendation;

import com.seme.wiseinvest.api.client.AccountClient;
import com.seme.wiseinvest.api.client.ProductClient;
import com.seme.wiseinvest.api.client.TransactionClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {AccountClient.class, ProductClient.class, TransactionClient.class})
public class RecommendationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendationApplication.class, args);
    }
}