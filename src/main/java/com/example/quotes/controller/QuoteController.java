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

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "*")
public class QuoteController {

    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);

    private final QuoteService quoteService;

    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/random")
    public ResponseEntity<Quote> getRandomQuote() {
        logger.info("Received request for random quote from API");
        Quote quote = quoteService.getRandomQuoteFromAPI();
        return ResponseEntity.ok(quote);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable Long id) {
        logger.info("Received request to get quote with ID: {}", id);
        Quote quote = quoteService.getQuoteById(id);
        return ResponseEntity.ok(quote);
    }

    @PostMapping
    public ResponseEntity<Quote> saveQuote(@RequestBody Quote quote) {
        logger.info("Received request to save quote: {}", quote);
        Quote savedQuote = quoteService.saveQuote(quote);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuote);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveQuoteAlt(@RequestBody Quote quote) {
        logger.info("Received request to save quote via /save endpoint: {}", quote);
        Quote savedQuote = quoteService.saveQuote(quote);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Quote saved successfully");
        response.put("quote", savedQuote);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/random/save")
    public ResponseEntity<Map<String, Object>> fetchAndSaveRandomQuote() {
        logger.info("Received request to fetch and save random quote");

        Quote randomQuote = quoteService.getRandomQuoteFromAPI();
        Quote savedQuote = quoteService.saveQuote(randomQuote);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Quote fetched from API and saved successfully");
        response.put("quote", savedQuote);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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

