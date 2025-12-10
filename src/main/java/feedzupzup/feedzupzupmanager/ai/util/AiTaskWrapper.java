package feedzupzup.feedzupzupmanager.ai.util;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AiTaskWrapper {

    private AiTaskWrapper() {}

    public static String execute(Supplier<String> action) {
        try {
            return action.get();
        } catch (Exception e) {
            log.warn("AI 도구 실행 중 예외 발생: {}", e.getMessage());
            return "Error executing query: " + e.getMessage();
        }
    }
}
