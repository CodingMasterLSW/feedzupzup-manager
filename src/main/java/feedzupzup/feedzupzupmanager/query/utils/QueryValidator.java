package feedzupzup.feedzupzupmanager.query.utils;

import feedzupzup.feedzupzupmanager.query.exception.NotExecuteQueryException;
import java.util.regex.Pattern;

/**
 * 기본적으로 DB 유저의 권한을 통해 DDL, DCL을 제어합니다.
 * 다만, 방어적으로 요청조차 가지 않기 위해 WAS 에서 1차 검증을 진행합니다.
 */
public class QueryValidator {

    private static final Pattern DANGEROUS_PATTERNS = Pattern.compile(
            "(?i)\\b(drop|truncate|delete|alter|grant|revoke|commit|rollback|rename)\\b"
    );

    private QueryValidator() {}

    public static void validate(final String sql) {
        if (DANGEROUS_PATTERNS.matcher(sql).find()) {
            throw new NotExecuteQueryException("해당 쿼리 : " + sql + "은 기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다.");
        }
    }
}
