package com.hackathon.api.domain.challenge.exception;

import com.hackathon.api.global.exception.AppErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChallengeErrorCode implements AppErrorCode {

    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND,          "CHALLENGE_NOT_FOUND",     "챌린지를 찾을 수 없습니다."),
    NOT_CHALLENGE_HOST(HttpStatus.FORBIDDEN,           "NOT_CHALLENGE_HOST",      "챌린지 주최자만 접근할 수 있습니다."),
    CHALLENGE_NOT_OPEN(HttpStatus.BAD_REQUEST,         "CHALLENGE_NOT_OPEN",      "모집 중인 챌린지가 아닙니다."),
    DEADLINE_PASSED(HttpStatus.BAD_REQUEST,            "DEADLINE_PASSED",         "모집 마감일이 지났습니다."),
    CANNOT_DELETE_CLOSED(HttpStatus.BAD_REQUEST,       "CANNOT_DELETE_CLOSED",    "모집이 마감된 챌린지는 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ChallengeErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
