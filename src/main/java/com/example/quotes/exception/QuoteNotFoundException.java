package com.example.quotes.exception;

/**
 * Exception thrown when a quote is not found in the database.
 */
public class QuoteNotFoundException extends RuntimeException {

    public QuoteNotFoundException(String message) {
        super(message);
    }

    public QuoteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
