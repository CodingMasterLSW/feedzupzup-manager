package feedzupzup.feedzupzupmanager.domain.ai.api;

import feedzupzup.feedzupzupmanager.domain.ai.dto.LlmResponse;
import feedzupzup.feedzupzupmanager.domain.ai.application.AiAgentService;
import feedzupzup.feedzupzupmanager.domain.ai.dto.LlmRequest;
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
    public ResponseEntity<LlmResponse> executeAiCommand(@RequestBody LlmRequest llmRequest) {
        final LlmResponse response = aiAgentService.executeSqlTask(llmRequest);
        return ResponseEntity.ok(response);
    }
}
