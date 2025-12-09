package feedzupzup.feedzupzupmanager.query.service;

import java.util.List;
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

    @Transactional
    public String executeWriteQuery(final String sql) {
        log.info("쓰기 쿼리 실행");
        log.info("쿼리 : " + sql);
        try {
            jdbcTemplate.update(sql);
            return "Query executed success";
        } catch (Exception e) {
            log.error("쓰기 쿼리 실패", e);
            return "Error executing write query: " + e.getMessage();
        }
    }

    public String executeReadQuery(final String sql) {
        log.info("읽기 쿼리 실행");
        log.info("쿼리 : " + sql);

        try {
            var result = jdbcTemplate.queryForList(sql);
            return result.toString();
        } catch (Exception e) {
            log.error("읽기 쿼리 실패", e);
            return "Error executing read query: " + e.getMessage();
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
