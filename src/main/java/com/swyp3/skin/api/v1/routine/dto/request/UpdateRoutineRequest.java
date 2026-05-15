package com.swyp3.skin.api.v1.routine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateRoutineRequest(

        @NotBlank
        @Schema(description = "사용자가 입력한 루틴 수정 제목", example = "건조 피부 관리 루틴")
        String title
) {
}
