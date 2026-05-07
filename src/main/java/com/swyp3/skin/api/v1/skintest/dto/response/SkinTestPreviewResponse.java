package com.swyp3.skin.api.v1.skintest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import com.swyp3.skin.recommendation.ux.SkinUxProfile;

@Schema(description = "피부 진단 미리보기 응답")
public record SkinTestPreviewResponse(

        @Schema(description = "진단 날짜(프리뷰 시점 기준 오늘)", example = "2026-04-22")
        String diagnosedDate,

        @Schema(description = "피부 유형명")
        String skinType,

        @Schema(description = "부제")
        String subtitle,

        @Schema(description = "피부 설명")
        String summary

        ) {
    public static SkinTestPreviewResponse of(
            String diagnosedDate,
            SkinUxProfile skinUxProfile
            ) {
        return new SkinTestPreviewResponse(
                diagnosedDate,
                skinUxProfile.skinType(),
                skinUxProfile.subtitle(),
                skinUxProfile.summary()
        );
    }
}
