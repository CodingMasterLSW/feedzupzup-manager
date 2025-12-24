package feedzupzup.feedzupzupmanager.domain.vectorization.application;

import feedzupzup.feedzupzupmanager.domain.vectorization.application.SwaggerParserConstants.Formats;
import feedzupzup.feedzupzupmanager.domain.vectorization.application.SwaggerParserConstants.JsonKeys;
import feedzupzup.feedzupzupmanager.domain.vectorization.application.SwaggerParserConstants.Metadata;
import feedzupzup.feedzupzupmanager.domain.vectorization.application.SwaggerParserConstants.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;

public class SwaggerParser implements DocumentParser{

    private final ObjectMapper objectMapper;

    public SwaggerParser(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Document> parse(String value) {
        List<Document> documents = new ArrayList<>();

        try {
            final JsonNode root = objectMapper.readTree(value);
            final JsonNode paths = root.get(JsonKeys.PATHS);
            final JsonNode schemas = root.path(JsonKeys.COMPONENTS).path(JsonKeys.SCHEMAS);
            paths.fieldNames().forEachRemaining(path ->
                processPath(path, paths.get(path), schemas, documents));
        } catch (Exception e) {
            throw new RuntimeException("Swagger 파싱 실패", e);
        }
        return documents;
    }

    private void processPath(String path, JsonNode pathNode, JsonNode schemas, List<Document> documents) {
        pathNode.fieldNames().forEachRemaining(method ->
            processOperation(path, method, pathNode.get(method), schemas, documents)
        );
    }

    private void processOperation(String path, String method, JsonNode operation, JsonNode schemas, List<Document> documents) {
        final Document doc = createApiDocument(path, method, operation, schemas);
        documents.add(doc);
    }

    private Document createApiDocument(String path, String method, JsonNode operation, JsonNode schemas) {
        String content = buildContent(path, method, operation, schemas);
        Map<String, Object> metadata = buildMetadata(path, method, operation);
        return new Document(content, metadata);
    }

    private String buildContent(String path, String method, JsonNode operation, JsonNode schemas) {
        StringBuilder sb = new StringBuilder();

        appendBasicInfo(sb, path, method, operation);
        appendParameters(sb, operation);
        appendRequestBody(sb, operation, schemas);
        appendResponses(sb, operation);
        appendSecurity(sb, operation);

        return sb.toString();
    }

    private void appendBasicInfo(StringBuilder sb, String path, String method, JsonNode operation) {
        sb.append(Formats.API).append(method.toUpperCase()).append(" ").append(path).append("\n");

        if (operation.has(JsonKeys.SUMMARY)) {
            sb.append(Formats.SUMMARY).append(operation.get(JsonKeys.SUMMARY).asText()).append("\n");
        }

        if (operation.has(JsonKeys.DESCRIPTION)) {
            sb.append(Formats.DESCRIPTION).append(operation.get(JsonKeys.DESCRIPTION).asText()).append("\n");
        }

        if (operation.has(JsonKeys.TAGS)) {
            sb.append(Formats.TAGS).append(joinArray(operation.get(JsonKeys.TAGS))).append("\n");
        }
    }

    private void appendParameters(StringBuilder sb, JsonNode operation) {
        if (!operation.has(JsonKeys.PARAMETERS)) {
            return;
        }

        sb.append(Formats.PARAMETERS);
        for (JsonNode param : operation.get(JsonKeys.PARAMETERS)) {
            sb.append(Formats.PARAMETER_ITEM).append(param.get(JsonKeys.NAME).asText());
            sb.append(" (").append(param.path(JsonKeys.IN).asText()).append(")");

            if (param.has(JsonKeys.DESCRIPTION)) {
                sb.append(": ").append(param.get(JsonKeys.DESCRIPTION).asText());
            }

            if (param.path(JsonKeys.REQUIRED).asBoolean(false)) {
                sb.append(Formats.REQUIRED_MARK);
            }
            sb.append("\n");
        }
    }

    private void appendRequestBody(StringBuilder sb, JsonNode operation, JsonNode schemas) {
        if (!operation.has(JsonKeys.REQUEST_BODY)) {
            return;
        }

        sb.append(Formats.REQUEST_BODY);
        String schemaRef = extractSchemaRef(operation);

        if (!schemaRef.isEmpty()) {
            String schemaName = schemaRef.replace(Schema.REF_PREFIX, "");
            sb.append(schemaName);
            appendSchemaProperties(sb, schemas.path(schemaName));
        }
        sb.append("\n");
    }

    private String extractSchemaRef(JsonNode operation) {
        return operation.path(JsonKeys.REQUEST_BODY)
                .path(JsonKeys.CONTENT)
                .path(Schema.CONTENT_TYPE_JSON)
                .path(JsonKeys.SCHEMA)
                .path(JsonKeys.REF)
                .asText("");
    }

    private void appendSchemaProperties(StringBuilder sb, JsonNode schema) {
        if (!schema.has(JsonKeys.PROPERTIES)) {
            return;
        }

        sb.append(" (");
        List<String> fields = new ArrayList<>();
        schema.get(JsonKeys.PROPERTIES).fieldNames().forEachRemaining(fields::add);
        sb.append(String.join(", ", fields));
        sb.append(")");
    }

    private void appendResponses(StringBuilder sb, JsonNode operation) {
        if (!operation.has(JsonKeys.RESPONSES)) {
            return;
        }

        sb.append(Formats.RESPONSES);
        operation.get(JsonKeys.RESPONSES).fieldNames().forEachRemaining(statusCode -> {
            JsonNode response = operation.get(JsonKeys.RESPONSES).get(statusCode);
            sb.append(Formats.RESPONSE_ITEM).append(statusCode).append(": ");

            if (response.has(JsonKeys.DESCRIPTION)) {
                sb.append(response.get(JsonKeys.DESCRIPTION).asText());
            }
            sb.append("\n");
        });
    }

    private void appendSecurity(StringBuilder sb, JsonNode operation) {
        if (operation.has(JsonKeys.SECURITY)) {
            sb.append(Formats.SECURITY_REQUIRED);
            return;
        }
        sb.append(Formats.SECURITY_NOT_REQUIRED);
    }

    private Map<String, Object> buildMetadata(String path, String method, JsonNode operation) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(Metadata.KEY_SOURCE, Metadata.SOURCE_VALUE);
        metadata.put(Metadata.KEY_PATH, path);
        metadata.put(Metadata.KEY_METHOD, method.toUpperCase());

        if (operation.has(JsonKeys.OPERATION_ID)) {
            metadata.put(Metadata.KEY_OPERATION_ID, operation.get(JsonKeys.OPERATION_ID).asText());
        }

        if (operation.has(JsonKeys.TAGS) && operation.get(JsonKeys.TAGS).isArray() && !operation.get(JsonKeys.TAGS).isEmpty()) {
            metadata.put(Metadata.KEY_TAG, operation.get(JsonKeys.TAGS).get(0).asText());
        }

        return metadata;
    }

    private String joinArray(JsonNode arrayNode) {
        List<String> items = new ArrayList<>();
        arrayNode.forEach(node -> items.add(node.asText()));
        return String.join(", ", items);
    }
}
