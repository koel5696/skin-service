package com.swyp3.skin.recommendation.ingredient.calculator;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.recommendation.ingredient.model.enums.SkinState;

import java.util.Map;

public final class ScoreMapping {

    private ScoreMapping() {
    }

    public static final Map<SkinState, Map<IngredientGroup, Double>> MAPPING = Map.of(
            SkinState.DRYNESS, Map.of( // DRYNESS (건조)
                    IngredientGroup.HYDRATION, 1.0,
                    IngredientGroup.BARRIER, 1.0 // 장벽 강화 (핵심)
            ),
            SkinState.SEBUM, Map.of( // SEBUM (피지)
                    IngredientGroup.SEBUM_CONTROL, 1.3, // 피지 조절 (핵심)
                    IngredientGroup.ACNE, 0.4 // 트러블 예방 (약하게)
            ),
            SkinState.ACNE, Map.of( // ACNE (트러블)
                    IngredientGroup.ACNE, 1.3,// 진정
                    IngredientGroup.SOOTHING, 0.3
            ),
            SkinState.SENSITIVITY, Map.of( // SENSITIVITY (민감)
                    IngredientGroup.SOOTHING, 1.25, // 장벽 강화
                    IngredientGroup.BARRIER, 0.8
            ),
            SkinState.PIGMENTATION, Map.of( // PIGMENTATION (색소)
                    IngredientGroup.BRIGHTENING, 1.25, // 미백/톤 개선 (핵심)
                    IngredientGroup.TURNOVER, 0.5 // 각질/재생 보조
            ),
            SkinState.AGING, Map.of( // AGING (노화)
                    IngredientGroup.ANTI_AGING, 1.4, // 핵심 (탄력/주름 개선)
                    IngredientGroup.TURNOVER, 0.35 // 보조 (재생)
            ));
}
