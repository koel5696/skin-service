package com.swyp3.skin.domain.user.domain.entity;

import com.swyp3.skin.global.auth.enums.AuthProvider;
import com.swyp3.skin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_oauth", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_provider_and_id",
                columnNames = {"provider", "provider_user_id"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 보안 강화
public class UserOauth extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerUserId;

    private String email;

    private UserOauth(User user, AuthProvider provider, String providerUserId, String email) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
    }

    // 2. 정적 팩토리 메서드 (외부에서는 이 메서드로만 객체 생성 가능)
    public static UserOauth create(User user, AuthProvider provider, String providerUserId, String email) {
        return new UserOauth(user, provider, providerUserId, email);
    }

    public void rebindUser(User newUser) {
        this.user = newUser;
    }
}