package com.swyp3.skin.api.v1.routine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "루틴 저장 요청")
public record SaveRoutineRequest(

        @NotBlank
        @Schema(description = "사용자가 입력한 루틴 제목", example = "건조 피부 관리 루틴")
        String title,

        @NotBlank
        @Schema(description = "루틴 미리보기 토큰", example = "3f8e4f3f-54d8-4a21-a20f-0fce8d2cc9a1")
        String previewToken
) {
}
