package feedzupzup.feedzupzupmanager.domain.vectorization.application;

public final class SwaggerParserConstants {

    private SwaggerParserConstants() {}

    public static final class JsonKeys {
        public static final String PATHS = "paths";
        public static final String COMPONENTS = "components";
        public static final String SCHEMAS = "schemas";
        public static final String SUMMARY = "summary";
        public static final String DESCRIPTION = "description";
        public static final String TAGS = "tags";
        public static final String PARAMETERS = "parameters";
        public static final String REQUEST_BODY = "requestBody";
        public static final String CONTENT = "content";
        public static final String SCHEMA = "schema";
        public static final String REF = "$ref";
        public static final String PROPERTIES = "properties";
        public static final String RESPONSES = "responses";
        public static final String SECURITY = "security";
        public static final String OPERATION_ID = "operationId";
        public static final String NAME = "name";
        public static final String IN = "in";
        public static final String REQUIRED = "required";

        private JsonKeys() {}
    }

    public static final class Formats {
        public static final String API = "API: ";
        public static final String SUMMARY = "요약: ";
        public static final String DESCRIPTION = "설명: ";
        public static final String TAGS = "태그: ";
        public static final String PARAMETERS = "파라미터:\n";
        public static final String REQUEST_BODY = "요청 본문: ";
        public static final String RESPONSES = "응답:\n";
        public static final String SECURITY_REQUIRED = "인증: 필요 (SessionAuth)\n";
        public static final String SECURITY_NOT_REQUIRED = "인증: 불필요\n";
        public static final String REQUIRED_MARK = " [필수]";
        public static final String PARAMETER_ITEM = "  - ";
        public static final String RESPONSE_ITEM = "  - ";

        private Formats() {}
    }

    public static final class Schema {
        public static final String REF_PREFIX = "#/components/schemas/";
        public static final String CONTENT_TYPE_JSON = "application/json";

        private Schema() {}
    }

    public static final class Metadata {
        public static final String SOURCE_VALUE = "swagger-api";
        public static final String KEY_SOURCE = "source";
        public static final String KEY_PATH = "path";
        public static final String KEY_METHOD = "method";
        public static final String KEY_OPERATION_ID = "operationId";
        public static final String KEY_TAG = "tag";

        private Metadata() {}
    }
}