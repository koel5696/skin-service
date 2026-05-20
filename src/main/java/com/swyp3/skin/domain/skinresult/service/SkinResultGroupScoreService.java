package com.swyp3.skin.domain.skinresult.service;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.domain.skinresult.repository.SkinResultGroupScoreRepository;
import com.swyp3.skin.recommendation.ingredient.model.RecommendationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SkinResultGroupScoreService {

    private final SkinResultGroupScoreRepository skinResultGroupScoreRepository;
    private final IngredientGroupReasonResolver reasonResolver;

    @Transactional
    public void saveAll(SkinResult skinResult, RecommendationResult result) {
        Map<IngredientGroup, Double> scores = result.getScores();
        List<IngredientGroup> ranking = result.getRanking();

        ArrayList<SkinResultGroupScore> entities = new ArrayList<>();

        for (int i = 0; i < ranking.size(); i++) {
            IngredientGroup group = ranking.get(i);
            Double score = scores.getOrDefault(group, 0.0);
            int priority = i + 1;

            entities.add(SkinResultGroupScore.create(
                    skinResult,
                    group,
                    score,
                    priority,
                    reasonResolver.resolve(group)
            ));
        }
        skinResultGroupScoreRepository.saveAll(entities);
    }

    public List<SkinResultGroupScore> getTop2ScoresByResultId(Long resultId) {
        return skinResultGroupScoreRepository.findTop2BySkinResultIdOrderByPriorityAsc(resultId);
    }

    public List<SkinResultGroupScore> getScoresByResultId(Long resultId) {
        return skinResultGroupScoreRepository.findBySkinResultIdOrderByPriorityAsc(resultId);
    }
}
