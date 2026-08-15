package com.example.quotes.service.ai;

import com.example.quotes.dto.ChatMessage;
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

import java.util.ArrayList;
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
                        @Value("${groq.api.model:openai/gpt-oss-20b}") String model) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    public String generateExplanation(Quote quote) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ExternalApiException("Groq API key is not configured. Set groq.api.key or GROQ_API_KEY.");
        }

        List<Map<String, Object>> messages = List.of(
                Map.of(
                        "role",
                        "system",
                        "content",
                        "Explain quotes in clear, simple language in 3-5 sentences. Include one real-life example and one lesson or moral."
                ),
                Map.of("role", "user", "content", buildPrompt(quote))
        );

        return callGroq(messages);
    }

    public String generateChatResponse(Quote quote, String question, List<ChatMessage> history) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ExternalApiException("Groq API key is not configured. Set groq.api.key or GROQ_API_KEY.");
        }

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role",
                "system",
                "content",
                "You are a helpful coach. Answer questions about the quote with practical guidance. Include at least one real-life example or actionable takeaway. Keep responses concise."
        ));
        messages.add(Map.of("role", "user", "content", buildQuoteContext(quote)));

        if (history != null) {
            for (ChatMessage message : history) {
                if (message == null || message.getRole() == null || message.getContent() == null) {
                    continue;
                }
                String role = message.getRole().trim().toLowerCase();
                if (!role.equals("user") && !role.equals("assistant")) {
                    continue;
                }
                String content = message.getContent().trim();
                if (content.isEmpty()) {
                    continue;
                }
                messages.add(Map.of("role", role, "content", content));
            }
        }

        messages.add(Map.of("role", "user", "content", question.trim()));
        return callGroq(messages);
    }

    private String callGroq(List<Map<String, Object>> messages) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("temperature", 0.7);
        payload.put("messages", messages);

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
            throw new ExternalApiException("Failed to generate AI response: " + e.getMessage());
        }
    }

    private String buildPrompt(Quote quote) {
        String authorPart = quote.getAuthor() == null || quote.getAuthor().trim().isEmpty()
                ? "" : " — " + quote.getAuthor().trim();
        return "Explain this quote:\n\"" + quote.getText() + "\"" + authorPart;
    }

    private String buildQuoteContext(Quote quote) {
        String authorPart = quote.getAuthor() == null || quote.getAuthor().trim().isEmpty()
                ? "" : "Author: " + quote.getAuthor().trim() + "\n";
        return "Quote:\n\"" + quote.getText() + "\"\n" + authorPart +
                "Answer the user\'s question about the quote.";
    }
}
