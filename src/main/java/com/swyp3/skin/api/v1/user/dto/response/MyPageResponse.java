package com.swyp3.skin.api.v1.user.dto.response;

import com.swyp3.skin.api.v1.user.dto.response.mypage.RoutineGroupSummary;
import com.swyp3.skin.api.v1.user.dto.response.mypage.SkinResultSummary;
import com.swyp3.skin.api.v1.user.dto.response.mypage.UserInfo;
import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.user.domain.entity.UserOauth;
import com.swyp3.skin.domain.user.domain.entity.UserProfile;
import com.swyp3.skin.recommendation.ux.SkinUxProfile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "마이 페이지 응답")
public record MyPageResponse(

        @Schema(description = "사용자 정보",example = "이름 : 홍길동, 이메일 : example.gamil.com")
        UserInfo user,

        @Schema(description = "최근 피부 진단 결과 목록",example = "진단 상세 시간: 2026-04-03 14:30")
        List<SkinResultSummary> skinResults,

        @Schema(description = "최근 루틴",example = "루틴명 : 아침 루틴, 루틴 생성 날짜: 2026-04-03")
        RoutineGroupSummary routine
) {

    public static MyPageResponse from(
            UserOauth userOauth, UserProfile userProfile,
            List<SkinResult> skinResults,
            RoutineGroup routineGroup
    ) {
        return new MyPageResponse(
                UserInfo.from(
                        userProfile.getNickname(),
                        userOauth.getEmail(),
                        userProfile.getProfileImageUrl()
                ),
                skinResults.stream()
                        .map(skinResult -> SkinResultSummary.from(skinResult, skinResult.getTypeName()))
                        .toList(),
                RoutineGroupSummary.from(routineGroup)
        );
    }
}
