package com.example.quotes.dto;

public class QuoteChatResponse {
    private String text;
    private String author;
    private String question;
    private String answer;

    public QuoteChatResponse() {
    }

    public QuoteChatResponse(String text, String author, String question, String answer) {
        this.text = text;
        this.author = author;
        this.question = question;
        this.answer = answer;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

