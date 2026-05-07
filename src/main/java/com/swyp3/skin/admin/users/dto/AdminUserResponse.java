package com.swyp3.skin.admin.users.dto;

import com.swyp3.skin.domain.user.domain.enums.UserStatus;
import com.swyp3.skin.global.auth.enums.AuthProvider;

import java.time.Instant;

public record AdminUserResponse(
        Long userId,
        String nickname,
        String email,
        AuthProvider provider,
        Instant createdAt,
        Instant lastLoginAt,
        Long totalDiagnoses,
        Instant lastDiagnosedAt,
        Boolean diagnosed,
        Boolean reDiagnosed,
        UserStatus status
) {
}
