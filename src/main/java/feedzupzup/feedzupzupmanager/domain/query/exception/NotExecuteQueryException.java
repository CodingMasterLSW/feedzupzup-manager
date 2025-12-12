package feedzupzup.feedzupzupmanager.domain.query.exception;

import feedzupzup.feedzupzupmanager.global.error.exception.DomainException;
import feedzupzup.feedzupzupmanager.global.error.ErrorCode;

public class NotExecuteQueryException extends DomainException {

    private static final ErrorCode errorCode = ErrorCode.DANGEROUS_QUERY;

    public NotExecuteQueryException(final String message) {
        super(errorCode, message);
    }
}
