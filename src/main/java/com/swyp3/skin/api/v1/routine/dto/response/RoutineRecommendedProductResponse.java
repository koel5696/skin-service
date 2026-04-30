package com.swyp3.skin.api.v1.routine.dto.response;

import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.domain.routine.domain.enums.RoutineStepCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "루틴 추천/상세에서 사용하는 제품 정보")
public record RoutineRecommendedProductResponse(

        @Schema(description = "제품 ID", example = "12")
        Long productId,

        @Schema(description = "제품명", example = "시카 진정 토너")
        String name,

        @Schema(description = "가격", example = "17,000")
        int price,

        @Schema(description = "상품 원래 카테고리", example = "TONER")
        ProductCategory productCategory,

        @Schema(description = "제품 대표 이미지 URL", example = "https://cdn.example.com/products/12.png")
        String imageUrl,

        @Schema(description = "루틴 단계 카테고리", example = "MOISTURIZER")
        RoutineStepCategory routineStepCategory
) {
}
