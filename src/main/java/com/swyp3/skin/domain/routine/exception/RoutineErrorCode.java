package com.swyp3.skin.domain.routine.exception;

import com.swyp3.skin.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RoutineErrorCode implements ErrorCode {
    PREVIEW_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "ROUTINE_400_001", "미리보기 토큰이 만료되었거나 존재하지 않습니다."),
    PREVIEW_RESPONSE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROUTINE_400_002", "루틴 미리보기 응답 데이터가 존재하지 않습니다."),
    PREVIEW_ROUTINE_EMPTY(HttpStatus.BAD_REQUEST, "ROUTINE_400_003", "루틴 미리보기 데이터에 AM/PM 루틴이 존재하지 않습니다."),
    PREVIEW_SECTION_INVALID(HttpStatus.BAD_REQUEST, "ROUTINE_400_004", "루틴 섹션 데이터가 올바르지 않습니다."),
    PREVIEW_PRODUCT_INVALID(HttpStatus.BAD_REQUEST, "ROUTINE_400_005", "루틴에 포함된 상품 정보가 올바르지 않습니다."),
    ROUTINE_TITLE_ERROR(HttpStatus.BAD_REQUEST, "ROUTINE_400_006", "루틴 타이틀이 비었거나 너무 깁니다."),
    ROUTINE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTINE_404_001", "존재하지 않는 루틴입니다."),
    PREVIEW_CACHE_TYPE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR,"ROUTINE_500_001" ,"캐시 값이 서버 지정값과 다릅니다." ),
    PREVIEW_CACHE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "ROUTINE_500_002", "루틴 미리보기 캐시를 찾을 수 없습니다."),
    ROUTINE_UX_PROFILE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "ROUTINE_500_003", "루틴 UX 프로파일 매핑이 존재하지 않습니다."),
    ROUTINE_DATA_INCOMPLETE(HttpStatus.INTERNAL_SERVER_ERROR, "ROUTINE_500_004", "루틴을 구성할 수 있는 추천 상품 데이터가 불안정합니다."),
    PRODUCT_USAGE_TIME_NOT_DEFINE(HttpStatus.INTERNAL_SERVER_ERROR, "ROUTINE_500_005", "루틴을 생성할 상품의 시간대가 정의되지 않았습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
