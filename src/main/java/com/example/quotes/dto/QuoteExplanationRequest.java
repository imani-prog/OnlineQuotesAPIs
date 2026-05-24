package com.example.quotes.dto;

public class QuoteExplanationRequest {
    private String text;
    private String author;

    public QuoteExplanationRequest() {
    }

    public QuoteExplanationRequest(String text, String author) {
        this.text = text;
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

