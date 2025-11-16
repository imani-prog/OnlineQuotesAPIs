package com.example.quotes.service;

import com.example.quotes.entities.Quote;
import java.util.List;


public interface QuoteService {

    List<Quote> getAllQuotes();

    Quote getRandomQuoteFromAPI();


    Quote saveQuote(Quote quote);

    void deleteQuote(Long id);

    Quote getQuoteById(Long id);
}

