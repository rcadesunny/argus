package com.rcadium.argus.exception;

import com.rcadium.argus.enums.ErrorCode;

public class InvalidRefreshTokenException extends ArgusException {
    public InvalidRefreshTokenException(){
        super(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh token is invalid, user, or expired");
    }
}
