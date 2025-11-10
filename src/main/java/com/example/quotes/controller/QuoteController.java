package com.example.quotes.controller;

import com.example.quotes.entities.Quote;
import com.example.quotes.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Quote operations.
 * Exposes endpoints for fetching quotes from API and managing local quotes.
 */
@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "*")
public class QuoteController {

    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);

    private final QuoteService quoteService;

    /**
     * Constructor injection for QuoteService.
     * @param quoteService Service layer for quote operations
     */
    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    /**
     * GET /api/quotes/random
     * Fetches a random quote from the ZenQuotes.io API.
     * @return ResponseEntity containing the random quote
     */
    @GetMapping("/random")
    public ResponseEntity<Quote> getRandomQuote() {
        logger.info("Received request for random quote from API");
        Quote quote = quoteService.getRandomQuoteFromAPI();
        return ResponseEntity.ok(quote);
    }

    /**
     * GET /api/quotes
     * Retrieves all quotes from the local database.
     * @return ResponseEntity containing list of all quotes
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllQuotes() {
        logger.info("Received request to get all quotes");
        List<Quote> quotes = quoteService.getAllQuotes();

        Map<String, Object> response = new HashMap<>();
        response.put("count", quotes.size());
        response.put("quotes", quotes);

        if (quotes.isEmpty()) {
            response.put("message", "No quotes found in database");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/quotes/{id}
     * Retrieves a specific quote by ID.
     * @param id The ID of the quote to retrieve
     * @return ResponseEntity containing the quote
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable Long id) {
        logger.info("Received request to get quote with ID: {}", id);
        Quote quote = quoteService.getQuoteById(id);
        return ResponseEntity.ok(quote);
    }

    /**
     * POST /api/quotes
     * Saves a new quote to the local database.
     * @param quote The quote to save (from request body)
     * @return ResponseEntity containing the saved quote
     */
    @PostMapping
    public ResponseEntity<Quote> saveQuote(@RequestBody Quote quote) {
        logger.info("Received request to save quote: {}", quote);
        Quote savedQuote = quoteService.saveQuote(quote);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuote);
    }

    /**
     * POST /api/quotes/random/save
     * Fetches a random quote from API and saves it to the database.
     * @return ResponseEntity containing the saved quote
     */
    @PostMapping("/random/save")
    public ResponseEntity<Map<String, Object>> fetchAndSaveRandomQuote() {
        logger.info("Received request to fetch and save random quote");

        // Fetch quote from API
        Quote randomQuote = quoteService.getRandomQuoteFromAPI();

        // Save it to database
        Quote savedQuote = quoteService.saveQuote(randomQuote);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Quote fetched from API and saved successfully");
        response.put("quote", savedQuote);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /api/quotes/{id}
     * Deletes a quote by its ID.
     * @param id The ID of the quote to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuote(@PathVariable Long id) {
        logger.info("Received request to delete quote with ID: {}", id);
        quoteService.deleteQuote(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Quote deleted successfully");
        response.put("deletedId", id);

        return ResponseEntity.ok(response);
    }
}

