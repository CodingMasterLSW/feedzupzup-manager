package feedzupzup.feedzupzupmanager.global.error.exception;

import feedzupzup.feedzupzupmanager.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED.getMessage());
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }
}