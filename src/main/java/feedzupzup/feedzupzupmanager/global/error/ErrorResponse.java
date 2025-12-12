package feedzupzup.feedzupzupmanager.global.error;

public record ErrorResponse(
        int status,
        String message
) {

    public static ErrorResponse error(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getHttpStatus().value(), errorCode.getMessage());
    }
}
