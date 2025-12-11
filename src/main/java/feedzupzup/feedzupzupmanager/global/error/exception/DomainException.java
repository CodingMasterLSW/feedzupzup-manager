package feedzupzup.feedzupzupmanager.global.error.exception;

import feedzupzup.feedzupzupmanager.global.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    protected DomainException(final ErrorCode errorCode, final String message) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}
