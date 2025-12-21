package feedzupzup.feedzupzupmanager.domain.vectorization.application;

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
            final JsonNode paths = root.get("paths");
            final JsonNode schemas = root.path("components").path("schemas");

            paths.fieldNames().forEachRemaining(path -> {
                final JsonNode pathNode = paths.get(path);
                pathNode.fieldNames().forEachRemaining(method -> {
                    final JsonNode operation = pathNode.get(method);
                    final Document doc = createApiDocument(path, method, operation,
                            schemas);
                    documents.add(doc);
                });
            });
        } catch (Exception e) {
            throw new RuntimeException("Swagger 파싱 실패", e);
        }
        return documents;
    }

    private Document createApiDocument(String path, String method, JsonNode operation, JsonNode schemas) {
        String content = buildContent(path, method, operation, schemas);
        Map<String, Object> metadata = buildMetadata(path, method, operation);
        return new Document(content, metadata);
    }

    private String buildContent(String path, String method, JsonNode operation, JsonNode schemas) {
        StringBuilder sb = new StringBuilder();

        // 기본 정보
        sb.append("API: ").append(method.toUpperCase()).append(" ").append(path).append("\n");

        if (operation.has("summary")) {
            sb.append("요약: ").append(operation.get("summary").asText()).append("\n");
        }

        if (operation.has("description")) {
            sb.append("설명: ").append(operation.get("description").asText()).append("\n");
        }

        if (operation.has("tags")) {
            sb.append("태그: ").append(joinArray(operation.get("tags"))).append("\n");
        }

        // 파라미터
        if (operation.has("parameters")) {
            sb.append("파라미터:\n");
            for (JsonNode param : operation.get("parameters")) {
                sb.append("  - ").append(param.get("name").asText());
                sb.append(" (").append(param.path("in").asText()).append(")");
                if (param.has("description")) {
                    sb.append(": ").append(param.get("description").asText());
                }
                if (param.path("required").asBoolean(false)) {
                    sb.append(" [필수]");
                }
                sb.append("\n");
            }
        }

        // 요청 본문
        if (operation.has("requestBody")) {
            sb.append("요청 본문: ");
            String schemaRef = operation.path("requestBody")
                    .path("content")
                    .path("application/json")
                    .path("schema")
                    .path("$ref")
                    .asText("");

            if (!schemaRef.isEmpty()) {
                String schemaName = schemaRef.replace("#/components/schemas/", "");
                sb.append(schemaName);

                // 스키마 필드 정보 추가
                JsonNode schema = schemas.path(schemaName);
                if (schema.has("properties")) {
                    sb.append(" (");
                    List<String> fields = new ArrayList<>();
                    schema.get("properties").fieldNames().forEachRemaining(fields::add);
                    sb.append(String.join(", ", fields));
                    sb.append(")");
                }
            }
            sb.append("\n");
        }

        // 응답
        if (operation.has("responses")) {
            sb.append("응답:\n");
            operation.get("responses").fieldNames().forEachRemaining(statusCode -> {
                JsonNode response = operation.get("responses").get(statusCode);
                sb.append("  - ").append(statusCode).append(": ");
                if (response.has("description")) {
                    sb.append(response.get("description").asText());
                }
                sb.append("\n");
            });
        }

        // 인증
        if (operation.has("security")) {
            sb.append("인증: 필요 (SessionAuth)\n");
        } else {
            sb.append("인증: 불필요\n");
        }

        return sb.toString();
    }

    private Map<String, Object> buildMetadata(String path, String method, JsonNode operation) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "swagger-api");
        metadata.put("path", path);
        metadata.put("method", method.toUpperCase());

        if (operation.has("operationId")) {
            metadata.put("operationId", operation.get("operationId").asText());
        }

        if (operation.has("tags") && operation.get("tags").isArray() && operation.get("tags").size() > 0) {
            metadata.put("tag", operation.get("tags").get(0).asText());
        }

        return metadata;
    }

    private String joinArray(JsonNode arrayNode) {
        List<String> items = new ArrayList<>();
        arrayNode.forEach(node -> items.add(node.asText()));
        return String.join(", ", items);
    }
}
