package com.swyp3.skin.api.v1.routine.controller;

import com.swyp3.skin.api.v1.routine.dto.request.SaveRoutineRequest;
import com.swyp3.skin.api.v1.routine.dto.response.*;
import com.swyp3.skin.domain.routine.dto.RoutinePreviewCacheValue;
import com.swyp3.skin.domain.routine.service.*;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.service.SkinResultService;
import com.swyp3.skin.global.auth.CustomUserDetails;
import com.swyp3.skin.global.response.dto.ApiResponse;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import com.swyp3.skin.recommendation.product.service.ProductRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Routine", description = "루틴 추천 및 저장 관리")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/routines")
public class RoutineController {

    private final SkinResultService skinResultService;
    private final ProductRecommendationService productRecommendationService;
    private final RoutineRecommendationService routineRecommendationService;
    private final RoutineCommandService routineCommandService;
    private final RoutineQueryService routineQueryService;
    private final RoutinePreviewCacheService routinePreviewCacheService;

    @Operation(
            summary = "맞춤형 루틴 추천",
            description = "기준 피부 진단 결과를 기반으로 사용자의 AM/PM 루틴 미리보기 정보를 반환"
    )
    @GetMapping("/recommendation")
    public ApiResponse<RoutineRecommendationWithTokenResponse> getRecommendation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long skinResultId
    ) {
        Long userId = userDetails.userId();
        SkinResult skinResult = (skinResultId != null)
                ? skinResultService.getSkinResultById(skinResultId, userId)
                : skinResultService.getLatestByUserId(userId);
        List<RecommendedProduct> recommended = productRecommendationService.recommend(skinResult.getId());
        RoutineRecommendationResponse response = routineRecommendationService.recommend(recommended, skinResult);
        String previewToken = routinePreviewCacheService.put(new RoutinePreviewCacheValue(userId, response));

        return ApiResponse.ok(new RoutineRecommendationWithTokenResponse(response, previewToken));
    }

    @Operation(
            summary = "루틴 저장",
            description = "직전에 추천된 AM/PM 루틴 결과를 그대로 사용자의 루틴으로 저장"
    )
    @PostMapping
    public ApiResponse<SaveRoutineResponse> saveRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SaveRoutineRequest request
    ){
        SaveRoutineResponse saveRoutineResponse = routineCommandService.save(userDetails, request);
        return ApiResponse.ok(saveRoutineResponse);
    }

    @Operation(
            summary = "루틴 목록 조회",
            description = "사용자가 저장한 루틴 이력을 커서 방식으로 조회"
    )
    @GetMapping
    public ApiResponse<RoutineListResponse> getRoutines(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @Positive(message = "커서는 1 이상이어야 합니다.") Long cursor,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 최소 1 이상이어야 합니다.") int size
    ) {
        Long userId = userDetails.userId();
        RoutineListResponse response = routineQueryService.inquiryRoutineGroups(userId, cursor, size);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "루틴 상세 조회",
            description = "선택한 루틴의 AM/PM 구성, 사용 순서, 추천 이유, 주의사항을 함께 조회"
    )
    @GetMapping("/{routineGroupId}")
    public ApiResponse<RoutineDetailResponse> getDetailRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive(message = "루틴 ID는 1 이상이어야 합니다.") Long routineGroupId) {
        RoutineDetailResponse response = routineQueryService.inquiryDetailRoutineGroup(userDetails.userId(), routineGroupId);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "루틴 삭제",
            description = "선택한 루틴 기준으로 동일 그룹의 AM/PM 루틴과 하위 데이터를 함께 삭제"
    )
    @DeleteMapping("/{routineGroupId}")
    public ApiResponse<Void> deleteRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 루틴 대표 ID", example = "1")
            @PathVariable @Positive(message = "루틴 ID는 1 이상이어야 합니다.") Long routineGroupId
    ) {
        routineCommandService.deleteRoutine(routineGroupId, userDetails.userId());
        return ApiResponse.ok();
    }
}
