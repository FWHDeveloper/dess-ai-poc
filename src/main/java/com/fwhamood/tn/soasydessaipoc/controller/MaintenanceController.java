package com.fwhamood.tn.soasydessaipoc.controller;

import com.fwhamood.tn.soasydessaipoc.service.MaintenanceAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dess-ai")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceAiService aiService;

    public MaintenanceController(MaintenanceAiService aiService) {
        this.aiService = aiService;
    }

    // Endpoint pour alimenter l'IA en documents (ETL)
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocument(@RequestBody IngestionRequest request) {
        boolean ok = aiService.ingestManualSection(request.docRef(), request.section(),
                request.page(), request.content());
        return ok ? ResponseEntity.ok("Segment indexé.")
                : ResponseEntity.status(503).body("Ingestion impossible : modèle d'embedding indisponible.");
    }



    // Endpoint pour interroger l'IA avec récupération contextuelle (RAG)
    @PostMapping("/diagnose")
    public ResponseEntity<DiagnosticResponse> queryDiagnostic(@RequestBody DiagnosticRequest request) {
        return ResponseEntity.ok(aiService.generateSafeDiagnostic(request.question()));
    }
}
