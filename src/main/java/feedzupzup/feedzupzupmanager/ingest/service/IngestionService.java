package feedzupzup.feedzupzupmanager.ingest.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionService {

    private final VectorStoreAdapter vectorStoreAdapter;
    private final SwaggerGateway swaggerGateway;

    public void loadSwaggerApiDocs() {
        final String swaggerDataSet = swaggerGateway.fetchSwaggerJson();
        final Document document = new Document(swaggerDataSet, Map.of("source", "swagger-api"));
        vectorStoreAdapter.saveDocument(document);
    }

}
