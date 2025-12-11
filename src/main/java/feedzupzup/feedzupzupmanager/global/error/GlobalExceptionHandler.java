package feedzupzup.feedzupzupmanager.global.error;

import feedzupzup.feedzupzupmanager.global.error.exception.BadRequestException;
import feedzupzup.feedzupzupmanager.global.error.exception.DataProcessingException;
import feedzupzup.feedzupzupmanager.global.error.exception.DomainException;
import feedzupzup.feedzupzupmanager.global.error.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static feedzupzup.feedzupzupmanager.global.error.ErrorCode.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(final UnauthorizedException e) {
        log.warn(e.getMessage());
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestException e) {
        log.info(e.getMessage());
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(final DomainException e) {
        log.error(e.getMessage(), e);
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }

    @ExceptionHandler(DataProcessingException.class)
    public ResponseEntity<ErrorResponse> handleDataProcessingException(final DataProcessingException e) {
        log.error(e.getMessage(), e);
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(INVALID_INPUT_VALUE.getHttpStatus())
                .body(ErrorResponse.error(INVALID_INPUT_VALUE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(INVALID_TYPE_VALUE.getHttpStatus())
                .body(ErrorResponse.error(INVALID_TYPE_VALUE));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(METHOD_NOT_ALLOWED.getHttpStatus())
                .body(ErrorResponse.error(METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ErrorResponse.error(INTERNAL_SERVER_ERROR));
    }
}
