package com.swyp3.skin.domain.product.service;

import com.swyp3.skin.global.config.CacheConfig;
import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import com.swyp3.skin.recommendation.product.service.ProductRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductRecommendCacheService {
    private final static int PER_CATEGORY_LIMIT = 5;

    private final ProductRecommendationService productRecommendationService;

    @Cacheable(cacheNames = CacheConfig.PRODUCT_RECOMMEND_CACHE, key = "#skinResultId")
    public List<RecommendedProduct> getOrCalculate(Long skinResultId) {
        List<RecommendedProduct> recommended = productRecommendationService.recommend(skinResultId);
        Map<ProductCategory, Integer> counts = new EnumMap<>(ProductCategory.class);
        List<RecommendedProduct> top5ByCategory = new ArrayList<>();

        for (RecommendedProduct product : recommended) {
            ProductCategory category = product.getProduct().getCategory();
            int count = counts.getOrDefault(category, 0);

            if (count >= PER_CATEGORY_LIMIT) {
                continue;
            }

            top5ByCategory.add(product);
            counts.put(category, count + 1);
        }

        return top5ByCategory;
    }

    @CacheEvict(cacheNames = CacheConfig.PRODUCT_RECOMMEND_CACHE, key = "#skinResultId")
    public void evict(Long skinResultId) {
    }

    @CacheEvict(cacheNames = CacheConfig.PRODUCT_RECOMMEND_CACHE, allEntries = true)
    public void evictAll() {
    }
}
