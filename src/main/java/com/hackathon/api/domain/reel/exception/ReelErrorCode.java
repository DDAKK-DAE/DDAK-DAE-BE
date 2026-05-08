package com.hackathon.api.domain.reel.exception;

import com.hackathon.api.global.exception.AppErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReelErrorCode implements AppErrorCode {

    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE_NOT_FOUND", "챌린지를 찾을 수 없습니다."),
    CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "CREW_NOT_FOUND", "크루를 찾을 수 없습니다."),
    CREW_CHALLENGE_MISMATCH(HttpStatus.BAD_REQUEST, "CREW_CHALLENGE_MISMATCH", "크루가 해당 챌린지 소속이 아닙니다."),
    NOT_A_CREW_MEMBER(HttpStatus.FORBIDDEN, "NOT_A_CREW_MEMBER", "크루 멤버만 완료 릴스를 업로드할 수 있습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ReelErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
