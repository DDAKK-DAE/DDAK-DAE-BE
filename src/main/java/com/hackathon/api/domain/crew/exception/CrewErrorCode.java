package com.hackathon.api.domain.crew.exception;

import com.hackathon.api.global.exception.AppErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CrewErrorCode implements AppErrorCode {

    CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "CREW_NOT_FOUND", "크루를 찾을 수 없습니다."),
    NOT_A_MEMBER(HttpStatus.FORBIDDEN, "NOT_A_CREW_MEMBER", "크루 멤버만 접근할 수 있습니다."),
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE_NOT_FOUND", "챌린지를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CrewErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
