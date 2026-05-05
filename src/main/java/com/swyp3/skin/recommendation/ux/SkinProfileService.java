package com.swyp3.skin.recommendation.ux;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.domain.skinresult.service.SkinResultGroupScoreService;
import com.swyp3.skin.domain.skintest.exception.SkinTestErrorCode;
import com.swyp3.skin.domain.skintest.exception.SkinTestException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SkinProfileService {

    public static final int PROFILE_NEED_RANKING_SIZE = 2;
    private final SkinUxProfileResolver skinUxProfileResolver;
    private final SkinResultGroupScoreService skinResultGroupScoreService;

    public SkinUxProfile getProfile(Long skinResultId) {
        List<SkinResultGroupScore> groupScores = skinResultGroupScoreService.getTop2ScoresByResultId(skinResultId);
        List<IngredientGroup> ranking = groupScores.stream()
                .map(SkinResultGroupScore::getIngredientGroup)
                .toList();

        validateRankingSize(ranking);
        return resolveProfile(ranking);
    }

    private @NonNull SkinUxProfile resolveProfile(List<IngredientGroup> ranking) {
        return skinUxProfileResolver.resolve(ranking.get(0), ranking.get(1))
                .orElseThrow(() -> new SkinTestException(SkinTestErrorCode.UX_PROFILE_NOT_FOUND));
    }

    private static void validateRankingSize(List<IngredientGroup> ranking) {
        if (ranking.size() < PROFILE_NEED_RANKING_SIZE) {
            throw new SkinTestException(SkinTestErrorCode.INVALID_RECOMMENDATION_RANKING_SIZE);
        }
    }
}
