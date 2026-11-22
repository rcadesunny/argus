package com.rcadium.argus.exception;

import com.rcadium.argus.enums.ErrorCode;

public class AccountNotActiveException extends ArgusException {
    public AccountNotActiveException(){
        super(ErrorCode.ACCOUNT_NOT_ACTIVE, "Account is not Active");
    }
}
