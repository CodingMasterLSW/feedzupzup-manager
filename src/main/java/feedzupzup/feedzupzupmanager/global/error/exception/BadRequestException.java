package feedzupzup.feedzupzupmanager.global.error.exception;

import feedzupzup.feedzupzupmanager.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    protected BadRequestException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}