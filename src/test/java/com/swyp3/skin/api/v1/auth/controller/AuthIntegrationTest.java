//package com.swyp3.skin.api.v1.auth.controller;
//
//import com.swyp3.skin.api.v1.auth.dto.response.CurrentUserResponse;
//import com.swyp3.skin.domain.auth.service.AuthApplicationService;
//import com.swyp3.skin.domain.user.domain.entity.User;
//import com.swyp3.skin.domain.user.domain.enums.UserRole;
//import com.swyp3.skin.domain.user.domain.repository.UserRepository;
//import com.swyp3.skin.global.auth.JwtTokenProvider;
//import jakarta.servlet.http.Cookie;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("local")
//class AuthIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private AuthApplicationService authApplicationService;
//
//    @MockitoBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    @MockitoBean
//    private UserRepository userRepository;
//
//    @MockitoBean
//    private com.swyp3.skin.domain.user.domain.repository.UserProfileRepository userProfileRepository;
//
//    @Test
//    @DisplayName("통합 테스트: 토큰 없이 요청 시 403 에러 확인")
//    void getCurrentUserWithoutToken() throws Exception {
//        mockMvc.perform(get("/api/v1/auth/me"))
//                .andDo(print())
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @DisplayName("통합 테스트: 쿠키 토큰으로 인증 성공 확인")
//    void getCurrentUserWithCookieToken() throws Exception {
//        // given
//        String token = "valid-token";
//        Long userId = 1L;
//        User user = User.create(UserRole.USER);
//        CurrentUserResponse response = new CurrentUserResponse(userId, "nickname", "USER", "url");
//
//        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
//        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(authApplicationService.getCurrentUser(userId)).thenReturn(response);
//
//        // when & then
//        mockMvc.perform(get("/api/v1/auth/me")
//                        .cookie(new Cookie("accessToken", token)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.userId").value(userId));
//    }
//}
