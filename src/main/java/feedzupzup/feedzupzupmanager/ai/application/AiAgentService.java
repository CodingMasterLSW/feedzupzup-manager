package feedzupzup.feedzupzupmanager.ai.application;

import feedzupzup.feedzupzupmanager.ai.dto.QueryLlmRequest;
import feedzupzup.feedzupzupmanager.ai.dto.QueryLlmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiAgentService {

    private final ChatClient chatClient;

    public AiAgentService(@Qualifier("chatClient") final ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public QueryLlmResponse executeSqlTask(final QueryLlmRequest userRequest) {
        log.info("AI Agent 작업 시작: {}", userRequest.input());

        String response = chatClient.prompt()
                .user(userRequest.input())
                .call()
                .content();

        log.info("AI Agent 작업 완료");

        return new QueryLlmResponse(response);
    }
}
