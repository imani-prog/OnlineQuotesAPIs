# QuickStart Guide - QuoteGenerator API

## Prerequisites Checklist
- [ ] Java 17 installed
- [ ] MySQL server running
- [ ] Maven installed (or use ./mvnw)
- [ ] Your favorite IDE (IntelliJ IDEA recommended)

## Setup Steps (5 minutes)

### Step 1: Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Run this SQL command
CREATE DATABASE OnlineQuotes;

# Exit MySQL
exit;
```

Or run the provided SQL script:
```bash
mysql -u root -p < database-setup.sql
```

### Step 2: Configure Database Password
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 3: Build the Project
```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# OR using Maven
mvn clean install
```

### Step 4: Run the Application
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# OR using Maven
mvn spring-boot:run

# OR from IntelliJ IDEA
Right-click QuotesApplication.java → Run
```

### Step 5: Test the API

**Using curl:**
```bash
# Get random quote from API
curl http://localhost:8080/api/quotes/random

# Get all quotes
curl http://localhost:8080/api/quotes

# Save a quote
curl -X POST http://localhost:8080/api/quotes \
  -H "Content-Type: application/json" \
  -d '{"text":"Test quote","author":"Test Author"}'

# Fetch and save random quote
curl -X POST http://localhost:8080/api/quotes/random/save

# Delete a quote
curl -X DELETE http://localhost:8080/api/quotes/1
```

**Using Postman:**
1. Import `QuoteGenerator-Postman-Collection.json`
2. Send requests to test all endpoints

**Using Browser:**
- Open: http://localhost:8080/api/quotes/random
- Open: http://localhost:8080/api/quotes

## Expected Output

When application starts successfully, you should see:
```
Started QuotesApplication in X.XXX seconds
```

## Troubleshooting

### Problem: "Access denied for user 'root'@'localhost'"
**Solution:** Update password in `application.properties`

### Problem: "Unknown database 'OnlineQuotes'"
**Solution:** Create database: `CREATE DATABASE OnlineQuotes;`

### Problem: Port 8080 already in use
**Solution:** Change port in `application.properties`: `server.port=8081`

### Problem: Cannot connect to MySQL
**Solution:** 
```bash
# Check if MySQL is running
sudo systemctl status mysql

# Start MySQL if needed
sudo systemctl start mysql
```

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/quotes/random | Fetch random quote from API |
| GET | /api/quotes | Get all stored quotes |
| GET | /api/quotes/{id} | Get quote by ID |
| POST | /api/quotes | Save new quote |
| POST | /api/quotes/random/save | Fetch & save random quote |
| DELETE | /api/quotes/{id} | Delete quote |

## Next Steps

1. ✅ Test all endpoints with Postman
2. ✅ Add some quotes to your database
3. ✅ Try fetching random quotes from the API
4. ✅ Build a frontend if needed
5. ✅ Deploy to production

## Support

Check the main README.md for detailed documentation.

Happy coding! 🚀

