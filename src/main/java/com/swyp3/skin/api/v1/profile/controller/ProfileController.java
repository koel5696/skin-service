package com.swyp3.skin.api.v1.profile.controller;

import com.swyp3.skin.api.v1.profile.dto.SkinProfileResponse;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.service.SkinResultService;
import com.swyp3.skin.global.auth.CustomUserDetails;
import com.swyp3.skin.global.response.dto.ApiResponse;
import com.swyp3.skin.recommendation.ux.SkinProfileService;
import com.swyp3.skin.recommendation.ux.SkinUxProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Profile", description = "사용자 프로필 조회")
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SkinResultService skinResultService;
    private final SkinProfileService skinProfileService;

    @Operation(summary = "프로필 조회")
    @GetMapping
    public ApiResponse<SkinProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long skinResultId
    ) {
        Long userId = userDetails.userId();

        SkinResult skinResult = (skinResultId != null)
                ? skinResultService.getSkinResultById(skinResultId,userId)
                : skinResultService.getLatestByUserId(userId);

        SkinUxProfile profile = skinProfileService.getProfile(skinResult.getId());


        SkinProfileResponse response = SkinProfileResponse.from(skinResult.getId(), skinResult.getDiagnosedAt(), profile);
        return ApiResponse.ok(response);
    }

}
