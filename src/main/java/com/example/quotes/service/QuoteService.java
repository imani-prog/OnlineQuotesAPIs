package com.example.quotes.service;

import com.example.quotes.dto.ChatMessage;
import com.example.quotes.entities.Quote;
import java.util.List;


public interface QuoteService {

    List<Quote> getAllQuotes();

    Quote getRandomQuoteFromAPI();

    Quote saveQuote(Quote quote);

    void deleteQuote(Long id);

    Quote getQuoteById(Long id);

    String explainQuote(Quote quote);

    String chatAboutQuote(Quote quote, String question, List<ChatMessage> history);


}
