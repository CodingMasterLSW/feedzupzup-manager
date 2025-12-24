package feedzupzup.feedzupzupmanager.domain.vectorization.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.feedzupzupmanager.infra.adapter.SwaggerGateway;
import feedzupzup.feedzupzupmanager.infra.adapter.VectorStoreAdapter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorService {

    private final VectorStoreAdapter vectorStoreAdapter;
    private final SwaggerGateway swaggerGateway;
    private final ObjectMapper objectMapper;

    public void loadSwaggerApiDocs() {
        final String swaggerDataSet = swaggerGateway.fetchSwaggerJson();
        SwaggerParser swaggerParser = new SwaggerParser(objectMapper);
        final List<Document> apiDocuments = swaggerParser.parse(swaggerDataSet);
        vectorStoreAdapter.saveDocuments(apiDocuments);
    }

}
