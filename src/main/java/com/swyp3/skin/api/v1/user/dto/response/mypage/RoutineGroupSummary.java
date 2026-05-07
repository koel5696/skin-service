package com.swyp3.skin.api.v1.user.dto.response.mypage;

import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.swyp3.skin.global.timeUtil.TimeUtils.formatToKstDate;

@Schema(description = "루틴 요약 정보")
public record RoutineGroupSummary(

        @Schema(description = "루틴 ID", example = "10")
        Long routineGroupId,

        @Schema(description = "루틴명", example = "아침 루틴")
        String routineGroupTitle,

        @Schema(description = "루틴 생성 날짜", example = "2026-04-03")
        String createdAt
) {
    public static RoutineGroupSummary from(RoutineGroup routine) {
        return new RoutineGroupSummary(
                routine.getId(),
                routine.getTitle(),
                formatToKstDate(routine.getCreatedAt())
        );
    }
}
