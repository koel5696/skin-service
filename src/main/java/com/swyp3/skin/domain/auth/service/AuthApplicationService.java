package com.swyp3.skin.domain.auth.service;

import com.swyp3.skin.api.v1.auth.dto.response.CurrentUserResponse;
import com.swyp3.skin.api.v1.auth.dto.response.TokenRefreshResponse;
import com.swyp3.skin.domain.auth.domain.entity.RefreshToken;
import com.swyp3.skin.domain.auth.repository.RefreshTokenRepository;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.domain.user.domain.entity.UserProfile;
import com.swyp3.skin.domain.user.repository.UserProfileRepository;
import com.swyp3.skin.domain.user.repository.UserRepository;
import com.swyp3.skin.global.auth.JwtTokenProvider;
import com.swyp3.skin.global.auth.exception.AuthErrorCode;
import com.swyp3.skin.global.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApplicationService {

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
    public TokenRefreshResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(refreshToken.getUser().getId());
        return new TokenRefreshResponse(newAccessToken);
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
    }
}
