package feedzupzup.feedzupzupmanager.domain.auth.exception;

import feedzupzup.feedzupzupmanager.global.error.ErrorCode;
import feedzupzup.feedzupzupmanager.global.error.exception.BadRequestException;

public class LoginBadRequestException extends BadRequestException {

    private static final ErrorCode errorCode = ErrorCode.LOGIN_INVALID_CREDENTIALS;

    public LoginBadRequestException(final String message) {
        super(errorCode, message);
    }

}
