package com.swyp3.skin.domain.auth.service;

import com.swyp3.skin.api.v1.auth.dto.response.CurrentUserResponse;
import com.swyp3.skin.domain.auth.domain.entity.RefreshToken;
import com.swyp3.skin.domain.auth.repository.RefreshTokenRepository;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.domain.user.domain.entity.UserProfile;
import com.swyp3.skin.domain.user.repository.UserProfileRepository;
import com.swyp3.skin.domain.user.repository.UserRepository;
import com.swyp3.skin.global.auth.JwtTokenProvider;
import com.swyp3.skin.global.auth.exception.AuthErrorCode;
import com.swyp3.skin.global.auth.exception.AuthException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApplicationService {

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public CurrentUserResponse getCurrentUser(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        return new CurrentUserResponse(
                userId,
                userProfile.getNickname(),
                userProfile.getUser().getRole().name(),
                userProfile.getProfileImageUrl()
        );
    }

    @Transactional
    public void refresh(String requestRefreshToken, HttpServletResponse response) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        // refresh 토큰 유효성 검사
        validToken(refreshToken);

        // refresh rotation
        refreshToken.rotate();

        // access 재발급
        String newAccessToken = jwtTokenProvider.createAccessToken(refreshToken.getUser().getId());

        ResponseCookie accessCookie = getAccessToken(newAccessToken);

        ResponseCookie refreshCookie = getRefreshToken(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
    }

    private @NonNull ResponseCookie getRefreshToken(RefreshToken refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSecure ? "None" : "Lax")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofDays(14))
                .build();
    }

    private @NonNull ResponseCookie getAccessToken(String newAccessToken) {
        return ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSecure ? "None" : "Lax")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
    }

    private static void validToken(RefreshToken refreshToken) {
        if (refreshToken.isExpired()) {
            refreshToken.revoke();
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
        }

        if (refreshToken.isRevoked()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}
