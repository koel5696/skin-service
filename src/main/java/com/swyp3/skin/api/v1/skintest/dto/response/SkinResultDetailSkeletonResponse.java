package com.swyp3.skin.api.v1.skintest.dto.response;

import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.swyp3.skin.global.timeUtil.TimeUtils.formatToKstDate;

@Schema(description = "결과 상세 조회 응답 (뼈대)")
public record SkinResultDetailSkeletonResponse(
        @Schema(description = "피부 진단 결과 ID", example = "12")
        Long resultId,

        @Schema(description = "피부 타입", example = "OILY")
        String skinType,

        @Schema(description = "결과 요약", example = "피지/트러블 관리가 우선입니다.")
        String summary,

        @Schema(description = "진단 날짜", example = "2026-04-20")
        String diagnosedAt
) {
    public static SkinResultDetailSkeletonResponse from(SkinResult skinResult) {
        return new SkinResultDetailSkeletonResponse(
                skinResult.getId(),
                skinResult.getSkinType().name(),
                skinResult.getTypeName(),
                formatToKstDate(skinResult.getDiagnosedAt())
        );
    }
}