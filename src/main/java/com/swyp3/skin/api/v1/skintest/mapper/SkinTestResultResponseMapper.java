package com.swyp3.skin.api.v1.skintest.mapper;

import com.swyp3.skin.api.v1.skintest.dto.response.SkinTestResultResponse;
import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import com.swyp3.skin.domain.routine.repository.RoutineGroupRepository;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.domain.skinresult.service.SkinResultGroupScoreService;
import com.swyp3.skin.domain.skintest.exception.SkinTestErrorCode;
import com.swyp3.skin.domain.skintest.exception.SkinTestException;
import com.swyp3.skin.recommendation.ux.IngredientMeta;
import com.swyp3.skin.recommendation.ux.SkinProfileService;
import com.swyp3.skin.recommendation.ux.SkinUxProfile;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.swyp3.skin.global.timeUtil.TimeUtils.formatToKstDate;

@Component
@RequiredArgsConstructor
public class SkinTestResultResponseMapper {

    private static final double MIN_GROUP_SCORE = 0.3;
    private static final double MAX_GROUP_SCORE = 1.5;
    private static final int MIN_DISPLAY_SCORE = 20;
    private static final int MAX_DISPLAY_SCORE = 100;

    private final SkinProfileService skinProfileService;
    private final SkinResultGroupScoreService skinResultGroupScoreService;
    private final RoutineGroupRepository routineGroupRepository;

    public SkinTestResultResponse toResponse(SkinResult skinResult, Long user_Id) {
        SkinUxProfile profile = skinProfileService.getProfile(skinResult.getId());
        List<IngredientMeta> ingredientMetas = getIngredientMetas(profile);
        List<SkinTestResultResponse.IngredientGroupScoreResponse> ingredientGroupScores =
                getIngredientGroupScores(skinResult.getId());

        Long RoutineGroup_Id = routineGroupRepository.findByUser_IdAndSkinResult_Id(user_Id, skinResult.getId())
                .map(RoutineGroup::getId)
                .orElse(null);

        return SkinTestResultResponse.of(
                formatToKstDate(skinResult.getDiagnosedAt()),
                profile,
                ingredientMetas,
                ingredientGroupScores,
                RoutineGroup_Id
        );
    }

    // dto 내장으로 넣어도 될듯함 일단 보류
    private static @NonNull List<IngredientMeta> getIngredientMetas(SkinUxProfile profile) {
        return profile.ingredients().stream()
                .map(ingredientType -> {
                    IngredientMeta meta = IngredientMeta.get(ingredientType);
                    if (meta == null) {
                        throw new SkinTestException(SkinTestErrorCode.INGREDIENT_META_NOT_FOUND);
                    }
                    return meta;
                }).toList();
    }

    private List<SkinTestResultResponse.IngredientGroupScoreResponse> getIngredientGroupScores(Long skinResultId) {
        List<SkinResultGroupScore> groupScores = skinResultGroupScoreService.getScoresByResultId(skinResultId);

        return groupScores.stream()
                .map(groupScore -> new SkinTestResultResponse.IngredientGroupScoreResponse(
                        groupScore.getIngredientGroup(),
                        toIngredientGroupName(groupScore.getIngredientGroup()),
                        toDisplayScore(groupScore.getScore())
                ))
                .toList();
    }

    private int toDisplayScore(double score) {
        double clampedScore = Math.max(MIN_GROUP_SCORE, Math.min(score, MAX_GROUP_SCORE));
        double normalized = (clampedScore - MIN_GROUP_SCORE) / (MAX_GROUP_SCORE - MIN_GROUP_SCORE);
        return (int) Math.round(MIN_DISPLAY_SCORE + normalized * (MAX_DISPLAY_SCORE - MIN_DISPLAY_SCORE));
    }

    private String toIngredientGroupName(IngredientGroup ingredientGroup) {
        return switch (ingredientGroup) {
            case ACNE -> "여드름 케어";
            case SEBUM_CONTROL -> "피지 조절";
            case SOOTHING -> "진정";
            case HYDRATION -> "수분 공급";
            case BARRIER -> "피부 장벽 강화";
            case BRIGHTENING -> "미백 / 톤 개선";
            case TURNOVER -> "각질 제거 / 재생";
            case ANTI_AGING -> "주름 개선 / 탄력 강화";
        };
    }
}
