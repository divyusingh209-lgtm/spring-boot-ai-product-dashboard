package com.example.demo.service;

import com.example.demo.dto.GenerateDescriptionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateDescription(
            GenerateDescriptionRequest request) {

        try {

            String prompt = """
                    You are an expert e-commerce product description writer.

                    Generate a professional and attractive product description
                    based only on the information below.

                    Product Name: %s
                    Category: %s
                    Features: %s
                    Material: %s

                    Rules:
                    1. Write only the product description.
                    2. Keep it between 80 and 120 words.
                    3. Do not invent any features or specifications.
                    4. Make it suitable for an e-commerce website.
                    """
                    .formatted(
                            request.getProductName(),
                            request.getCategory(),
                            request.getFeatures(),
                            request.getMaterial()
                    );

            Map<String, Object> requestBody = Map.of(
                    "model", "openai/gpt-oss-20b",
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    )
            );

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON
            );

            headers.setBearerAuth(groqApiKey);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(
                            requestBody,
                            headers
                    );

            String url =
                    "https://api.groq.com/openai/v1/chat/completions";

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );

            JsonNode root =
                    objectMapper.readTree(response.getBody());

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {

            throw new RuntimeException(
                    "AI description generation failed: "
                            + e.getMessage()
            );
        }
    }
}