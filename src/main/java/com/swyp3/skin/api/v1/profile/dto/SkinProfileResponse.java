package com.swyp3.skin.api.v1.profile.dto;

import com.swyp3.skin.recommendation.ux.SkinUxProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

import static com.swyp3.skin.global.timeUtil.TimeUtils.formatToKstDate;

public record SkinProfileResponse(

        @Schema(description = "진단 결과 ID", example = "10")
        Long resultId,

        @Schema(description = "진단 날짜", example = "2026.04.22")
        String diagnosedAt,

        @Schema(description = "피부 유형명")
        String skinType,

        @Schema(description = "부제")
        String subtitle,

        @Schema(description = "피부 설명")
        String summary
)
{
    public static SkinProfileResponse from(Long resultId, Instant diagnosedAt, SkinUxProfile profile) {
        return new SkinProfileResponse(
                resultId,
                formatToKstDate(diagnosedAt),
                profile.skinType(),
                profile.subtitle(),
                profile.summary()
        );
    }
}
