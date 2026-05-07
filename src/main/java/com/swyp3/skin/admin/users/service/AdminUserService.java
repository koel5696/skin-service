package com.swyp3.skin.admin.users.service;

import com.swyp3.skin.admin.users.dto.AdminUserResponse;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.repository.SkinResultRepository;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.domain.user.domain.entity.UserOauth;
import com.swyp3.skin.domain.user.domain.entity.UserProfile;
import com.swyp3.skin.domain.user.repository.UserOauthRepository;
import com.swyp3.skin.domain.user.repository.UserProfileRepository;
import com.swyp3.skin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserOauthRepository userOauthRepository;
    private final UserProfileRepository userProfileRepository;
    private final SkinResultRepository skinResultRepository;

    public Page<AdminUserResponse> getUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> {

            UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
            UserOauth oauth = userOauthRepository.findByUser(user).orElse(null);

            long totalDiagnoses = skinResultRepository.countByUser(user);

            Instant lastDiagnosedAt = skinResultRepository
                    .findTopByUserOrderByDiagnosedAtDesc(user)
                    .map(SkinResult::getDiagnosedAt)
                    .orElse(null);

            boolean diagnosed = totalDiagnoses > 0;
            boolean reDiagnosed = totalDiagnoses > 1;

            return new AdminUserResponse(
                    user.getId(),
                    profile != null ? profile.getNickname() : null,
                    oauth != null ? oauth.getEmail() : null,
                    oauth != null ? oauth.getProvider() : null,
                    user.getCreatedAt(),
                    user.getLastLoginAt(),
                    totalDiagnoses,
                    lastDiagnosedAt,
                    diagnosed,
                    reDiagnosed,
                    user.getUserStatus()
            );
        });
    }
}
