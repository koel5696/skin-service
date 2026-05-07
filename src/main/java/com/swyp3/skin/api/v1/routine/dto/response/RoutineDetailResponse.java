package com.swyp3.skin.api.v1.routine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "루틴 그룹 상세 조회 응답")
public record RoutineDetailResponse(

        @Schema(description = "루틴 그룹 ID", example = "21")
        Long routineGroupId,

        @Schema(description = "기준 피부 진단 결과 ID", example = "7")
        Long skinResultId,

        @Schema(description = "루틴 제목", example = "민감 피부 데일리 루틴")
        String title,

        @Schema(description = "유형명", example = "촉촉한 수분 결핍형")
        String skinType,

        @Schema(description = "피부 타입 설명", example = "장벽까지 같이 약해진 상태예요.")
        String subtitle,

        @Schema(description = "루틴 설명", example =  "수분이 부족해지면 피부 장벽도 함께 약해져요. 보습제를 발라도 금방 건조해지는 이유가 바로 이 악순환 때문이에요. 수분을 채우면서 장벽을 복구하는 이중 케어가 필요해요.")
        String routineSummary,

        @Schema(description = "AM 루틴 구성")
        RoutineSectionResponse amRoutine,

        @Schema(description = "PM 루틴 구성")
        RoutineSectionResponse pmRoutine,

        @Schema(description = "생성 상세 시간", example = "2026-04-05 14:30")
        String createdAt
) {
}
