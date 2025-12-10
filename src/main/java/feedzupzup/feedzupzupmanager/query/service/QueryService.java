package feedzupzup.feedzupzupmanager.query.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.feedzupzupmanager.global.exception.DataProcessingException.JsonSerializationException;
import feedzupzup.feedzupzupmanager.query.utils.QueryValidator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QueryService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public int executeWriteQuery(final String sql) {
        QueryValidator.validate(sql);
        log.info("쓰기 쿼리 실행");
        log.info("쿼리 : " + sql);
        return jdbcTemplate.update(sql);
    }

    public String executeReadQuery(final String sql) {
        QueryValidator.validate(sql);
        log.info("읽기 쿼리 실행");
        log.info("쿼리 : " + sql);
        final List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패. 쿼리 결과: {}" , result, e);
            throw new JsonSerializationException("Json 변환 과정에서 오류 발생");
        }
    }

    public String getAllTableDdl() {
        final List<String> tableNames = jdbcTemplate.query("SHOW TABLES",
                (rs, rowNum) -> rs.getString(1));
        return tableNames.stream()
                .filter(this::isUserTable)
                .map(this::getTableDdl)
                .collect(Collectors.joining("\n\n"));
    }

    private String getTableDdl(final String tableName) {
        return jdbcTemplate.queryForObject(
                "SHOW CREATE TABLE " + tableName,
                (rs, rowNum) -> rs.getString(2)
        );
    }

    private boolean isUserTable(final String tableName) {
        return !tableName.startsWith("sys_");
    }
}
