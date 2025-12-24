package feedzupzup.feedzupzupmanager.ragevaludator.factory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class ModelEvaluatorFactory {

    @Value("${test.evaluator-model}")
    private String evaluatorModel;

    public ChatClient.Builder create(ChatClient.Builder baseBuilder) {
        return baseBuilder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(evaluatorModel)
                        .build());
    }

}
