package com.seme.wiseinvest.recommendation.controller;

import com.seme.wiseinvest.product.domain.Product;
import com.seme.wiseinvest.recommendation.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
public class RecommendationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void getMyRecommendations_shouldReturnProductList() throws Exception {
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setName("Growth Fund");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setName("Income Fund");

        when(recommendationService.getPersonalizedRecommendations(10001L))
                .thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/recommendations")
                        .header("X-Fund-Account", 10001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].name").value("Growth Fund"))
                .andExpect(jsonPath("$.data[1].productId").value(2));
    }
}
