package com.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * GlobalErrorCode
 * <p>예외 발생시 사용할 ErrorCode 입니다. <br>
 * 응답시 상태 코드, 커스텀 코드, 메세지를 정의합니다. <br> 커스텀 코드는 각 도메인 별로 100 단위로 코드를 정의해주시면 됩니다. <br> ex) Board - 100
 * -> 커스텀 코드 1001, 1002 순으로 정의하시면 됩니다.</p>
 *
 * @author Kim Dong O
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {

    // 유저 도메인 에러 코드
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "유저가 존재하지 않습니다."),
    UNAUTHENTICATION_USER(HttpStatus.UNAUTHORIZED, 4002, "인증되지 않은 사용자입니다."),
    UNAUTHORIZATION_USER(HttpStatus.FORBIDDEN, 4003, "접근 권한이 없는 유저입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 4004, "잘못된 접근입니다."),
    KAKAO_LOGIN_FAIL(HttpStatus.BAD_REQUEST, 4005, "카카오 로그인에 실패하였습니다."),
    INVALID_JOB_SKILL(HttpStatus.BAD_REQUEST, 4006, "유효하지 않은 직무 기술입니다."),

    // 게시글 도메인 에러 코드
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "카테고리가 존재하지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 1002, "게시글이 존재하지 않습니다."),

    // 카테고리 도메인 에러 코드
    DATABASE_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3001, "데이터베이스 접근 중 오류가 발생했습니다."),

    // 웹 소켓 에러 코드
    EXCEPTION_IN_WEBSOCKET(HttpStatus.UNAUTHORIZED, 6001, "웹 소켓 연결 중에 예외가 발생하였습니다."),

    //공통 서버 에러 코드 500
    NOT_VALID(HttpStatus.BAD_REQUEST, 5001, "요청하신 유효성 검증에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "서버 내부 오류가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
