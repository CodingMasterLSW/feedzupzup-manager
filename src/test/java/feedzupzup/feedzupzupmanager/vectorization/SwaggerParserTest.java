package feedzupzup.feedzupzupmanager.vectorization;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.feedzupzupmanager.domain.vectorization.application.SwaggerParser;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

public class SwaggerParserTest {

    private SwaggerParser swaggerParser = new SwaggerParser(new ObjectMapper());

    @Test
    @DisplayName("특정 API 파싱 결과 확인 - 로그인 API")
    void parseLoginApiOnly() {
        // given
        String swaggerJson = getTestSwaggerJson();

        // when
        final List<Document> documents = swaggerParser.parse(swaggerJson);

        // then
        Document loginDoc = documents.stream()
                .filter(doc -> "/admin/login".equals(doc.getMetadata().get("path")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("로그인 API를 찾을 수 없음"));

        System.out.println("[Metadata]");
        loginDoc.getMetadata().forEach((key, value) ->
                System.out.println("  " + key + ": " + value)
        );
        System.out.println("\n[Content]");
        System.out.println(loginDoc.getText());

        assertThat(loginDoc.getMetadata().get("method")).isEqualTo("POST");
        assertThat(loginDoc.getText()).contains("관리자 로그인");
    }

    private String getTestSwaggerJson() {
        return """
                {
                    "openapi": "3.1.0",
                    "paths": {
                        "/admin/login": {
                            "post": {
                                "tags": ["AdminAuthorization"],
                                "summary": "관리자 로그인",
                                "description": "관리자 로그인을 수행하고 세션을 생성합니다.",
                                "operationId": "login",
                                "requestBody": {
                                    "content": {
                                        "application/json": {
                                            "schema": {
                                                "$ref": "#/components/schemas/LoginRequest"
                                            }
                                        }
                                    },
                                    "required": true
                                },
                                "responses": {
                                    "200": {
                                        "description": "로그인 성공"
                                    },
                                    "400": {
                                        "description": "로그인 정보가 올바르지 않음"
                                    }
                                }
                            }
                        },
                        "/admin/organizations": {
                            "get": {
                                "tags": ["Admin Organization"],
                                "summary": "단체 조회",
                                "description": "본인이 속한 단체를 모두 조회합니다.",
                                "operationId": "getOrganizations",
                                "responses": {
                                    "200": {
                                        "description": "조회 성공"
                                    }
                                },
                                "security": [{"SessionAuth": []}]
                            },
                            "post": {
                                "tags": ["Admin Organization"],
                                "summary": "단체 저장",
                                "description": "단체를 저장합니다.",
                                "operationId": "createOrganization",
                                "parameters": [
                                    {
                                        "name": "organizationName",
                                        "in": "query",
                                        "description": "단체 이름",
                                        "required": true
                                    }
                                ],
                                "responses": {
                                    "201": {
                                        "description": "저장 성공"
                                    }
                                },
                                "security": [{"SessionAuth": []}]
                            }
                        }
                    },
                    "components": {
                        "schemas": {
                            "LoginRequest": {
                                "type": "object",
                                "properties": {
                                    "loginId": {"type": "string"},
                                    "password": {"type": "string"}
                                }
                            }
                        }
                    }
                }
                """;
    }
}
