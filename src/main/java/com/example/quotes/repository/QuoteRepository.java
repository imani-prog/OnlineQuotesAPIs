package com.example.quotes.repository;

import com.example.quotes.entities.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Quote entity.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    // JpaRepository provides all basic CRUD operations
    // Custom query methods can be added here if needed
}

