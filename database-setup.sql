-- SQL Script to set up the MySQL database for QuoteGenerator application
-- Run this script in your MySQL client before starting the application

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS OnlineQuotes;

-- Use the database
USE OnlineQuotes;

-- The 'quotes' table will be automatically created by Spring Boot JPA
-- when the application starts (due to spring.jpa.hibernate.ddl-auto=update)

-- However, if you want to create it manually, use this:
-- CREATE TABLE IF NOT EXISTS quotes (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     text VARCHAR(1000) NOT NULL,
--     author VARCHAR(255) NOT NULL
-- );

-- Optional: Insert some sample data
-- INSERT INTO quotes (text, author) VALUES
-- ('The only way to do great work is to love what you do.', 'Steve Jobs'),
-- ('Life is what happens when you are busy making other plans.', 'John Lennon'),
-- ('Be yourself; everyone else is already taken.', 'Oscar Wilde');

-- Verify the setup
SHOW DATABASES;
SELECT DATABASE();

