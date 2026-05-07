package com.swyp3.skin.global.auth;

import com.swyp3.skin.domain.auth.domain.entity.RefreshToken;
import com.swyp3.skin.domain.auth.service.AuthApplicationService;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.domain.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final AuthApplicationService authApplicationService;
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    private final JwtTokenProvider jwtTokenProvider;

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (response.isCommitted()) return;

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userService.findById(userDetails.userId());

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String redirectUrl = getRedirectUrl(request, response, savedRequest);

        boolean isAdminRedirect = redirectUrl.contains("/admin");

        // admin페이지는 쿠키 발급 필요없음
        if (!isAdminRedirect) {
            String providerAccessToken = jwtTokenProvider.createAccessToken(user.getId());

            ResponseCookie accessCookie = getAccessToken(providerAccessToken);

            RefreshToken refreshToken = authApplicationService.saveRefreshToken(user);
            ResponseCookie refreshCookie = getRefreshToken(refreshToken);


            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        response.sendRedirect(redirectUrl);
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

    private String getRedirectUrl(HttpServletRequest request, HttpServletResponse response, SavedRequest savedRequest) {
        String redirectUrl;

        if (savedRequest != null) {
            redirectUrl = savedRequest.getRedirectUrl();
            requestCache.removeRequest(request, response);
        } else {
            redirectUrl = "https://layerd.co.kr/oauth2/callback";
        }
        return redirectUrl;
    }
}
