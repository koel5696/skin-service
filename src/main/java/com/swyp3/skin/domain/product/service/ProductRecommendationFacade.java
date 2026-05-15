package com.swyp3.skin.domain.product.service;

import com.swyp3.skin.api.v1.product.dto.response.ProductListResponse;
import com.swyp3.skin.domain.common.pagination.CursorPaginationUtils;
import com.swyp3.skin.domain.common.pagination.SliceResult;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.domain.skinresult.service.SkinResultGroupScoreService;
import com.swyp3.skin.domain.skinresult.service.SkinResultService;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import com.swyp3.skin.recommendation.product.policy.ProductOrderingPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRecommendationFacade {

    private final ProductService productService;
    private final SkinResultService skinResultService;
    private final ProductRecommendCacheService productRecommendCacheService;
    private final ProductOrderingPolicy drySkinOrderingPolicy;
    private final SkinResultGroupScoreService skinResultGroupScoreService;


    public ProductListResponse getRecommendedProducts(
            Long userId,
            Long skinResultId,
            List<String> categories,
            Long cursor,
            int size) {

        SkinResult skinResult = (skinResultId != null)
                ? skinResultService.getSkinResultById(skinResultId,userId)
                : skinResultService.getLatestByUserId(userId);

        List<RecommendedProduct> recommended =
                productRecommendCacheService.getOrCalculate(skinResult.getId());

        List<RecommendedProduct> filtered =
                productService.filter(recommended, categories);

        List<SkinResultGroupScore> topScores =
                skinResultGroupScoreService.getTop2ScoresByResultId(skinResult.getId());

        List<RecommendedProduct> ordered =
                drySkinOrderingPolicy.apply(filtered, topScores);

        SliceResult<RecommendedProduct> sliced =
                CursorPaginationUtils.sliceWithCursor(
                        ordered,
                        cursor,
                        size,
                        recommendedProduct ->
                                recommendedProduct.getProduct().getId());


        return ProductListResponse.from(
                sliced.items(),
                sliced.hasNext()
        );
    }
}
