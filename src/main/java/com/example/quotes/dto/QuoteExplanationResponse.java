package com.example.quotes.dto;

public class QuoteExplanationResponse {
    private String text;
    private String author;
    private String explanation;

    public QuoteExplanationResponse() {
    }

    public QuoteExplanationResponse(String text, String author, String explanation) {
        this.text = text;
        this.author = author;
        this.explanation = explanation;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

