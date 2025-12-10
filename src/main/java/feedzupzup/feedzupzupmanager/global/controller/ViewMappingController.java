package feedzupzup.feedzupzupmanager.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewMappingController {

    @GetMapping("/")
    public String chatPage() {
        return "chat";
    }
}
