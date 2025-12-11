package feedzupzup.feedzupzupmanager.global.config;

import static feedzupzup.feedzupzupmanager.ai.constant.AiPrompts.DBA_SYSTEM_PROMPT;

import feedzupzup.feedzupzupmanager.ai.util.AiTaskWrapper;
import feedzupzup.feedzupzupmanager.ingest.service.VectorStoreAdapter;
import feedzupzup.feedzupzupmanager.query.service.QueryService;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
@Slf4j
public class AiToolConfig {

    public record SqlRequest(String sql) {}

    public record SearchRequestWrapper(String query) {}

    @Bean
    @Description("Search technical documents (Swagger API, Team Rules) to answer questions.")
    public Function<SearchRequestWrapper, String> searchKnowledgeBase(final VectorStoreAdapter vectorStoreAdapter) {
        return requestWrapper -> AiTaskWrapper.execute(() ->
                vectorStoreAdapter.searchSimilarDocuments(requestWrapper.query()));
    }

    @Bean("chatClient")
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultToolNames("getSchema", "executeWriteSql", "executeReadSql", "searchKnowledgeBase")
                .defaultSystem(DBA_SYSTEM_PROMPT)
                .build();
    }

    @Bean
    @Description("Get the database schema DDL for all tables. Use this to understand table structures before writing SQL.")
    public Function<Void, String> getSchema(final QueryService queryService) {
        return request -> queryService.getAllTableDdl();
    }

    @Bean
    @Description("Execute INSERT, UPDATE queries. NOT for SELECT.")
    public Function<SqlRequest, String> executeWriteSql(final QueryService queryService) {
        return request -> AiTaskWrapper.execute(() -> {
            int rows = queryService.executeWriteQuery(request.sql());
            return "Query executed successfully. Rows affected: " + rows;
        });
    }

    @Bean
    @Description("Execute SELECT queries to retrieve data from the database.")
    public Function<SqlRequest, String> executeReadSql(final QueryService queryService) {
        return request -> AiTaskWrapper.execute(() ->
            queryService.executeReadQuery(request.sql())
        );
    }
}
