-- PostgreSQL Database Setup Script for OnlineQuotes
-- This script initializes the database schema and tables

-- Create database (if running manually)
-- CREATE DATABASE OnlineQuotes;

-- Connect to the database
-- \c OnlineQuotes;

-- Drop table if exists (for clean setup)
DROP TABLE IF EXISTS quotes CASCADE;

-- Create quotes table
CREATE TABLE IF NOT EXISTS quotes (
    id BIGSERIAL PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    author VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on author for faster searches
CREATE INDEX IF NOT EXISTS idx_quotes_author ON quotes(author);

-- Insert sample quotes
INSERT INTO quotes (text, author) VALUES
('The only way to do great work is to love what you do.', 'Steve Jobs'),
('Innovation distinguishes between a leader and a follower.', 'Steve Jobs'),
('Stay hungry, stay foolish.', 'Steve Jobs'),
('The future belongs to those who believe in the beauty of their dreams.', 'Eleanor Roosevelt'),
('It is during our darkest moments that we must focus to see the light.', 'Aristotle'),
('The only impossible journey is the one you never begin.', 'Tony Robbins'),
('Success is not final, failure is not fatal: It is the courage to continue that counts.', 'Winston Churchill'),
('Believe you can and you are halfway there.', 'Theodore Roosevelt'),
('The best time to plant a tree was 20 years ago. The second best time is now.', 'Chinese Proverb'),
('Your limitation—it is only your imagination.', 'Unknown');

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
DROP TRIGGER IF EXISTS update_quotes_updated_at ON quotes;
CREATE TRIGGER update_quotes_updated_at
    BEFORE UPDATE ON quotes
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Display confirmation message
DO $$
BEGIN
    RAISE NOTICE 'Database setup completed successfully!';
    RAISE NOTICE 'Sample quotes inserted: 10 records';
END $$;

