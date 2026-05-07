package com.swyp3.skin.api.v1.skintest.mapper;

import com.swyp3.skin.api.v1.skintest.dto.response.SkinTestResultResponse;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
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

    private final SkinProfileService skinProfileService;

    public SkinTestResultResponse toResponse(SkinResult skinResult) {
        SkinUxProfile profile = skinProfileService.getProfile(skinResult.getId());
        List<IngredientMeta> ingredientMetas = getIngredientMetas(profile);
        return SkinTestResultResponse.of(formatToKstDate(skinResult.getDiagnosedAt()), profile, ingredientMetas);
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
}
