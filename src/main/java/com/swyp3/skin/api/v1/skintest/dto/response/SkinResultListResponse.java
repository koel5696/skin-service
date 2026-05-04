package com.swyp3.skin.api.v1.skintest.dto.response;

import com.swyp3.skin.api.v1.user.dto.response.mypage.SkinResultSummary;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "피부 진단 목록 조회 응답")
public record SkinResultListResponse(

        @ArraySchema(
                schema = @Schema(implementation = SkinResultSummary.class),
                arraySchema = @Schema(description = "조회된 피부 진단 결과 목록")
        )
        List<SkinResultSummary> skinResults,

        @Schema(description = "다음 데이터 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "다음 조회에 사용할 커서", example = "21")
        Long nextCursor
) {
    public static SkinResultListResponse from(
            List<SkinResultSummary> skinResults,
            boolean hasNext
    ) {
        Long nextCursor = (hasNext && !skinResults.isEmpty())
                ? skinResults.get(skinResults.size() - 1).resultId()
                : null;

        return new SkinResultListResponse(skinResults, hasNext, nextCursor);
    }
}
