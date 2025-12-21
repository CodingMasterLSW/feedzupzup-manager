package feedzupzup.feedzupzupmanager.infra.adapter;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VectorStoreAdapter {

    private final VectorStore vectorStore;

    public void saveDocuments(final List<Document> documents) {
        vectorStore.add(documents);
    }

    public String searchSimilarDocuments(final String query) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .similarityThreshold(0.6)
                        .build()
        );
        if (docs.isEmpty()) {
            return "관련된 데이터를 찾을 수 없습니다.";
        }

        return docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
