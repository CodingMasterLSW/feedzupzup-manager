package feedzupzup.feedzupzupmanager.global.error;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    RESOURCE_NOT_FOUND("요청한 자원을 찾을 수 없습니다", NOT_FOUND),
    UNAUTHORIZED("인증이 필요합니다. 로그인 후 다시 시도해주세요.", HttpStatus.UNAUTHORIZED),
    LOGIN_INVALID_CREDENTIALS("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    DANGEROUS_QUERY("해당 쿼리는 데이터를 조작할 수 있어 실행할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_VALUE("잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE_VALUE("요청 값의 타입이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PROCESSING_ERROR("서버 내부 데이터 변환 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(final String message, final HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
