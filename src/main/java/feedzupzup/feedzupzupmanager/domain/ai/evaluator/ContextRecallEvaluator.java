package feedzupzup.feedzupzupmanager.domain.ai.evaluator;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.Evaluator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ContextRecallEvaluator implements Evaluator {

    private static final PromptTemplate DEFAULT_CLASSIFICATION_PROMPT = new PromptTemplate("""
            Given a context and a statement, determine if the statement can be attributed to
            (i.e., is supported by or can be inferred from) the given context.

            Respond with only "YES" if the statement can be attributed to the context,
            or "NO" if it cannot.

            Context:
            {context}

            Statement:
            {statement}

            Can this statement be attributed to the context? (YES/NO):
            """);

    private static final PromptTemplate DEFAULT_SENTENCE_EXTRACTION_PROMPT = new PromptTemplate("""
            Break down the following text into individual, atomic statements or claims.
            Each statement should be a single, complete thought that can be verified independently.

            Return only the statements, one per line, without numbering or bullet points.

            Text:
            {text}

            Statements:
            """);

    private final ChatClient.Builder chatClientBuilder;
    private final PromptTemplate classificationPrompt;
    private final PromptTemplate sentenceExtractionPrompt;

    public ContextRecallEvaluator(ChatClient.Builder chatClientBuilder) {
        this(chatClientBuilder, null, null);
    }

    private ContextRecallEvaluator(ChatClient.Builder chatClientBuilder,
            @Nullable PromptTemplate classificationPrompt,
            @Nullable PromptTemplate sentenceExtractionPrompt) {
        Assert.notNull(chatClientBuilder, "chatClientBuilder cannot be null");
        this.chatClientBuilder = chatClientBuilder;
        this.classificationPrompt = classificationPrompt != null ? classificationPrompt : DEFAULT_CLASSIFICATION_PROMPT;
        this.sentenceExtractionPrompt = sentenceExtractionPrompt != null ? sentenceExtractionPrompt : DEFAULT_SENTENCE_EXTRACTION_PROMPT;
    }

    @Override
    public EvaluationResponse evaluate(EvaluationRequest evaluationRequest) {
        String groundTruth = evaluationRequest.getResponseContent();
        String context = doGetSupportingData(evaluationRequest);

        if (!StringUtils.hasText(groundTruth)) {
            return new EvaluationResponse(false, 0.0f,
                    "Ground truth (expected answer) is required for Context Recall evaluation",
                    Collections.emptyMap());
        }

        if (!StringUtils.hasText(context)) {
            return new EvaluationResponse(false, 0.0f,
                    "No context provided for evaluation",
                    Collections.emptyMap());
        }

        // Extract atomic statements from ground truth
        List<String> statements = extractStatements(groundTruth);

        if (statements.isEmpty()) {
            return new EvaluationResponse(false, 0.0f,
                    "Could not extract statements from ground truth",
                    Collections.emptyMap());
        }

        // Classify each statement
        List<Boolean> attributionResults = new ArrayList<>();
        for (String statement : statements) {
            boolean isAttributable = classifyStatement(context, statement);
            attributionResults.add(isAttributable);
        }

        // Calculate recall
        long attributedCount = attributionResults.stream().filter(r -> r).count();
        float recall = (float) attributedCount / statements.size();

        boolean passing = recall >= 0.5f;

        Map<String, Object> metadata = Map.of(
                "statements", statements,
                "attributionResults", attributionResults,
                "totalStatements", statements.size(),
                "attributedStatements", attributedCount
        );

        return new EvaluationResponse(passing, recall, "", metadata);
    }

    private List<String> extractStatements(String text) {
        String userMessage = this.sentenceExtractionPrompt.render(Map.of("text", text));

        String response = this.chatClientBuilder.build()
                .prompt()
                .user(userMessage)
                .call()
                .content();

        if (!StringUtils.hasText(response)) {
            return Collections.emptyList();
        }

        return response.lines()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    private boolean classifyStatement(String context, String statement) {
        String userMessage = this.classificationPrompt.render(Map.of(
                "context", context,
                "statement", statement
        ));

        String response = this.chatClientBuilder.build()
                .prompt()
                .user(userMessage)
                .call()
                .content();

        return "yes".equalsIgnoreCase(response != null ? response.trim() : "");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ChatClient.Builder chatClientBuilder;

        private PromptTemplate classificationPrompt;

        private PromptTemplate sentenceExtractionPrompt;

        private Builder() {
        }

        public Builder chatClientBuilder(ChatClient.Builder chatClientBuilder) {
            this.chatClientBuilder = chatClientBuilder;
            return this;
        }

        public Builder classificationPrompt(PromptTemplate classificationPrompt) {
            this.classificationPrompt = classificationPrompt;
            return this;
        }

        public Builder sentenceExtractionPrompt(PromptTemplate sentenceExtractionPrompt) {
            this.sentenceExtractionPrompt = sentenceExtractionPrompt;
            return this;
        }

        public ContextRecallEvaluator build() {
            return new ContextRecallEvaluator(
                    this.chatClientBuilder,
                    this.classificationPrompt,
                    this.sentenceExtractionPrompt
            );
        }

    }
}
