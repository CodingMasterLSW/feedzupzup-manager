package feedzupzup.feedzupzupmanager.global.exception;

import static feedzupzup.feedzupzupmanager.global.exception.error.ErrorCode.JSON_PROCESSING_ERROR;

import feedzupzup.feedzupzupmanager.global.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class DataProcessingException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    protected DataProcessingException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public static class JsonSerializationException extends DataProcessingException {

        public JsonSerializationException(final String message) {
            super(JSON_PROCESSING_ERROR, message);
        }
    }
}
