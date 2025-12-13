package feedzupzup.feedzupzupmanager.ragevaludator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import feedzupzup.feedzupzupmanager.ragevaludator.factory.ModelEvaluatorFactory;
import feedzupzup.feedzupzupmanager.domain.ai.application.AiAgentService;
import feedzupzup.feedzupzupmanager.domain.ai.dto.LlmRequest;
import feedzupzup.feedzupzupmanager.domain.vectorization.application.VectorService;
import feedzupzup.feedzupzupmanager.infra.adapter.SwaggerGateway;
import feedzupzup.feedzupzupmanager.infra.adapter.VectorStoreAdapter;
import feedzupzup.feedzupzupmanager.ragevaludator.RagTestCaseRepository.RagTestCase;
import java.util.List;
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
    private VectorService vectorService;

    @Autowired
    private SwaggerGateway swaggerGateway;

    @Autowired
    private VectorStoreAdapter vectorStoreAdapter;

    @Autowired
    private AiAgentService aiAgentService;

    private RelevancyEvaluator relevancyEvaluator;
    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    void init() {
        swaggerGateway.fetchSwaggerJson();
        vectorService.loadSwaggerApiDocs();
        Builder builder = modelEvaluatorFactory.create(chatClientBuilder);
        this.relevancyEvaluator = new RelevancyEvaluator(builder);
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(builder).build();
    }

    @Test
    @DisplayName("Baseline RAG 평가 - positive case 5회")
    void evaluatePositiveCases() {
        final List<RagTestCase> testCases = RagTestCaseRepository.getTestCases()
                .stream()
                .filter(tc -> !tc.getExpectedAnswer().contains("찾을 수 없습니다"))
                .limit(5)
                .toList();

        evaluateAndPrint("Positive", testCases);
    }

    @Test
    @DisplayName("Baseline RAG 평가 - Negative Cases (5개)")
    void evaluateNegativeCases() {
        List<RagTestCaseRepository.RagTestCase> testCases = RagTestCaseRepository.getTestCases()
                .stream()
                .filter(tc -> tc.getExpectedAnswer().contains("찾을 수 없습니다"))
                .limit(5)
                .toList();
        evaluateAndPrint("Negative", testCases);
    }

    private void evaluateAndPrint(String category, List<RagTestCaseRepository.RagTestCase> testCases) {
        int totalTests = 0;
        int relevancyPassed = 0;
        int faithfulnessPassed = 0;

        System.out.println("\n=== " + category + " Cases Evaluation ===");

        for (int i = 0; i < testCases.size(); i++) {
            RagTestCaseRepository.RagTestCase testCase = testCases.get(i);

            System.out.print(String.format("[%d/%d] ", i + 1, testCases.size()));

            // RAG 실행
            final String context = vectorStoreAdapter.searchSimilarDocuments(testCase.getQuery());
            final String aiAnswer = aiAgentService.executeSqlTask(new LlmRequest(testCase.getQuery()))
                    .llmResponse();

            List<Document> retrievedDocs = List.of(new Document(context));
            final EvaluationRequest request = new EvaluationRequest(
                    testCase.getQuery(),
                    retrievedDocs,
                    aiAnswer
            );

            // 평가
            final EvaluationResponse relevancyResult = relevancyEvaluator.evaluate(request);
            final EvaluationResponse faithfulnessResult = factCheckingEvaluator.evaluate(request);

            totalTests++;
            if (relevancyResult.isPass()) relevancyPassed++;
            if (faithfulnessResult.isPass()) faithfulnessPassed++;

            System.out.println("Done");
        }

        // 결과 출력
        System.out.println("\n--- " + category + " Results ---");
        System.out.println("Total Cases: " + totalTests);
        System.out.println(String.format("Relevancy:    %.1f%% (%d/%d)",
                (double)relevancyPassed / totalTests * 100, relevancyPassed, totalTests));
        System.out.println(String.format("Faithfulness: %.1f%% (%d/%d)",
                (double)faithfulnessPassed / totalTests * 100, faithfulnessPassed, totalTests));
        System.out.println("========================\n");
    }

    @Test
    @DisplayName("RAG 응답의 관련성(Relevancy) 평가 - 환각 체크")
    void testRagRelevancy() {
        String userQuery = "로그인 API의 엔드포인트는 무엇인가요?";
        LlmRequest llmRequest = new LlmRequest(userQuery);

        final String context = vectorStoreAdapter.searchSimilarDocuments(userQuery);
        final String aiAnswer = aiAgentService.executeSqlTask(llmRequest).llmResponse();

        List<Document> reconstructedDocs = List.of(new Document(context));

        final EvaluationRequest evaluationRequest = new EvaluationRequest(
                userQuery,
                reconstructedDocs,
                aiAnswer
        );

        final Builder builder = modelEvaluatorFactory.create(chatClientBuilder);
        RelevancyEvaluator evaluator = new RelevancyEvaluator(builder);

        final EvaluationResponse result = evaluator.evaluate(evaluationRequest);

        System.out.println("통과 여부: " + result.isPass());
        System.out.println("평가 점수: " + result.getScore());
        System.out.println("평가 이유: " + result.getFeedback());
        System.out.println(aiAnswer);

        assertTrue(result.isPass(), "RAG 응답이 문맥과 일치하지 않습니다! 이유: " + result.getFeedback());    }
}
