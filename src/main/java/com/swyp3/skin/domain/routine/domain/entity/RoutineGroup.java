package com.swyp3.skin.domain.routine.domain.entity;

import com.swyp3.skin.domain.routine.exception.RoutineErrorCode;
import com.swyp3.skin.domain.routine.exception.RoutineException;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_routine_group_user_skin_result",
                columnNames = {"user_id", "skin_result_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutineGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SkinResult skinResult;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String skinType;

    @Column(length = 100)
    private String subtitle;

    @Column(length = 500)
    private String summary;

    public static RoutineGroup of(
            User user,
            SkinResult skinResult,
            String title,
            String skinType,
            String subtitle,
            String summary
    ) {
        RoutineGroup routineGroup = new RoutineGroup();
        routineGroup.user = user;
        routineGroup.skinResult = skinResult;
        routineGroup.skinType = skinType;
        routineGroup.subtitle = subtitle;
        routineGroup.summary = summary;
        routineGroup.updateTitle(title);
        return routineGroup;
    }

    public void updateTitle(String title) {
        String normalizedTitle = normalizeTitle(title);
        if (normalizedTitle.isEmpty() || normalizedTitle.length() > 100) {
            throw new RoutineException(RoutineErrorCode.ROUTINE_TITLE_ERROR);
        }
        this.title = normalizedTitle;
    }

    private String normalizeTitle(String title) {
        return title == null ? "" : title.trim();
    }
}
