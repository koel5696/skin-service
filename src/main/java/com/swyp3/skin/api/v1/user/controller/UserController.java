package com.swyp3.skin.api.v1.user.controller;

import com.swyp3.skin.api.v1.user.dto.response.MyPageResponse;
import com.swyp3.skin.domain.user.service.UserService;
import com.swyp3.skin.global.auth.CustomUserDetails;
import com.swyp3.skin.global.response.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "마이페이지 요약 정보 조회", description = "사용자 정보, 최근 피부 진단 결과(4개), 최근 루틴(4개)을 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.userId();
        MyPageResponse response = userService.getMyPageInfo(userId);
        return ApiResponse.ok(response);
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 처리합니다. (클라이언트 측의 토큰 삭제와 병행 필요)")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {

        return ApiResponse.ok();
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제 처리합니다.")
    @DeleteMapping
    public ApiResponse<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.deleteUser(userDetails.userId());
        return ApiResponse.ok();
    }
}
