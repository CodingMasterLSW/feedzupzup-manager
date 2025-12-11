package feedzupzup.feedzupzupmanager.domain.vectorization.api;

import feedzupzup.feedzupzupmanager.domain.vectorization.application.VectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VectorController {

    private final VectorService vectorService;

    @PostMapping("/api/vector/initialize")
    public void doVectorization() {
        vectorService.loadSwaggerApiDocs();
    }
}
