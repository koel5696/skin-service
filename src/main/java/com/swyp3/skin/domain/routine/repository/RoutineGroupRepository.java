package com.swyp3.skin.domain.routine.repository;

import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutineGroupRepository extends JpaRepository<RoutineGroup, Long> {
    List<RoutineGroup> findTop4ByUser_IdOrderByCreatedAtDesc(Long userId);
    List<RoutineGroup> findByUser_IdOrderByIdDesc(Long userId, Pageable pageable);
    List<RoutineGroup> findByUser_IdAndIdLessThanOrderByIdDesc(Long userId, Long cursor, Pageable pageable);
    Optional<RoutineGroup> findByIdAndUser_Id(Long routineGroupId, Long userId);
    Optional<RoutineGroup> findTopByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<RoutineGroup> findByUser_IdAndSkinResult_Id(Long userId, Long skinResultId);
}
