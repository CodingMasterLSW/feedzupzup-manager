package feedzupzup.feedzupzupmanager.domain.ai.dto;

public record LlmResponse(
        String llmResponse,
        Long promptTokens,
        Long completionTokens,
        Long totalTokens
) {

}
