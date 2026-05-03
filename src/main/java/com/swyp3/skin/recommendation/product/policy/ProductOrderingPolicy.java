package com.swyp3.skin.recommendation.product.policy;

import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;

import java.util.List;

public interface ProductOrderingPolicy {

    List<RecommendedProduct> apply(
            List<RecommendedProduct> products,
            List<SkinResultGroupScore> topScores
    );
}
