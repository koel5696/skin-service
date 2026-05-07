package com.swyp3.skin.api.v1.skintest.mapper;

import com.swyp3.skin.api.v1.skintest.dto.response.SkinTestPreviewResponse;
import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.skintest.exception.SkinTestErrorCode;
import com.swyp3.skin.domain.skintest.exception.SkinTestException;
import com.swyp3.skin.recommendation.ingredient.model.RecommendationResult;
import com.swyp3.skin.recommendation.ux.SkinUxProfile;
import com.swyp3.skin.recommendation.ux.SkinUxProfileResolver;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static com.swyp3.skin.global.timeUtil.TimeUtils.formatToKstDate;

@Component
@RequiredArgsConstructor
public class SkinTestPreviewResponseMapper {


    public static final int PROFILE_NEED_RANKING_SIZE = 2;
    private final SkinUxProfileResolver skinUxProfileResolver;

    public SkinTestPreviewResponse toResponse(RecommendationResult result) {

        List<IngredientGroup> ranking = result.getRanking();
        validateRankingSize(ranking);

        SkinUxProfile profile = resolveProfile(ranking);

        return SkinTestPreviewResponse.of(formatToKstDate(Instant.now()), profile);
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
