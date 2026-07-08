package com.example.quotes.service;

import com.example.quotes.dto.ChatMessage;
import com.example.quotes.entities.Quote;
import com.example.quotes.exception.QuoteNotFoundException;
import com.example.quotes.exception.ExternalApiException;
import com.example.quotes.repository.QuoteRepository;
import com.example.quotes.service.ai.GroqAiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class QuoteServiceImpl implements QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private static final String ZENQUOTES_API_URL = "https://zenquotes.io/api/random";
    private static final String DUMMYJSON_API_URL = "https://dummyjson.com/quotes/random";
    private static final String FORISMATIC_API_URL = "https://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=en";

    private final QuoteRepository quoteRepository;
    private final RestTemplate restTemplate;
    private final GroqAiClient groqAiClient;
    private final Random random = new Random();

    // In-memory cache refreshed on a schedule, not per-request
    private final List<Quote> cache = new CopyOnWriteArrayList<>();

    @Autowired
    public QuoteServiceImpl(QuoteRepository quoteRepository, RestTemplate restTemplate, GroqAiClient groqAiClient) {
        this.quoteRepository = quoteRepository;
        this.restTemplate = restTemplate;
        this.groqAiClient = groqAiClient;
    }

    @Override
    public List<Quote> getAllQuotes() {
        logger.info("Fetching all quotes from database");
        return quoteRepository.findAll();
    }

    /**
     * Refreshes the in-memory cache every 5 minutes instead of calling
     * the external API on every user request. This is what actually
     * fixes the 429s — ZenQuotes' free tier chokes on per-request calls,
     * not on a background job hitting it once every 5 minutes.
     */
    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void refreshQuoteCache() {
        try {
            Quote quote = fetchFromExternalChain();
            cache.add(quote);
            if (cache.size() > 50) {
                cache.remove(0);
            }
            logger.info("Cache refreshed, size now {}", cache.size());
        } catch (Exception e) {
            logger.warn("Scheduled cache refresh failed, keeping existing cache (size {})", cache.size(), e);
        }
    }

    @Override
    public Quote getRandomQuoteFromAPI() {
        logger.info("Serving random quote request");

        if (!cache.isEmpty()) {
            Quote cached = cache.get(random.nextInt(cache.size()));
            logger.info("Served from cache: {} - {}", cached.getText(), cached.getAuthor());
            return cached;
        }

        // Cold start: cache is empty (app just booted), fetch live
        logger.info("Cache empty, fetching live via fallback chain");
        return fetchFromExternalChain();
    }

    /**
     * Tries each provider in order. Only throws ExternalApiException
     * if every single one fails, including the local DB fallback.
     */
    private Quote fetchFromExternalChain() {
        try {
            return fetchFromZenQuotes();
        } catch (RestClientException e) {
            logger.warn("ZenQuotes failed ({}), trying DummyJSON", e.getMessage());
        }

        try {
            return fetchFromDummyJson();
        } catch (RestClientException e) {
            logger.warn("DummyJSON failed ({}), trying Forismatic", e.getMessage());
        }

        try {
            return fetchFromForismatic();
        } catch (RestClientException e) {
            logger.warn("Forismatic failed ({}), falling back to local database", e.getMessage());
        }

        return fetchFromLocalDatabase();
    }

    private Quote fetchFromZenQuotes() {
        @SuppressWarnings("unchecked")
        Map<String, Object>[] response = restTemplate.getForObject(ZENQUOTES_API_URL, Map[].class);

        if (response == null || response.length == 0) {
            throw new RestClientException("Empty response from ZenQuotes");
        }
        Map<String, Object> data = response[0];
        String text = (String) data.get("q");
        String author = (String) data.get("a");
        logger.info("Fetched from ZenQuotes: {} - {}", text, author);
        return new Quote(text, author);
    }

    @SuppressWarnings("unchecked")
    private Quote fetchFromDummyJson() {
        Map<String, Object> response = restTemplate.getForObject(DUMMYJSON_API_URL, Map.class);

        if (response == null || response.get("quote") == null) {
            throw new RestClientException("Empty response from DummyJSON");
        }
        String text = (String) response.get("quote");
        String author = (String) response.get("author");
        logger.info("Fetched from DummyJSON: {} - {}", text, author);
        return new Quote(text, author);
    }

    @SuppressWarnings("unchecked")
    private Quote fetchFromForismatic() {
        // Forismatic is a GET with method=getQuote; occasionally returns
        // JSONP-ish quirks, but format=json keeps it a clean object.
        Map<String, Object> response = restTemplate.getForObject(FORISMATIC_API_URL, Map.class);

        if (response == null || response.get("quoteText") == null) {
            throw new RestClientException("Empty response from Forismatic");
        }
        String text = ((String) response.get("quoteText")).trim();
        String author = response.get("quoteAuthor") == null || ((String) response.get("quoteAuthor")).isBlank()
                ? "Unknown"
                : (String) response.get("quoteAuthor");
        logger.info("Fetched from Forismatic: {} - {}", text, author);
        return new Quote(text, author);
    }

    /**
     * Last resort: pull a random quote already sitting in your own
     * Postgres DB. Requires the table to be seeded (see saveQuote /
     * a data.sql seed script). Never depends on network at all.
     */
    private Quote fetchFromLocalDatabase() {
        List<Quote> all = quoteRepository.findAll();
        if (all.isEmpty()) {
            logger.error("All external providers failed and local database is empty");
            throw new ExternalApiException("All quote providers unavailable and no local fallback quotes exist");
        }
        Quote fallback = all.get(random.nextInt(all.size()));
        logger.info("Served from local database fallback: {} - {}", fallback.getText(), fallback.getAuthor());
        return fallback;
    }

    @Override
    public Quote saveQuote(Quote quote) {
        logger.info("Saving quote to database: {}", quote);

        if (quote.getText() == null || quote.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Quote text cannot be empty");
        }
        if (quote.getAuthor() == null || quote.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Quote author cannot be empty");
        }

        return quoteRepository.save(quote);
    }

    @Override
    public void deleteQuote(Long id) {
        logger.info("Deleting quote with ID: {}", id);

        if (!quoteRepository.existsById(id)) {
            logger.error("Quote not found with ID: {}", id);
            throw new QuoteNotFoundException("Quote not found with ID: " + id);
        }

        quoteRepository.deleteById(id);
        logger.info("Successfully deleted quote with ID: {}", id);
    }

    @Override
    public Quote getQuoteById(Long id) {
        logger.info("Fetching quote with ID: {}", id);
        return quoteRepository.findById(id)
                .orElseThrow(() -> new QuoteNotFoundException("Quote not found with ID: " + id));
    }

    @Override
    public String explainQuote(Quote quote) {
        if (quote == null) {
            throw new IllegalArgumentException("Quote cannot be null");
        }
        if (quote.getText() == null || quote.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Quote text cannot be empty");
        }

        logger.info("Generating explanation for quote: {}", quote.getText());
        return groqAiClient.generateExplanation(quote);
    }

    @Override
    public String chatAboutQuote(Quote quote, String question, List<ChatMessage> history) {
        if (quote == null) {
            throw new IllegalArgumentException("Quote cannot be null");
        }
        if (quote.getText() == null || quote.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Quote text cannot be empty");
        }
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be empty");
        }

        logger.info("Generating chat response for quote: {}", quote.getText());
        return groqAiClient.generateChatResponse(quote, question, history);
    }
}