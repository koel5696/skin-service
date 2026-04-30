package com.swyp3.skin.recommendation.product.calculator;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.recommendation.product.dto.ProductScore;
import com.swyp3.skin.recommendation.product.dto.ProductSupply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ProductScoreCalculator {

    public List<ProductScore> calculate(
            Map<IngredientGroup, Double> need,
            List<ProductSupply> supplies
    ) {

        // top 3 추출
        List<IngredientGroup> topGroups = need.entrySet().stream()
                .sorted(Map.Entry.<IngredientGroup, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        IngredientGroup top1 = topGroups.get(0);
        IngredientGroup top2 = topGroups.get(1);

        List<ProductScore> result = new ArrayList<>();

        for (ProductSupply supply : supplies) {

            double score = calculateScore(
                    need,
                    supply.getSupply(),
                    topGroups,
                    top1,
                    top2
            );

            result.add(new ProductScore(
                    supply.getProductId(),
                    score
            ));
        }

        return result;
    }

    private double calculateScore(
            Map<IngredientGroup, Double> need,
            Map<IngredientGroup, Double> supply,
            List<IngredientGroup> topGroups,
            IngredientGroup top1,
            IngredientGroup top2) {

        double core = 0.0; // 중요한 성분 매칭
        double rest = 0.0; // 덜 중요한 성분 매칭

        for (IngredientGroup ingredientGroup : need.keySet()) {
            Double n = need.getOrDefault(ingredientGroup, 0.0);
            Double s = supply.getOrDefault(ingredientGroup, 0.0);

            // 상품 성분군에 없는 제품은 아예 제거
            double value = n * s;

            // 중요한 성분을 따로 모아서 강하게 반영 준비
            // 사용자에게 필요한 성분이라면 중요성분이 아니더라도 일단 반영은 하기
            // 대신에 rest로 분류
            if (topGroups.contains(ingredientGroup)) {
                core += value;
            } else {
                rest += value;
            }
        }

        double score = (core * 1.2) + (rest * 0.5);

        // 상품이 사용자의 핵심성분을 얼마나 포함하고있는지 계산
        double coverage = topGroups.stream()
                .mapToDouble(group -> supply.getOrDefault(group, 0.0))
                .sum();

        // 낮으면 더낮게 조정
        if (coverage < 0.2) {
            score *= 0.5;
        }

        // 제일 중요한 성분을 갖고있다면 좀더 플러스
        if (supply.containsKey(top1)) {
            score += 0.1;
        }

        if (top2 != null && supply.containsKey(top2)) {
            score += 0.05;
        }

        log.info("score = {}", score);

        // 비선형 강화
        return score * score;
    }


}