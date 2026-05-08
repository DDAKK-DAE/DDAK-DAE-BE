package com.hackathon.api.domain.participation.exception;

import com.hackathon.api.global.exception.AppErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ParticipationErrorCode implements AppErrorCode {

    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND,       "PARTICIPATION_NOT_FOUND",       "신청 정보를 찾을 수 없습니다."),
    CANNOT_APPLY_OWN_CHALLENGE(HttpStatus.BAD_REQUEST,  "CANNOT_APPLY_OWN_CHALLENGE",    "본인이 개설한 챌린지에는 신청할 수 없습니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT,                "ALREADY_APPLIED",               "이미 신청한 챌린지입니다."),
    CHALLENGE_FULL(HttpStatus.BAD_REQUEST,              "CHALLENGE_FULL",                "모집 인원이 꽉 찼습니다."),
    NOT_PENDING_PARTICIPATION(HttpStatus.BAD_REQUEST,   "NOT_PENDING_PARTICIPATION",     "이미 처리된 신청입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ParticipationErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
