package com.rcadium.argus.enums;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_REGISTERED(HttpStatus.CONFLICT),
    ACCOUNT_NOT_ACTIVE(HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status){
        this.status = status;
    }

    public HttpStatus status(){
        return status;
    }
}
