package config;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class VectorStoreMockConfig {

    @Bean
    @Primary
    public VectorStore vectorStore() {
        return new VectorStore() {
            private final List<Document> memoryStorage = new ArrayList<>();

            private List<Document> search(String query) {
                String[] words = query.toLowerCase().split("\\W+");
                return memoryStorage.stream()
                        .filter(d -> Arrays.stream(words)
                                .filter(w -> w.length() > 3)
                                .anyMatch(w -> d.getContent().toLowerCase().contains(w)))
                        .toList();
            }

            @Override
            public void accept(List<Document> docs) {
                memoryStorage.addAll(docs);
            }

            @Override
            public void add(List<Document> docs) {
                memoryStorage.addAll(docs);
            }

            @Override
            public Optional<Boolean> delete(List<String> ids) {
                return Optional.of(true);
            }

            @Override
            public List<Document> similaritySearch(String q) {
                return search(q);
            }

            @Override
            public List<Document> similaritySearch(SearchRequest r) {
                return search(r.getQuery());
            }
        };
    }
    }
