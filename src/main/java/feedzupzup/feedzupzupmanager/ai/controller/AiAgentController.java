package feedzupzup.feedzupzupmanager.ai.controller;

import feedzupzup.feedzupzupmanager.ai.dto.QueryLlmResponse;
import feedzupzup.feedzupzupmanager.ai.service.AiAgentService;
import feedzupzup.feedzupzupmanager.ai.dto.QueryLlmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiAgentController {

    private final AiAgentService aiAgentService;

    @PostMapping("/api/ai/sql")
    public ResponseEntity<QueryLlmResponse> executeAiCommand(@RequestBody QueryLlmRequest queryLlmRequest) {
        final QueryLlmResponse response = aiAgentService.executeSqlTask(queryLlmRequest);
        return ResponseEntity.ok(response);
    }
}
