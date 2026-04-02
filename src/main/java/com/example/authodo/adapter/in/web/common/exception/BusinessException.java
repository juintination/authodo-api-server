package com.example.authodo.adapter.in.web.common.exception;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] messageArgs;

    public BusinessException(ErrorCode errorCode, Object... messageArgs) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

}
