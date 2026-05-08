package com.hackathon.api.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 도메인별 에러 코드 enum이 구현해야 하는 공통 인터페이스.
 * 예) AuthErrorCode, ChallengeErrorCode 등
 */
public interface AppErrorCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
