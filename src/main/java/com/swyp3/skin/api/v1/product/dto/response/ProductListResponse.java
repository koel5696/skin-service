package com.swyp3.skin.api.v1.product.dto.response;

import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "추천 제품 조회 응답")
public record ProductListResponse(

        @ArraySchema(
                schema = @Schema(implementation = ProductSummaryResponse.class),
                arraySchema = @Schema(description = "추천 제품 목록")
        )
        List<ProductSummaryResponse> products,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "마지막 상품의 ID", example = "12")
        Long nextCursor
        ) {

    public static ProductListResponse from(
            List<RecommendedProduct> sliced,
            boolean hasNext
    ) {
        List<ProductSummaryResponse> products = sliced.stream()
                .map(ProductSummaryResponse::from)
                .toList();

        Long nextCursor = (hasNext && !sliced.isEmpty())
                ? sliced.get(sliced.size() - 1).getProduct().getId()
                : null;

        return new ProductListResponse(
                products,
                hasNext,
                nextCursor
        );
    }
}
