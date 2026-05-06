package com.swyp3.skin.api.v1.user.dto.response.mypage;

import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "루틴 요약 정보")
public record RoutineGroupSummary(

        @Schema(description = "루틴 ID", example = "10")
        Long routineGroupId,

        @Schema(description = "루틴명", example = "아침 루틴")
        String routineGroupTitle,

        @Schema(description = "루틴 생성 시각", example = "2026-04-03 14:30:00")
        String createdAt
) {
    public static RoutineGroupSummary from(RoutineGroup routine) {
        if (routine == null) return null;
        return new RoutineGroupSummary(
                routine.getId(),
                routine.getTitle(),
                routine.getCreatedAt().toLocalDate().toString()
        );
    }
}
