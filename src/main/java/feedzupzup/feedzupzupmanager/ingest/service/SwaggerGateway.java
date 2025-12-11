package feedzupzup.feedzupzupmanager.ingest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class SwaggerGateway {

    @Value("${target.swagger.url}")
    private String swaggerUrl;

    private final RestClient restClient = RestClient.create();

    public String fetchSwaggerJson() {
        return restClient.get()
                .uri(swaggerUrl)
                .retrieve()
                .body(String.class);
    }
}
