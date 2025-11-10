# Application Configuration Templates

## Local Development (application.properties - CURRENT)
```properties
spring.application.name=Quotes

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/OnlineQuotes
spring.datasource.username=root
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080

# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

## Production Configuration (application-prod.properties)
```properties
spring.application.name=Quotes

# MySQL Database Configuration - Use environment variables
spring.datasource.url=${DB_URL:jdbc:mysql://your-production-db:3306/OnlineQuotes}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration - NEVER use 'create' or 'create-drop' in production
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=false

# Server Configuration
server.port=${PORT:8080}

# HikariCP Connection Pool - Optimized for production
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Logging
logging.level.root=INFO
logging.level.com.example.quotes=INFO

# Security (if adding Spring Security later)
# spring.security.user.name=admin
# spring.security.user.password=${ADMIN_PASSWORD}
```

## Docker Configuration (application-docker.properties)
```properties
spring.application.name=Quotes

# MySQL Database Configuration - Docker container
spring.datasource.url=jdbc:mysql://mysql-container:3306/OnlineQuotes
spring.datasource.username=root
spring.datasource.password=docker_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server Configuration
server.port=8080

# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

## Cloud Deployment (application-cloud.properties)
```properties
spring.application.name=Quotes

# Cloud Database Configuration (e.g., AWS RDS, Azure Database)
spring.datasource.url=${CLOUD_DB_URL}
spring.datasource.username=${CLOUD_DB_USERNAME}
spring.datasource.password=${CLOUD_DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server Configuration
server.port=${PORT:8080}

# HikariCP Connection Pool - Cloud optimized
spring.datasource.hikari.maximum-pool-size=15
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.leak-detection-threshold=60000

# Actuator endpoints (for health checks)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized

# Logging
logging.level.root=WARN
logging.level.com.example.quotes=INFO
```

## How to Use Different Profiles

### Run with specific profile:
```bash
# Development (default)
./mvnw spring-boot:run

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Docker
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker

# Cloud
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud
```

### Using environment variables:
```bash
# Set environment variables
export DB_URL=jdbc:mysql://production-server:3306/OnlineQuotes
export DB_USERNAME=produser
export DB_PASSWORD=secure_password

# Run application
java -jar target/Quotes-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Important Notes:

1. **NEVER commit passwords** to version control
2. Use environment variables in production
3. Set `ddl-auto=validate` in production (not `update`)
4. Disable SQL logging in production
5. Use proper connection pool settings
6. Enable SSL for database connections in production
7. Consider using Spring Cloud Config for centralized configuration
8. Use secrets management (AWS Secrets Manager, Azure Key Vault, etc.)

## Security Checklist for Production:

- [ ] Change default passwords
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS/SSL
- [ ] Add Spring Security
- [ ] Implement rate limiting
- [ ] Add authentication/authorization
- [ ] Use proper CORS configuration (not origins="*")
- [ ] Enable database SSL
- [ ] Set up monitoring and alerting
- [ ] Configure proper logging
- [ ] Use a reverse proxy (Nginx, Apache)
- [ ] Implement API versioning

