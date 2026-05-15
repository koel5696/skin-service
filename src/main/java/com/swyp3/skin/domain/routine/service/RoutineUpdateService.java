package com.swyp3.skin.domain.routine.service;

import com.swyp3.skin.api.v1.routine.dto.response.UpdateRoutineResponse;
import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import com.swyp3.skin.domain.routine.exception.RoutineErrorCode;
import com.swyp3.skin.domain.routine.exception.RoutineException;
import com.swyp3.skin.domain.routine.repository.RoutineGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoutineUpdateService {

    private final RoutineGroupRepository routineGroupRepository;

    @Transactional
    public UpdateRoutineResponse update(Long userId, Long routineGroupId, String title) {
         RoutineGroup routineGroup = routineGroupRepository.findByIdAndUser_Id(routineGroupId, userId).orElseThrow(
                () -> new RoutineException(RoutineErrorCode.ROUTINE_NOT_FOUND));

         routineGroup.updateTitle(title);
        return new UpdateRoutineResponse(routineGroup.getId(), routineGroup.getTitle());
    }
}
