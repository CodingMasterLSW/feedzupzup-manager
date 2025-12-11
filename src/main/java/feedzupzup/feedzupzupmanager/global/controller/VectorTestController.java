package feedzupzup.feedzupzupmanager.global.controller;

import feedzupzup.feedzupzupmanager.ingest.application.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VectorTestController {

    private final IngestionService ingestionService;

    @GetMapping("/test/vector")
    public void addTest() {
        ingestionService.loadSwaggerApiDocs();
    }
}
