package com.swyp3.skin.recommendation.product.policy;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DrySkinOrderingPolicy implements ProductOrderingPolicy{

    @Override
    public List<RecommendedProduct> apply(List<RecommendedProduct> products, List<SkinResultGroupScore> topScores) {

        if (!isDry(topScores)) {
            return products;
        }

        return products.stream()
                .sorted(Comparator.comparing(this::isLotionOrEmulsion))
                .toList();
    }

    private boolean isDry(List<SkinResultGroupScore> topScores) {

        if (topScores == null || topScores.size() < 2) {
            return false;
        }

        IngredientGroup group1 = topScores.get(0).getIngredientGroup();
        IngredientGroup group2 = topScores.get(1).getIngredientGroup();

        if (group1 == IngredientGroup.HYDRATION &&
                (group2 == IngredientGroup.SEBUM_CONTROL || group2 == IngredientGroup.ACNE)) {
            return false;
        }

        return group1 == IngredientGroup.HYDRATION
                || group1 == IngredientGroup.BARRIER
                || group1 == IngredientGroup.ANTI_AGING;
    }

    private boolean isLotionOrEmulsion(RecommendedProduct product) {
        ProductCategory productCategory = product.getProduct().getCategory();
        return productCategory == ProductCategory.LOTION || productCategory == ProductCategory.EMULSION;
    }
}
