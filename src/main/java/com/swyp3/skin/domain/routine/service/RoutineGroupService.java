package com.swyp3.skin.domain.routine.service;

import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import com.swyp3.skin.domain.routine.exception.RoutineGroupErrorCode;
import com.swyp3.skin.domain.routine.exception.RoutineGroupException;
import com.swyp3.skin.domain.routine.repository.RoutineGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineGroupService {

    private final RoutineGroupRepository routineGroupRepository;

    public List<RoutineGroup> getTop4ByUserId(Long userId) {
        return routineGroupRepository.findTop4ByUser_IdOrderByCreatedAtDesc(userId);
    }

    public RoutineGroup getLatestByUserId(Long userId) {
        return routineGroupRepository.findTopByUser_IdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new RoutineGroupException(RoutineGroupErrorCode.ROUTINE_GROUP_NOT_FOUND));
    }

}
