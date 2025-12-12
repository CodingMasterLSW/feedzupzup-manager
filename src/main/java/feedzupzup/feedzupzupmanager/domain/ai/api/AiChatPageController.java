package feedzupzup.feedzupzupmanager.domain.ai.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AiChatPageController {

    @GetMapping("/")
    public String chatPage() {
        return "chat";
    }
}
