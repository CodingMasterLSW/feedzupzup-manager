package feedzupzup.feedzupzupmanager.global.exception.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    DANGEROUS_QUERY("해당 쿼리는 데이터를 조작할 수 있어 실행할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_VALUE("잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE_VALUE("요청 값의 타입이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(final String message, final HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
