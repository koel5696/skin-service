package com.swyp3.skin.domain.skinresult.domain.entity;

import com.swyp3.skin.domain.skinresult.domain.enums.Concern;
import com.swyp3.skin.domain.skinresult.domain.enums.SkinType;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinType skinType;

    @Column(nullable = false)
    private String typeName;

    @ElementCollection
    @CollectionTable(
            name = "skin_result_concern",
            joinColumns = @JoinColumn(name = "skin_result_id", nullable = false)
    )
    @Column(name = "concern", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Concern> concerns;

    @Column(nullable = false)
    private Instant diagnosedAt;

    private SkinResult(User user, SkinType skinType, String typeName, List<Concern> concerns, Instant diagnosedAt) {
        this.user = user;
        this.skinType = skinType;
        this.typeName = typeName;
        this.concerns = concerns;
        this.diagnosedAt = diagnosedAt;
    }

    public static SkinResult create(
            User user,
            SkinType skinType,
            String typeName,
            List<Concern> concerns,
            Instant diagnosedAt) {
        return new SkinResult(user, skinType, typeName,concerns ,diagnosedAt);
    }
}
