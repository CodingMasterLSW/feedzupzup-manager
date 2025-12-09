package feedzupzup.feedzupzupmanager.query.controller;

import feedzupzup.feedzupzupmanager.query.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @GetMapping("/test/schema")
    public String testSchema() {
        return queryService.getAllTableDdl();
    }

}
