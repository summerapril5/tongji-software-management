package com.seme.wiseinvest.recommendation.service;

import com.seme.wiseinvest.product.domain.Product;
import java.util.List;

public interface RecommendationService {
    List<Product> getPersonalizedRecommendations(Long fundAccount);
}