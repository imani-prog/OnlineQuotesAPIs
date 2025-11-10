package com.example.quotes.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Entity class representing a Quote.
 * Maps to the 'quotes' table in the database.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "quotes")
public class Quote {
    /**
     * Primary key, auto-generated ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The quote text content
     */
    @Column(nullable = false, length = 1000)
    private String text;

    /**
     * The author of the quote
     */
    @Column(nullable = false)
    private String author;

    /**
     * Constructor without ID (used when creating new quotes)
     */
    public Quote(String text, String author) {
        this.text = text;
        this.author = author;
    }
}
