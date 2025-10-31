package com.example.authodo.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "error.common.invalid-argument"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.common.unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "error.common.forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "error.common.not-found"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.common.internal-error"),

    // TODO 도메인
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "error.todo.not-found"),
    TODO_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "error.todo.title-required"),
    TODO_STATUS_INVALID(HttpStatus.BAD_REQUEST, "error.todo.status-invalid");

    private final HttpStatus httpStatus;
    private final String messageKey;

    ErrorCode(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }

}
