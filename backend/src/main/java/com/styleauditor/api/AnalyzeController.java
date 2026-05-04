package com.styleauditor.api;

import com.styleauditor.engine.AnalysisService;
import com.styleauditor.model.AnalysisResult;
import com.styleauditor.model.AnalyzeRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalyzeController {
    private final AnalysisService service;

    public AnalyzeController(AnalysisService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public AnalysisResult analyze(@RequestBody AnalyzeRequest request) {
        String text = request == null || request.text() == null ? "" : request.text();
        return service.analyze(text);
    }
}

