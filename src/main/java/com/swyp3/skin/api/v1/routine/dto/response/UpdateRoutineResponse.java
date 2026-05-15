package com.swyp3.skin.api.v1.routine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "루틴 수정 완료 응답")
public record UpdateRoutineResponse(

        @Schema(description = "수정된 루틴 그룹 ID", example = "21")
        Long routineGroupId,

        @Schema(description = "수정된 루틴 타이틀", example = "민감 피부 데일리 루틴")
        String title
) {
}
