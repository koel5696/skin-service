package com.swyp3.skin.domain.routine.service;

import com.swyp3.skin.domain.product.domain.entity.Product;
import com.swyp3.skin.domain.product.domain.enums.ProductUsageTime;
import com.swyp3.skin.domain.routine.domain.enums.RoutineStepCategory;
import com.swyp3.skin.domain.routine.domain.enums.RoutineType;
import com.swyp3.skin.domain.routine.exception.RoutineErrorCode;
import com.swyp3.skin.domain.routine.exception.RoutineException;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RoutineCompositionService {
    public Map<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> compose(List<RecommendedProduct> recommendedProducts) {
        Map<RoutineType, List<RecommendedProduct>> productsByRoutineType =
                groupProductsByRoutineType(recommendedProducts);
        Map<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> productsByStepCategory =
                groupProductsByStepCategory(productsByRoutineType);
        sortProductsByScore(productsByStepCategory);

        return productsByStepCategory;
    }

    private Map<RoutineType, List<RecommendedProduct>> groupProductsByRoutineType(
            List<RecommendedProduct> recommendedProducts
    ) {
        Map<RoutineType, List<RecommendedProduct>> productsByRoutineType = new EnumMap<>(RoutineType.class);
        for (RoutineType routineType : RoutineType.values()) {
            productsByRoutineType.put(routineType, new ArrayList<>()); // 미리 초기화
        }

        for (RecommendedProduct recommendedProduct : recommendedProducts) {
            ProductUsageTime usageTime = recommendedProduct.getProduct().getProductUsageTime();
            if(usageTime == null) {
                throw new RoutineException(RoutineErrorCode.PRODUCT_USAGE_TIME_NOT_DEFINE);
            }
            for (RoutineType routineType : RoutineType.from(usageTime)) {
                productsByRoutineType.get(routineType).add(recommendedProduct);
            }
        }
        return productsByRoutineType;
    }

    private Map<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> groupProductsByStepCategory(
            Map<RoutineType, List<RecommendedProduct>> productsByRoutineType
    ) {
        Map<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> productsByStepCategory =
                new EnumMap<>(RoutineType.class);

        for (Map.Entry<RoutineType, List<RecommendedProduct>> routineTypeEntry : productsByRoutineType.entrySet()) {
            Map<RoutineStepCategory, List<RecommendedProduct>> productsForStepCategory =
                    productsByStepCategory.computeIfAbsent(
                            routineTypeEntry.getKey(),
                            ignored -> new EnumMap<>(RoutineStepCategory.class)
                    );

            for(RoutineStepCategory routineStepCategory : RoutineStepCategory.values()) {
                if(routineTypeEntry.getKey() == RoutineType.PM && routineStepCategory == RoutineStepCategory.SUN_CARE) {
                    continue;
                }
                productsForStepCategory.put(routineStepCategory, new ArrayList<>());
            }

            for (RecommendedProduct recommendedProduct : routineTypeEntry.getValue()) {
                Product product = recommendedProduct.getProduct();
                RoutineStepCategory routineStepCategory =
                        RoutineStepCategory.from(product.getCategory());

                productsForStepCategory.get(routineStepCategory).add(recommendedProduct);
            }
        }
        return productsByStepCategory;
    }

    private void sortProductsByScore(
            Map<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> productsByStepCategory
    ) {
        for (Map<RoutineStepCategory, List<RecommendedProduct>> productsByCategory : productsByStepCategory.values()) {
            for (Map.Entry<RoutineStepCategory, List<RecommendedProduct>> categoryEntry : productsByCategory.entrySet()) {
                List<RecommendedProduct> sortedProducts = categoryEntry.getValue().stream()
                        .sorted(Comparator.comparing(RecommendedProduct::getScore).reversed())
                        .toList();
                categoryEntry.setValue(sortedProducts);
            }
        }
    }
}
