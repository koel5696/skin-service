package com.swyp3.skin.domain.routine.service;

import com.swyp3.skin.api.v1.routine.dto.response.*;
import com.swyp3.skin.domain.product.domain.entity.Product;
import com.swyp3.skin.domain.routine.domain.enums.RoutineStepCategory;
import com.swyp3.skin.domain.routine.domain.enums.RoutineType;
import com.swyp3.skin.domain.routine.exception.RoutineErrorCode;
import com.swyp3.skin.domain.routine.exception.RoutineException;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import com.swyp3.skin.recommendation.ux.SkinProfileService;
import com.swyp3.skin.recommendation.ux.SkinUxProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoutineRecommendationService {

    private final RoutineCompositionService routineCompositionService;
    private final SkinProfileService skinProfileService;

    public RoutineRecommendationResponse recommend(
        List<RecommendedProduct> recommendedProducts,
        SkinResult skinResult) {
        Map<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> productsByStepCategory =
                routineCompositionService.compose(recommendedProducts);
        return toRecommendationResponse(productsByStepCategory, skinResult);
    }

    private RoutineRecommendationResponse toRecommendationResponse(
            Map<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> productsByStepCategory,
            SkinResult skinResult
    ) {
        RoutineSectionResponse amRoutine = null;
        RoutineSectionResponse pmRoutine = null;
        SkinUxProfile uxProfile = skinProfileService.getProfile(skinResult.getId());

        for (Map.Entry<RoutineType, Map<RoutineStepCategory, List<RecommendedProduct>>> routineTypeEntry : productsByStepCategory.entrySet()) {
            List<RoutineRecommendedProductResponse> routineProducts = new ArrayList<>();

            for (Map.Entry<RoutineStepCategory, List<RecommendedProduct>> stepCategoryEntry : routineTypeEntry.getValue().entrySet()) {
                List<RecommendedProduct> categoryProducts = stepCategoryEntry.getValue();
                if(categoryProducts == null || categoryProducts.isEmpty()) {
                    throw new RoutineException(RoutineErrorCode.ROUTINE_DATA_INCOMPLETE);
                }
                routineProducts.add(toProductResponse(stepCategoryEntry.getValue().get(0), stepCategoryEntry.getKey()));
            }
            RoutineSectionResponse sectionResponse = new RoutineSectionResponse(
                    routineTypeEntry.getKey(),
                    routineProducts
            );
            switch (routineTypeEntry.getKey()) {
                case AM -> amRoutine = sectionResponse;
                case PM -> pmRoutine = sectionResponse;
            }
        }
        if(amRoutine == null || pmRoutine == null) {
            throw new RoutineException(RoutineErrorCode.ROUTINE_DATA_INCOMPLETE);
        }

        return new RoutineRecommendationResponse(
                skinResult.getId(),
                uxProfile.skinType(),
                uxProfile.subtitle(),
                uxProfile.routineSummary(),
                amRoutine,
                pmRoutine
        );
    }

    private RoutineRecommendedProductResponse toProductResponse(
            RecommendedProduct recommendedProduct,
            RoutineStepCategory routineStepCategory
    ) {
        Product product = recommendedProduct.getProduct();
        return new RoutineRecommendedProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                routineStepCategory
        );
    }
}
