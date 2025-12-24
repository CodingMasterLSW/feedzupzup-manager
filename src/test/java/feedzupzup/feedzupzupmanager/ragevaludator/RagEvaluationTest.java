package feedzupzup.feedzupzupmanager.ragevaludator;


import feedzupzup.feedzupzupmanager.domain.vectorization.application.VectorService;
import feedzupzup.feedzupzupmanager.infra.adapter.SwaggerGateway;
import feedzupzup.feedzupzupmanager.domain.ai.evaluator.ContextPrecisionEvaluator;
import feedzupzup.feedzupzupmanager.domain.ai.evaluator.ContextRecallEvaluator;
import feedzupzup.feedzupzupmanager.ragevaludator.factory.ModelEvaluatorFactory;
import feedzupzup.feedzupzupmanager.domain.ai.application.AiAgentService;
import feedzupzup.feedzupzupmanager.domain.ai.dto.LlmRequest;
import feedzupzup.feedzupzupmanager.domain.ai.dto.LlmResponse;
import feedzupzup.feedzupzupmanager.infra.adapter.VectorStoreAdapter;
import feedzupzup.feedzupzupmanager.ragevaludator.RagTestCaseRepository.RagTestCase;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(ModelEvaluatorFactory.class)
public class RagEvaluationTest {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ModelEvaluatorFactory modelEvaluatorFactory;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private VectorStoreAdapter vectorStoreAdapter;

    @Autowired
    private AiAgentService aiAgentService;

    @Autowired
    private SwaggerGateway swaggerGateway;

    @Autowired
    private VectorService vectorService;

    private RelevancyEvaluator relevancyEvaluator;
    private FactCheckingEvaluator factCheckingEvaluator;
    private ContextPrecisionEvaluator contextPrecisionEvaluator;
    private ContextRecallEvaluator contextRecallEvaluator;

    @BeforeEach
    void init() {
        Builder builder = modelEvaluatorFactory.create(chatClientBuilder);
        this.relevancyEvaluator = new RelevancyEvaluator(builder);
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(builder).build();
        this.contextPrecisionEvaluator = new ContextPrecisionEvaluator(builder);
        this.contextRecallEvaluator = new ContextRecallEvaluator(builder);
    }

    @Test
    @DisplayName("Baseline RAG 평가 - positive case 10회")
    void evaluatePositiveCases() {
        final List<RagTestCase> candidates = RagTestCaseRepository.getTestCases()
                .stream()
                .filter(tc -> !tc.getExpectedAnswer().contains("찾을 수 없습니다"))
                .collect(Collectors.toList());

//        Collections.shuffle(candidates);

        List<RagTestCase> testCases = candidates.stream()
                .limit(10)
                .toList();

        evaluateAndPrint("Positive", testCases);
    }

    @Test
    @DisplayName("Baseline RAG 평가 - Negative Cases (5개)")
    void evaluateNegativeCases() {
        List<RagTestCase> testCases = RagTestCaseRepository.getTestCases()
                .stream()
                .filter(tc -> tc.getExpectedAnswer().contains("찾을 수 없습니다"))
                .limit(10)
                .toList();

        evaluateAndPrint("Negative", testCases);
    }

    private void evaluateAndPrint(String category,
            List<RagTestCaseRepository.RagTestCase> testCases) {
        int totalTests = 0;
        int relevancyPassed = 0;
        int faithfulnessPassed = 0;
        int precisionPassed = 0;
        int recallPassed = 0;
        float precisionScoreSum = 0.0f;
        float recallScoreSum = 0.0f;
        long totalPromptTokens = 0;
        long totalCompletionTokens = 0;
        long totalTokens = 0;

        System.out.println("\n=== " + category + " Cases Evaluation ===");

        for (int i = 0; i < testCases.size(); i++) {
            RagTestCaseRepository.RagTestCase testCase = testCases.get(i);

            System.out.print(String.format("[%d/%d] ", i + 1, testCases.size()));

            // RAG 실행
            final String context = vectorStoreAdapter.searchSimilarDocuments(testCase.getQuery());
            final LlmResponse llmResponse = aiAgentService.executeSqlTask(
                    new LlmRequest(testCase.getQuery()));
            final String aiAnswer = llmResponse.llmResponse();

            List<Document> retrievedDocs = List.of(new Document(context));
            final EvaluationRequest request = new EvaluationRequest(
                    testCase.getQuery(),
                    retrievedDocs,
                    aiAnswer
            );

            final EvaluationRequest recallRequest = new EvaluationRequest(
                    testCase.getQuery(),
                    retrievedDocs,
                    testCase.getExpectedAnswer()
            );


            System.out.println("Query: " + testCase.getQuery());
            System.out.println("Context: [" + context + "]");
            System.out.println("AI Answer: " + aiAnswer);
            System.out.println(String.format("Tokens - Prompt: %d, Completion: %d, Total: %d",
                    llmResponse.promptTokens(), llmResponse.completionTokens(), llmResponse.totalTokens()));

            // 평가
            final EvaluationResponse relevancyResult = relevancyEvaluator.evaluate(request);
            final EvaluationResponse faithfulnessResult = factCheckingEvaluator.evaluate(request);
            final EvaluationResponse precisionResult = contextPrecisionEvaluator.evaluate(request);
            final EvaluationResponse recallResult = contextRecallEvaluator.evaluate(recallRequest);

            totalTests++;
            if (relevancyResult.isPass())
                relevancyPassed++;
            if (faithfulnessResult.isPass())
                faithfulnessPassed++;
            if (precisionResult.isPass())
                precisionPassed++;
            if (recallResult.isPass())
                recallPassed++;
            precisionScoreSum += precisionResult.getScore();
            recallScoreSum += recallResult.getScore();

            totalPromptTokens += llmResponse.promptTokens();
            totalCompletionTokens += llmResponse.completionTokens();
            totalTokens += llmResponse.totalTokens();

            System.out.println("Done");
        }

        // 결과 출력
        System.out.println("\n--- " + category + " Results ---");
        System.out.println("Total Cases: " + totalTests);
        System.out.println(String.format("Relevancy:    %.1f%% (%d/%d)",
                (double) relevancyPassed / totalTests * 100, relevancyPassed, totalTests));
        System.out.println(String.format("Faithfulness: %.1f%% (%d/%d)",
                (double) faithfulnessPassed / totalTests * 100, faithfulnessPassed, totalTests));
        System.out.println(String.format("Context Precision: %.1f%% (%d/%d) | Avg Score: %.3f",
                (double) precisionPassed / totalTests * 100, precisionPassed, totalTests,
                precisionScoreSum / totalTests));
        System.out.println(String.format("Context Recall:    %.1f%% (%d/%d) | Avg Score: %.3f",
                (double) recallPassed / totalTests * 100, recallPassed, totalTests,
                recallScoreSum / totalTests));
        System.out.println("\n--- Token Usage (RAG LLM) ---");
        System.out.println(String.format("Total Prompt Tokens:     %d (Avg: %.1f)",
                totalPromptTokens, (double) totalPromptTokens / totalTests));
        System.out.println(String.format("Total Completion Tokens: %d (Avg: %.1f)",
                totalCompletionTokens, (double) totalCompletionTokens / totalTests));
        System.out.println(String.format("Total Tokens:            %d (Avg: %.1f)",
                totalTokens, (double) totalTokens / totalTests));
        System.out.println("========================\n");
    }
}