package com.swyp3.skin.domain.auth.domain.entity;

import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    private static final long EXPIRE_DAYS = 14L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    private RefreshToken(User user) {
        this.user = user;
    }

    public static RefreshToken create(User user) {
        RefreshToken refreshToken = new RefreshToken(user);
        refreshToken.token = refreshToken.newToken();
        refreshToken.expiryDate = LocalDateTime.now().plusDays(EXPIRE_DAYS);
        refreshToken.revoked = false;
        return refreshToken;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public boolean isValid() {
        return !isRevoked() && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
    }

    public void rotate() {
        this.token = newToken();
        this.expiryDate = LocalDateTime.now().plusDays(EXPIRE_DAYS);
        this.revoked = false;
    }

    private String newToken() {
        return UUID.randomUUID().toString();
    }
}