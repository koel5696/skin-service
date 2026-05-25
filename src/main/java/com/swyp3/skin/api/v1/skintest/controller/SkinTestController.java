package com.swyp3.skin.api.v1.skintest.controller;

import com.swyp3.skin.api.v1.skintest.dto.request.CreateSkinResultRequest;
import com.swyp3.skin.api.v1.skintest.dto.request.SkinTestPreviewRequest;
import com.swyp3.skin.api.v1.skintest.dto.response.*;
import com.swyp3.skin.api.v1.skintest.mapper.SkinInputMapper;
import com.swyp3.skin.api.v1.skintest.mapper.SkinTestPreviewResponseMapper;
import com.swyp3.skin.api.v1.skintest.mapper.SkinTestResultResponseMapper;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.service.SkinResultQueryService;
import com.swyp3.skin.domain.skinresult.service.SkinResultService;
import com.swyp3.skin.domain.skintest.dto.SkinPreviewCacheValue;
import com.swyp3.skin.domain.skintest.service.SkinPreviewCacheService;
import com.swyp3.skin.domain.skintest.service.SkinTestApplicationService;
import com.swyp3.skin.global.auth.CustomUserDetails;
import com.swyp3.skin.global.response.dto.ApiResponse;
import com.swyp3.skin.recommendation.ingredient.model.RecommendationResult;
import com.swyp3.skin.recommendation.ingredient.model.SkinInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Skin Test", description = "피부진단 / 온보딩")
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SkinTestController {

    private final SkinResultService skinResultService;
    private final SkinResultQueryService skinResultQueryService;

    private final SkinTestApplicationService skinTestApplicationService;
    private final SkinPreviewCacheService skinPreviewCacheService;

    private final SkinInputMapper skinInputMapper;
    private final SkinTestPreviewResponseMapper previewResponseMapper;
    private final SkinTestResultResponseMapper resultResponseMapper;

    @Operation(
            summary = "설문 단계 조회",
            description = "특정 단계의 질문, 선택지 를 반환합니다."
    )
    @GetMapping("/surveys")
    public ApiResponse<SkinSurveyResponse> getSurvey() {
        return ApiResponse.ok(skinTestApplicationService.getSurveys());
    }

    @Operation(
            summary = "성분 추천 미리보기",
            description = "미리보기 결과와 저장용 previewToken을 반환"
    )
    @PostMapping("/results/preview")
    public ApiResponse<SkinTestPreviewWithTokenResponse> preview(
            @Valid @RequestBody SkinTestPreviewRequest request) {

        SkinInput skinInput = skinInputMapper.toSkinInput(request);
        RecommendationResult result = skinTestApplicationService.calculate(skinInput);
        SkinTestPreviewResponse response = previewResponseMapper.toResponse(result);

        String token = skinPreviewCacheService.put(new SkinPreviewCacheValue(skinInput, result, response.skinType()));
        return ApiResponse.ok(new SkinTestPreviewWithTokenResponse(response, token));
    }

    @Operation(
            summary = "결과 DB 저장",
            description = "사용자의 진단 이력 저장합니다.")
    @PostMapping("/results")
    public ApiResponse<CreateSkinResultResponse> saveResult(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateSkinResultRequest request
            ) {
        Long userId = userDetails.userId();

        CreateSkinResultResponse response =
                skinTestApplicationService.createResult(userId, request);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "내 진단 목록 조회",
            description = "사용자의 피부 진단 이력을 커서 방식으로 조회합니다.")
    @GetMapping("/results")
    public ApiResponse<SkinResultListResponse> getSkinResults(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @Positive(message = "커서는 1 이상이어야 합니다.") Long cursor,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 최소 1 이상이어야 합니다.") int size
    ) {
        SkinResultListResponse response = skinResultQueryService.getSkinResults(userDetails.userId(), cursor, size);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "내 진단 조회",
            description = "로그인한 사용자의 최신 피부 진단 결과를 조회합니다.")
    @GetMapping("/results/{resultId}")
    public ApiResponse<SkinTestResultResponse> getMyResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long resultId
    ) {
        SkinResult skinResult = skinResultService.getSkinResultById(resultId, customUserDetails.userId());
        SkinTestResultResponse response =
                resultResponseMapper.toResponse(skinResult, customUserDetails.userId());

        return ApiResponse.ok(response);
    }
}
