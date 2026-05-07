package com.swyp3.skin.api.v1.routine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "루틴 그룹 목록에서 사용하는 요약 정보")
public record RoutineSummaryResponse(

        @Schema(description = "루틴 그룹 ID", example = "21")
        Long routineGroupId,

        @Schema(description = "루틴 제목", example = "민감 피부 데일리 루틴")
        String title,

        @Schema(description = "생성 날짜", example = "2026-04-05")
        String createdAt
) {
}