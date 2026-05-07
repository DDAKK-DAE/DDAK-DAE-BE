package com.hackathon.api.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 에러 코드 — 프로젝트 공통 에러만 포함.
 * 도메인별 에러 코드는 각 도메인 패키지에 추가하세요.
 *
 * 예시: com.hackathon.api.domain.store.exception.StoreErrorCode
 */
@Getter
public enum ErrorCode {

    // ── 공통 ──────────────────────────────────────────────
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST,          "INVALID_INPUT",  "잘못된 입력값입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND,                "NOT_FOUND",      "리소스를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,          "UNAUTHORIZED",   "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,                "FORBIDDEN",      "접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
