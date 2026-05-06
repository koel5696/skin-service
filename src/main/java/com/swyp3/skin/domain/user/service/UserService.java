package com.swyp3.skin.domain.user.service;

import com.swyp3.skin.api.v1.user.dto.response.MyPageResponse;
import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import com.swyp3.skin.domain.routine.service.RoutineGroupService;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.service.SkinResultService;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.domain.user.domain.entity.UserOauth;
import com.swyp3.skin.domain.user.domain.entity.UserProfile;
import com.swyp3.skin.domain.user.domain.enums.UserStatus;
import com.swyp3.skin.domain.user.repository.UserOauthRepository;
import com.swyp3.skin.domain.user.repository.UserProfileRepository;
import com.swyp3.skin.domain.user.repository.UserRepository;
import com.swyp3.skin.global.auth.enums.AuthProvider;
import com.swyp3.skin.global.auth.exception.AuthErrorCode;
import com.swyp3.skin.global.auth.exception.AuthException;
import com.swyp3.skin.global.auth.oauth.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserOauthRepository userOauthRepository;
    private final UserProfileRepository userProfileRepository;
    private final SkinResultService skinResultService;
    private final RoutineGroupService routineGroupService;


    public User findOrCreateUser(OAuth2UserInfo userInfo, AuthProvider provider) {

        return userOauthRepository
                .findByProviderAndProviderUserId(provider, userInfo.getProviderUserId())
                .map(userOauth -> {
                    User user = userOauth.getUser();

                    //탈퇴한 유저가 다시 로그인하는 경우, 유저 상태를 활성화로 변경
                    if (user.getUserStatus() == UserStatus.DELETED){
                        User newUser = userRepository.save(User.create());

                        userOauth.rebindUser(user);

                        userProfileRepository.save(UserProfile.create(
                                newUser,
                                userInfo.getNickname(),
                                userInfo.getProfileImageUrl()
                        ));

                        newUser.login();
                        return newUser;
                    }

                    // 로그인 시점 업데이트
                    user.login();

                    return user;
                })
                .orElseGet(() -> {
                    User user = userRepository.save(User.create());

                    userOauthRepository.save(UserOauth.create(
                            user,
                            provider,
                            userInfo.getProviderUserId(),
                            userInfo.getEmail()
                    ));

                    userProfileRepository.save(UserProfile.create(
                            user,
                            userInfo.getNickname(),
                            userInfo.getProfileImageUrl()
                    ));
                    user.login(); // 새로 생성된 유저도 로그인 시점 업데이트
                    return user;
                });
    }

    public MyPageResponse getMyPageInfo(Long userId) {

        UserOauth userOauth = userOauthRepository.findByUser_id(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        List<SkinResult> skinResults =
                skinResultService.getTop3ByUserId(userId);

        Optional<RoutineGroup> routineGroup =
                routineGroupService.getLatestByUserId(userId);

        return MyPageResponse.from(userOauth, userProfile, skinResults, routineGroup.orElse(null));

    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND))
                .delete();
    }
}
