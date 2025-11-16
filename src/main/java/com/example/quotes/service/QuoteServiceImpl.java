package com.example.quotes.service;

import com.example.quotes.entities.Quote;
import com.example.quotes.exception.QuoteNotFoundException;
import com.example.quotes.exception.ExternalApiException;
import com.example.quotes.repository.QuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;


@Service
public class QuoteServiceImpl implements QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);
    private static final String ZENQUOTES_API_URL = "https://zenquotes.io/api/random";

    private final QuoteRepository quoteRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public QuoteServiceImpl(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<Quote> getAllQuotes() {
        logger.info("Fetching all quotes from database");
        return quoteRepository.findAll();
    }

    @Override
    public Quote getRandomQuoteFromAPI() {
        logger.info("Fetching random quote from ZenQuotes API");

        try {

            Map<String, Object>[] response = restTemplate.getForObject(ZENQUOTES_API_URL, Map[].class);

            if (response != null && response.length > 0) {
                Map<String, Object> quoteData = response[0];
                String text = (String) quoteData.get("q");
                String author = (String) quoteData.get("a");

                logger.info("Successfully fetched quote from API: {} - {}", text, author);

                // Create and return Quote object (not saved to database yet)
                return new Quote(text, author);
            } else {
                logger.error("Empty response from ZenQuotes API");
                throw new ExternalApiException("Failed to fetch quote from external API: Empty response");
            }

        } catch (RestClientException e) {
            logger.error("Error calling ZenQuotes API: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch quote from external API: " + e.getMessage());
        }
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
}

