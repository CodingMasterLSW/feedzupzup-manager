package feedzupzup.feedzupzupmanager.domain.ai.evaluator;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.Evaluator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ContextPrecisionEvaluator implements Evaluator {

    private static final String CONTEXT_DELIMITER = "\n---CONTEXT_SEPARATOR---\n";

    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("""
            Given the following question and context, determine if the context is relevant
            to answering the question.

            A context is considered RELEVANT if it contains information that would help
            answer the question, even if it doesn't contain the complete answer.

            Respond with only "YES" if the context is relevant, or "NO" if it is not relevant.

            Question:
            {question}

            Context:
            {context}

            Is this context relevant to answering the question? (YES/NO):
            """);

    private final ChatClient.Builder chatClientBuilder;

    private final PromptTemplate promptTemplate;

    public ContextPrecisionEvaluator(ChatClient.Builder chatClientBuilder) {
        this(chatClientBuilder, null);
    }

    private ContextPrecisionEvaluator(ChatClient.Builder chatClientBuilder, @Nullable PromptTemplate promptTemplate) {
        Assert.notNull(chatClientBuilder, "chatClientBuilder cannot be null");
        this.chatClientBuilder = chatClientBuilder;
        this.promptTemplate = promptTemplate != null ? promptTemplate : DEFAULT_PROMPT_TEMPLATE;
    }

    @Override
    public EvaluationResponse evaluate(final EvaluationRequest evaluationRequest) {
        String question = evaluationRequest.getUserText();
        List<String> contextChunks = extractContextChunks(evaluationRequest);

        if (contextChunks.isEmpty()) {
            return new EvaluationResponse(false, 0.0f, "No context provided for evaluation", Collections.emptyMap());
        }

        List<Boolean> relevanceResults = new ArrayList<>();

        for (String chunk : contextChunks) {
            boolean isRelevant = evaluateChunkRelevance(question, chunk);
            relevanceResults.add(isRelevant);
        }

        float precision = calculateContextPrecision(relevanceResults);

        boolean passing = precision >= 0.5f;

        Map<String, Object> metadata = Map.of(
                "relevanceResults", relevanceResults,
                "totalChunks", contextChunks.size(),
                "relevantChunks", relevanceResults.stream().filter(r -> r).count()
        );
        return new EvaluationResponse(passing, precision, "", metadata);
    }

    private List<String> extractContextChunks(EvaluationRequest evaluationRequest) {
        String combinedContext = doGetSupportingData(evaluationRequest);

        if (!StringUtils.hasText(combinedContext)) {
            return Collections.emptyList();
        }

        if (combinedContext.contains(CONTEXT_DELIMITER)) {
            return List.of(combinedContext.split(CONTEXT_DELIMITER));
        }

        return evaluationRequest.getDataList().stream()
                .map(doc -> doc.getText())
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private boolean evaluateChunkRelevance(String question, String context) {
        String userMessage = this.promptTemplate.render(Map.of(
                "question", question,
                "context", context
        ));

        String response = this.chatClientBuilder.build()
                .prompt()
                .user(userMessage)
                .call()
                .content();

        return "yes".equalsIgnoreCase(response != null ? response.trim() : "");
    }

    private float calculateContextPrecision(List<Boolean> relevanceResults) {
        long totalRelevant = relevanceResults.stream().filter(r -> r).count();

        if (totalRelevant == 0) {
            return 0.0f;
        }

        double sumPrecision = 0.0;
        int relevantSoFar = 0;

        for (int k = 0; k < relevanceResults.size(); k++) {
            if (relevanceResults.get(k)) {
                relevantSoFar++;
                double precisionAtK = (double) relevantSoFar / (k + 1);
                sumPrecision += precisionAtK;
            }
        }

        return (float) (sumPrecision / totalRelevant);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ChatClient.Builder chatClientBuilder;

        private PromptTemplate promptTemplate;

        private Builder() {
        }

        public Builder chatClientBuilder(ChatClient.Builder chatClientBuilder) {
            this.chatClientBuilder = chatClientBuilder;
            return this;
        }

        public Builder promptTemplate(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
            return this;
        }

        public ContextPrecisionEvaluator build() {
            return new ContextPrecisionEvaluator(this.chatClientBuilder, this.promptTemplate);
        }

    }
}
