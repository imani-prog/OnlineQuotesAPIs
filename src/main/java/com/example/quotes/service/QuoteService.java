package com.example.quotes.service;

import com.example.quotes.entities.Quote;
import java.util.List;

/**
 * Service interface defining operations for Quote management.
 */
public interface QuoteService {

    /**
     * Retrieves all quotes from the local database.
     * @return List of all quotes
     */
    List<Quote> getAllQuotes();

    /**
     * Fetches a random quote from the online API (ZenQuotes.io).
     * @return A random quote from the API
     */
    Quote getRandomQuoteFromAPI();

    /**
     * Saves a quote to the local database.
     * @param quote The quote to save
     * @return The saved quote with generated ID
     */
    Quote saveQuote(Quote quote);

    /**
     * Deletes a quote by its ID.
     * @param id The ID of the quote to delete
     */
    void deleteQuote(Long id);

    /**
     * Retrieves a quote by its ID.
     * @param id The ID of the quote
     * @return The quote if found
     */
    Quote getQuoteById(Long id);
}

