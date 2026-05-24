package com.example.quotes.service.ai;

import com.example.quotes.entities.Quote;
import com.example.quotes.exception.ExternalApiException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GroqAiClient {

    private static final Logger logger = LoggerFactory.getLogger(GroqAiClient.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public GroqAiClient(RestTemplate restTemplate,
                        @Value("${groq.api.key:}") String apiKey,
                        @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}") String apiUrl,
                        @Value("${groq.api.model:llama-3.1-8b-instant}") String model) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    public String generateExplanation(Quote quote) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ExternalApiException("Groq API key is not configured. Set groq.api.key or GROQ_API_KEY.");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("temperature", 0.7);
        payload.put("messages", List.of(
                Map.of("role", "system", "content", "Explain quotes in clear, simple language in 2-4 sentences."),
                Map.of("role", "user", "content", buildPrompt(quote))
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, JsonNode.class);
            JsonNode body = response.getBody();
            if (body == null) {
                throw new ExternalApiException("Groq API returned an empty response body.");
            }

            JsonNode choices = body.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new ExternalApiException("Groq API response did not include choices.");
            }

            JsonNode content = choices.get(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().trim().isEmpty()) {
                throw new ExternalApiException("Groq API response did not include content.");
            }

            return content.asText().trim();
        } catch (RestClientException e) {
            logger.error("Error calling Groq API: {}", e.getMessage());
            throw new ExternalApiException("Failed to generate explanation: " + e.getMessage());
        }
    }

    private String buildPrompt(Quote quote) {
        String authorPart = quote.getAuthor() == null || quote.getAuthor().trim().isEmpty()
                ? "" : " — " + quote.getAuthor().trim();
        return "Explain this quote:\n\"" + quote.getText() + "\"" + authorPart;
    }
}

