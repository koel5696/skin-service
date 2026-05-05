package com.swyp3.skin.domain.routine.exception;

import com.swyp3.skin.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RoutineGroupErrorCode implements ErrorCode {
    ROUTINE_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTINE_404_002", "루틴 그룹을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
