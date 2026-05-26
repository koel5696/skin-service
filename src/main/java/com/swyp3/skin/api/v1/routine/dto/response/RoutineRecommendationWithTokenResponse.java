package com.swyp3.skin.api.v1.routine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "루틴 추천 미리보기 응답")
public record RoutineRecommendationWithTokenResponse(
        @Schema(description = "루틴 추천 결과", requiredMode = Schema.RequiredMode.REQUIRED)
        RoutineRecommendationResponse recommendation,

        @Schema(description = "저장 시 사용하는 루틴 미리보기 토큰", example = "3f8e4f3f-54d8-4a21-a20f-0fce8d2cc9a1")
        String previewToken,

        @Schema(description = "이미 저장된 루틴 여부")
        boolean alreadySaved
) {
}
