package com.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * GlobalErrorCode
 * <p>예외 발생시 사용할 ErrorCode 입니다. <br>
 * 응답시 상태 코드, 커스텀 코드, 메세지를 정의합니다. <br>
 * 커스텀 코드는 각 도메인 별로 100 단위로 코드를 정의해주시면 됩니다. <br>
 * ex) Board - 100 -> 커스텀 코드 1001, 1002 순으로 정의하시면 됩니다.</p>
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
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, 4007, "유효하지 않은 토큰입니다."),

    // 게시글 도메인 에러 코드
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "카테고리가 존재하지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 1002, "게시글이 존재하지 않습니다."),
    POST_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, 1003, "게시글 삭제 권한이 없습니다."),
    POST_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, 1004, "게시글 업데이트 권한이 없습니다."),
    NOT_AUTHOR(HttpStatus.FORBIDDEN, 1005, "게시글 작성자가 아닙니다."),
    INVALID_BOARD_TYPE(HttpStatus.BAD_REQUEST, 1006, "게시판 타입을 잘못 선택했습니다."),

    // 모집 관련 에러 코드
    RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 8001, "모집 지원 내역이 존재하지 않습니다."),
    ALREADY_RECRUITMENT(HttpStatus.BAD_REQUEST, 8002, "이미 모집 신청을 하였습니다."),
    INVALID_POST(HttpStatus.BAD_REQUEST, 8003, "모집 신청한 게시글과 일치하지 않습니다."),
    RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, 8004, "모집이 마감되었습니다."),
    INVALID_RECRUITMENT_STATUS(HttpStatus.BAD_REQUEST, 8005, "이미 처리된 지원자 입니다."),

    // 카테고리 도메인 에러 코드
    DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, 3001, "카테고리 이름이 이미 존재합니다."),
    INVALID_CATEGORY_NAME(HttpStatus.BAD_REQUEST, 3002, "카테고리 이름이 유효하지 않습니다."),
    NAME_MISMATCH(HttpStatus.BAD_REQUEST, 3004, "카테고리 이름이 일치하지 않습니다."),

    // 댓글 도메인 에러 코드
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "댓글이 존재하지않습니다."),

	// 관심 에러 코드 7001 ~
	ALREADY_VOTER(HttpStatus.BAD_REQUEST, 7001, "이미 관심을 추가하였습니다."),
	NOT_SUPPORT_TYPE(HttpStatus.BAD_REQUEST, 7002, "지원하지 않는 관심 타입 입니다."),

    // 웹 소켓 에러 코드
    EXCEPTION_IN_WEBSOCKET(HttpStatus.UNAUTHORIZED, 6001, "웹 소켓 연결 중에 예외가 발생하였습니다."),

    // 채용 공고 도메인 에러 코드
    JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "채용 정보가 존재하지 않습니다."),
    JOB_POSTING_REQUIRED(HttpStatus.BAD_REQUEST, 2002, "채용 정보가 필요합니다."),

    // 채용공고 서비스 에러 코드
    NO_DATA_RECEIVED(HttpStatus.NO_CONTENT, 5003, "받아온 데이터가 없습니다."),
    API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, 5001, "외부 API 요청에 실패했습니다."),
    JSON_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "응답 데이터를 처리하는중 오류가 발생했습니다."),
    DATABASE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 5005, "데이터 저장중 오류가 발생했습니다."),

    // 공통 서버 에러 코드 500
    NOT_VALID(HttpStatus.BAD_REQUEST, 5001, "요청하신 유효성 검증에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "서버 내부 오류가 발생하였습니다."),

    // JWT 관련 에러 코드
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 7001, "토큰이 만료되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 7002, "토큰이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}