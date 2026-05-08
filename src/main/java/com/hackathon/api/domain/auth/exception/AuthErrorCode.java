package com.hackathon.api.domain.auth.exception;

import com.hackathon.api.global.exception.AppErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements AppErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,       "EMAIL_ALREADY_EXISTS",  "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED,    "INVALID_CREDENTIALS",   "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,            "USER_NOT_FOUND",        "사용자를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
