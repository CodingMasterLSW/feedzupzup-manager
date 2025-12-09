package feedzupzup.feedzupzupmanager.global.config;

import feedzupzup.feedzupzupmanager.query.service.QueryService;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
@Slf4j
public class AiToolConfig {

    public record SqlRequest(String sql) {}

    @Bean
    @Description("Get the database schema DDL for all tables. Use this to understand table structures before writing SQL.")
    public Function<Void, String> getSchema(final QueryService queryService) {
        return request -> {
            log.info("[AI Tool] getSchema 호출");
            return queryService.getAllTableDdl();
        };
    }

    @Bean
    @Description("Execute INSERT, UPDATE queries. NOT for SELECT.")
    public Function<SqlRequest, String> executeWriteSql(final QueryService queryService) {
        return request -> queryService.executeWriteQuery(request.sql());
    }

    @Bean
    @Description("Execute SELECT queries to retrieve data from the database.")
    public Function<SqlRequest, String> executeReadSql(final QueryService queryService) {
        return request -> queryService.executeReadQuery(request.sql());
    }

}
