package com.swyp3.skin.domain.routine.exception;

import com.swyp3.skin.global.exception.CustomException;
import com.swyp3.skin.global.exception.ErrorCode;

public class RoutineGroupException extends CustomException {
    public RoutineGroupException(ErrorCode errorCode) {
        super(errorCode);
    }
}
