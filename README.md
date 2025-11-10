# QuoteGenerator - Spring Boot 3 REST API

A RESTful API application built with Spring Boot 3 and Java 17 that fetches random quotes from the ZenQuotes.io API and manages quotes in a local MySQL database.

## Features

- Fetch random quotes from ZenQuotes.io API
- Store quotes locally in MySQL database
- Full CRUD operations for quotes
- Clean architecture with layered design (Controller → Service → Repository)
- Comprehensive error handling
- CORS enabled for frontend integration
- RESTful API design with proper HTTP status codes

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA** (Hibernate)
- **MySQL** (Database)
- **Lombok** (Reducing boilerplate code)
- **Maven** (Build tool)
- **RestTemplate** (HTTP client for external API calls)
- **HikariCP** (Connection pooling)

## Prerequisites

Before running the application, ensure you have:

1. **Java 17** or higher installed
2. **MySQL** server running locally
3. **Maven** installed (or use the included Maven wrapper)
4. **IntelliJ IDEA** or any Java IDE (optional)

## Database Setup

1. Start your MySQL server

2. Create the database:
```sql
CREATE DATABASE OnlineQuotes;
```

3. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_password_here
```

**Note:** The application will automatically create the `quotes` table when it starts (thanks to `spring.jpa.hibernate.ddl-auto=update`).

## Installation & Running

### Option 1: Using Maven Wrapper (Recommended)

```bash
# On Linux/Mac
./mvnw clean install
./mvnw spring-boot:run

# On Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### Option 2: Using Maven

```bash
mvn clean install
mvn spring-boot:run
```

### Option 3: Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Wait for Maven dependencies to download
3. Right-click on `QuotesApplication.java`
4. Select "Run 'QuotesApplication'"

The application will start on **http://localhost:8080**

## API Endpoints

### 1. Get Random Quote from API
Fetches a random quote from ZenQuotes.io (does not save to database).

```
GET http://localhost:8080/api/quotes/random
```

**Response:**
```json
{
  "id": null,
  "text": "The only way to do great work is to love what you do.",
  "author": "Steve Jobs"
}
```

---

### 2. Get All Quotes from Database
Retrieves all quotes stored in the local database.

```
GET http://localhost:8080/api/quotes
```

**Response:**
```json
{
  "count": 2,
  "quotes": [
    {
      "id": 1,
      "text": "The only way to do great work is to love what you do.",
      "author": "Steve Jobs"
    },
    {
      "id": 2,
      "text": "Life is what happens when you're busy making other plans.",
      "author": "John Lennon"
    }
  ]
}
```

---

### 3. Get Quote by ID
Retrieves a specific quote by its ID.

```
GET http://localhost:8080/api/quotes/{id}
```

**Example:** `GET http://localhost:8080/api/quotes/1`

**Response:**
```json
{
  "id": 1,
  "text": "The only way to do great work is to love what you do.",
  "author": "Steve Jobs"
}
```

---

### 4. Save a Quote
Saves a new quote to the database.

```
POST http://localhost:8080/api/quotes
Content-Type: application/json

{
  "text": "Be yourself; everyone else is already taken.",
  "author": "Oscar Wilde"
}
```

**Response:**
```json
{
  "id": 3,
  "text": "Be yourself; everyone else is already taken.",
  "author": "Oscar Wilde"
}
```

---

### 5. Fetch and Save Random Quote
Fetches a random quote from the API and saves it to the database.

```
POST http://localhost:8080/api/quotes/random/save
```

**Response:**
```json
{
  "message": "Quote fetched from API and saved successfully",
  "quote": {
    "id": 4,
    "text": "Success is not final, failure is not fatal.",
    "author": "Winston Churchill"
  }
}
```

---

### 6. Delete a Quote
Deletes a quote by its ID.

```
DELETE http://localhost:8080/api/quotes/{id}
```

**Example:** `DELETE http://localhost:8080/api/quotes/1`

**Response:**
```json
{
  "message": "Quote deleted successfully",
  "deletedId": 1
}
```

---

## Error Handling

The API includes comprehensive error handling:

### Quote Not Found (404)
```json
{
  "timestamp": "2025-11-10T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Quote not found with ID: 999",
  "path": "/api/quotes/999"
}
```

### External API Error (503)
```json
{
  "timestamp": "2025-11-10T14:30:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Failed to fetch quote from external API: Connection timeout",
  "path": "/api/quotes/random"
}
```

### Validation Error (400)
```json
{
  "timestamp": "2025-11-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Quote text cannot be empty",
  "path": "/api/quotes"
}
```

## Project Structure

```
src/main/java/com/example/quotes/
├── QuotesApplication.java          # Main application class
├── controller/
│   └── QuoteController.java        # REST endpoints
├── service/
│   ├── QuoteService.java           # Service interface
│   └── QuoteServiceImpl.java       # Service implementation
├── repository/
│   └── QuoteRepository.java        # JPA repository
├── entities/
│   └── Quote.java                  # JPA entity
└── exception/
    ├── QuoteNotFoundException.java
    ├── ExternalApiException.java
    └── GlobalExceptionHandler.java # Global exception handling

src/main/resources/
└── application.properties          # Configuration
```

## Testing with Postman

1. Import the following requests into Postman:
   - GET Random Quote: `http://localhost:8080/api/quotes/random`
   - GET All Quotes: `http://localhost:8080/api/quotes`
   - POST Save Quote: `http://localhost:8080/api/quotes`
   - DELETE Quote: `http://localhost:8080/api/quotes/1`

2. For POST requests, set:
   - Headers: `Content-Type: application/json`
   - Body: Raw JSON

## Configuration

### Application Properties

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/OnlineQuotes
spring.datasource.username=root
spring.datasource.password=your_password_here

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server
server.port=8080
```

### Switching to Remote MySQL

To use a remote MySQL database, simply update the URL in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://your-remote-host:3306/OnlineQuotes
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Troubleshooting

### Database Connection Issues

1. Ensure MySQL is running: `sudo systemctl status mysql`
2. Verify database exists: `SHOW DATABASES;`
3. Check credentials in `application.properties`
4. Ensure MySQL is accepting connections on port 3306

### API Not Responding

1. Check if application started successfully
2. Look for error messages in console
3. Verify port 8080 is not in use
4. Check firewall settings

### External API (ZenQuotes) Not Working

- The ZenQuotes.io API may have rate limits
- Check your internet connection
- The API is free and doesn't require authentication
- If the API is down, you can still use the local database features

## Future Enhancements

- Add pagination for GET all quotes
- Implement search functionality
- Add quote categories
- User authentication and authorization
- Rate limiting for API calls
- Caching for frequently accessed quotes
- Frontend UI (React/Angular)
- Docker containerization
- CI/CD pipeline

## License

This project is open source and available for educational purposes.

## Contact

For questions or issues, please create an issue in the repository.

---

**Enjoy generating and managing quotes!** 📝✨

