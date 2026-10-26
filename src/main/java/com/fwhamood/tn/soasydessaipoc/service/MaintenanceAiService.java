package com.fwhamood.tn.soasydessaipoc.service;


import com.fwhamood.tn.soasydessaipoc.controller.DiagnosticResponse;
import com.fwhamood.tn.soasydessaipoc.controller.SourceRef;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MaintenanceAiService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public MaintenanceAiService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public boolean ingestManualSection(String docRef, String section, int page, String rawText) {
        try {
            vectorStore.accept(List.of(new Document(rawText,
                    Map.of("docRef", docRef, "section", section, "page", page))));
            return true;
        } catch (Exception e) {
            System.out.println("Ingestion échouée" + e);
            return false;
        }
    }


    public DiagnosticResponse generateSafeDiagnostic(String question) {
        List<Document> docs;
        try {
            docs = vectorStore.similaritySearch(question);
        } catch (Exception e) {
            return new DiagnosticResponse(
                    "Recherche documentaire indisponible. Aucune réponse fiable possible.",
                    List.of(), "DEGRADED");
        }

        if (docs == null || docs.isEmpty()) {
            return new DiagnosticResponse(
                    "Information non trouvée dans les documents.",
                    List.of(), "NO_CONTEXT");
        }

        String context = docs.stream()
                .map(d -> "[" + d.getMetadata().get("docRef") + " " + d.getMetadata().get("section")
                        + " p." + d.getMetadata().get("page") + "] " + d.getContent())
                .collect(java.util.stream.Collectors.joining("\n"));

        String system = "Tu es un assistant de maintenance. Réponds en français, en une phrase, à partir du contexte fourni.";

        String user = """
                Contexte :
                %s
                
                Question : %s
                
                Si le contexte ne contient pas la réponse, écris : Information non trouvée dans les documents.
                """.formatted(context, question);

        String answer = chatClient.prompt().system(system).user(user).call().content();

        if (answer.contains("Information non trouvée")) {
            return new DiagnosticResponse(
                    "Information non trouvée dans les documents.",
                    List.of(), "NORMAL");
        }
        List<SourceRef> sources = docs.stream()
                .map(d -> new SourceRef(
                        String.valueOf(d.getMetadata().get("docRef")),
                        String.valueOf(d.getMetadata().get("section")),
                        Integer.parseInt(String.valueOf(d.getMetadata().get("page")))))
                .toList();

        return new DiagnosticResponse(answer, sources, "NORMAL");
    }

}

