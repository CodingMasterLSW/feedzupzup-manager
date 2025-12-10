package feedzupzup.feedzupzupmanager.query.exception;

import feedzupzup.feedzupzupmanager.global.exception.DomainException;
import feedzupzup.feedzupzupmanager.global.exception.error.ErrorCode;

public class NotExecuteQueryException extends DomainException {

    private static final ErrorCode errorCode = ErrorCode.DANGEROUS_QUERY;

    public NotExecuteQueryException(final String message) {
        super(errorCode, message);
    }
}
