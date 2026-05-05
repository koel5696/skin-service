package com.swyp3.skin.api.v1.user.dto.response.mypage;

import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피부 진단 결과 요약 정보")
public record SkinResultSummary(

        @Schema(description = "피부 진단 결과 ID", example = "10")
        Long resultId,

        @Schema(description = "진단 시각", example = "2026-04-03 14:30:00")
        String createdAt,

        @Schema(description = "피부 유형", example = "촉촉한 수분 결핍형")
        String typeName
) {
    public static SkinResultSummary from(SkinResult skinResult,String typeName) {
        return new SkinResultSummary(
                skinResult.getId(),
                skinResult.getCreatedAt().toString(),
                typeName
        );
    }
}
