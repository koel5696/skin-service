package com.swyp3.skin.domain.user.domain.entity;

import com.swyp3.skin.domain.user.domain.enums.UserRole;
import com.swyp3.skin.domain.user.domain.enums.UserStatus;
import com.swyp3.skin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus;

    private LocalDateTime lastLoginAt;

    private LocalDateTime deletedAt;

    private User(UserRole role){
        this.role = role;
        this.userStatus = UserStatus.ACTIVE;
    }

    public static User create() {
        return new User(UserRole.USER);
    }

    public void login() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void delete() {
        this.userStatus = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
