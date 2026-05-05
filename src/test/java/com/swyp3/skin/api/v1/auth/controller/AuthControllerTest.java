package com.swyp3.skin.api.v1.auth.controller;

import com.swyp3.skin.api.v1.auth.dto.response.CurrentUserResponse;
import com.swyp3.skin.domain.auth.service.AuthApplicationService;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.domain.user.domain.enums.UserRole;
import com.swyp3.skin.domain.user.domain.repository.UserRepository;
import com.swyp3.skin.global.auth.CustomOAuth2UserService;
import com.swyp3.skin.global.auth.JwtTokenProvider;
import com.swyp3.skin.global.auth.OAuth2SuccessHandler;
import com.swyp3.skin.global.config.SecurityConfig;
import com.swyp3.skin.global.config.WebConfig;
import com.swyp3.skin.global.exception.GlobalExceptionHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, WebConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthApplicationService authApplicationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Test
    @DisplayName("토큰 없이 현재 사용자 조회 요청 시 403 에러를 반환한다")
    void getCurrentUserWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("유효한 AccessToken을 헤더에 담아 요청 시 현재 사용자 정보를 반환한다")
    void getCurrentUserWithHeaderToken() throws Exception {
        // given
        String token = "valid-token";
        Long userId = 1L;
        User user = User.create(UserRole.USER);
        CurrentUserResponse response = new CurrentUserResponse(userId, "nickname", "USER", "url");

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authApplicationService.getCurrentUser(userId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.nickname").value("nickname"));
    }

    @Test
    @DisplayName("유효한 AccessToken을 쿠키에 담아 요청 시 현재 사용자 정보를 반환한다")
    void getCurrentUserWithCookieToken() throws Exception {
        // given
        String token = "valid-token";
        Long userId = 1L;
        User user = User.create(UserRole.USER);
        CurrentUserResponse response = new CurrentUserResponse(userId, "nickname", "USER", "url");

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authApplicationService.getCurrentUser(userId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/auth/me")
                        .cookie(new Cookie("accessToken", token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.nickname").value("nickname"));
    }
}
