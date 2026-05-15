package com.swyp3.skin.recommendation.ingredient.calculator;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.recommendation.ingredient.model.enums.SkinState;

import java.util.HashMap;
import java.util.Map;

// step 2
public class ScoreCalculator {
    public Map<IngredientGroup, Double> calculate(Map<SkinState, Integer> stateVector) {

        // 각 성분군별 누적 점수
        Map<IngredientGroup, Double> result = new HashMap<>();
        // 상태 -> 성분군 매핑 테이블
        Map<SkinState, Map<IngredientGroup, Double>> mapping = ScoreMapping.MAPPING;

        for (SkinState state : stateVector.keySet()) {
            // 해당 상태의 점수 => ex.DRYNESS = 70
            int stateScore = stateVector.get(state);

            // 해당 상태가 영향을 주는 성분군 목록(ScoreMapping)
            Map<IngredientGroup, Double> targets = mapping.get(state);
            // 매핑 없으면 스킵 , 근데 그럴일 없음
            if (targets == null) continue;
            // 성분군 별 점수 분재
            for (IngredientGroup group : targets.keySet()) {
                // 가중치
                double weight = targets.get(group);
                // 상태 점수 x 가중치
                double addScore = stateScore * weight;
                // 없으면 0 있으면 누적
                result.put(group, result.getOrDefault(group, 0.0) + addScore);
            }
        }

        return result;
    }
}
