package com.example.quotes.dto;

import java.util.List;

public class QuoteChatRequest {
    private String text;
    private String author;
    private String question;
    private List<ChatMessage> history;

    public QuoteChatRequest() {
    }

    public QuoteChatRequest(String text, String author, String question, List<ChatMessage> history) {
        this.text = text;
        this.author = author;
        this.question = question;
        this.history = history;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChatMessage> getHistory() {
        return history;
    }

    public void setHistory(List<ChatMessage> history) {
        this.history = history;
    }
}

