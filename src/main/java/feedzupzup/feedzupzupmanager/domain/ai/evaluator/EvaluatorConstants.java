package feedzupzup.feedzupzupmanager.domain.ai.evaluator;

public final class EvaluatorConstants {

    public static final String EMPTY_STRING = "";
    public static final String RESPONSE_YES = "yes";
    public static final float PASSING_THRESHOLD = 0.5f;

    private EvaluatorConstants() {}

    public static final class PromptVars {
        public static final String CONTEXT = "context";
        public static final String QUESTION = "question";
        public static final String TEXT = "text";
        public static final String STATEMENT = "statement";

        private PromptVars() {}
    }

    public static final class MetadataKeys {
        // ContextPrecisionEvaluator
        public static final String RELEVANCE_RESULTS = "relevanceResults";
        public static final String TOTAL_CHUNKS = "totalChunks";
        public static final String RELEVANT_CHUNKS = "relevantChunks";

        // ContextRecallEvaluator
        public static final String STATEMENTS = "statements";
        public static final String ATTRIBUTION_RESULTS = "attributionResults";
        public static final String TOTAL_STATEMENTS = "totalStatements";
        public static final String ATTRIBUTED_STATEMENTS = "attributedStatements";

        private MetadataKeys() {}
    }
}