package feedzupzup.feedzupzupmanager.domain.vectorization.application;

import java.util.List;
import org.springframework.ai.document.Document;

public interface DocumentParser {

    List<Document> parse(String value);
}
