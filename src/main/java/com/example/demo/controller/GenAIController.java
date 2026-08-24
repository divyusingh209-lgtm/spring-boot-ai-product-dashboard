package com.example.demo.controller;

import com.example.demo.dto.GenerateDescriptionRequest;
import com.example.demo.service.AIService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/ai")
public class GenAIController {

    private final AIService aiService;

    public GenAIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate-description")
    public ResponseEntity<?> generateDescription(
            @RequestBody GenerateDescriptionRequest request) {

        try {

            String description =
                    aiService.generateDescription(request);

            return ResponseEntity.ok(
                    Map.of(
                            "description", description
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error", e.getMessage()
                            )
                    );
        }
    }
}