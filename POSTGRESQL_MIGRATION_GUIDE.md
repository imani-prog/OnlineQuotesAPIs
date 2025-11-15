# 🐘 PostgreSQL Migration Complete - Deployment Guide

## ✅ Migration Summary

Your application has been successfully migrated from MySQL to PostgreSQL!

### **What Changed:**
- ✅ Database: MySQL → PostgreSQL 16
- ✅ Driver: `mysql-connector-j` → `postgresql`
- ✅ Dialect: `MySQLDialect` → `PostgreSQLDialect`
- ✅ Port: 3306 → 5432 (Docker: 5433 on host)
- ✅ Docker images: `mysql:8.0` → `postgres:16-alpine`
- ✅ Sample data: 10 quotes pre-loaded

---

## 🚀 Local Development - Quick Start

### **1. Start Docker Containers**
```bash
cd /home/imanitim/CODE/OnlineQuotes/Quotes

# Start PostgreSQL + Spring Boot API
docker compose up -d

# View logs
docker compose logs -f

# Stop containers
docker compose down

# Reset database (wipe all data)
docker compose down -v && docker compose up -d
```

### **2. Access Services**
- **API:** http://localhost:8080/api/quotes
- **Health Check:** http://localhost:8080/actuator/health
- **PostgreSQL:** localhost:5433 (username: `quoteuser`, password: `quotepass123`, db: `OnlineQuotes`)

### **3. Test Endpoints**
```bash
# Health check
curl http://localhost:8080/actuator/health

# Get random quote from external API
curl http://localhost:8080/api/quotes/random

# Get all saved quotes
curl http://localhost:8080/api/quotes

# Save a quote
curl -X POST http://localhost:8080/api/quotes \
  -H "Content-Type: application/json" \
  -d '{"text":"Test quote","author":"Test Author"}'

# Delete a quote
curl -X DELETE http://localhost:8080/api/quotes/1
```

---

## ☁️ Render Deployment

### **Option 1: Use Render's Managed PostgreSQL (Recommended)**

#### **Step 1: Create PostgreSQL Database on Render**
1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click **"New +"** → **"PostgreSQL"**
3. Configure:
   - **Name:** `quotes-postgres`
   - **Database:** `OnlineQuotes`
   - **User:** `quoteuser` (or leave default)
   - **Region:** `Oregon` (same as your web service)
   - **Plan:** `Free`
4. Click **"Create Database"**
5. Wait for database to be created (~2 minutes)

#### **Step 2: Get Database Connection String**
Once created, Render will show:
- **Internal Database URL** (use this for your app)
- **External Database URL** (for tools like pgAdmin)

Example Internal URL:
```
postgresql://quoteuser:password@dpg-xxxxx-internal:5432/OnlineQuotes
```

#### **Step 3: Update Render Web Service Environment Variables**

Go to your **quotes-backend** web service → **Environment** tab:

| Key | Value |
|-----|-------|
| `DATABASE_URL` | `jdbc:postgresql://dpg-xxxxx-internal:5432/OnlineQuotes` |
| `DB_USERNAME` | `quoteuser` (from Render database) |
| `DB_PASSWORD` | `password` (from Render database) |
| `DDL_AUTO` | `update` |
| `SHOW_SQL` | `false` |
| `PORT` | `8080` |
| `DB_POOL_SIZE` | `10` |
| `DB_MIN_IDLE` | `5` |

**Important:** 
- Add `jdbc:` prefix to the Render PostgreSQL URL
- Use the **Internal** connection string (faster, no egress fees)

#### **Step 4: Deploy**
```bash
git add .
git commit -m "Migrate to PostgreSQL"
git push origin main
```

Render will automatically detect the changes and redeploy.

#### **Step 5: Verify Deployment**
Once deployed, check:
```bash
# Health check
curl https://your-app.onrender.com/actuator/health

# Test API
curl https://your-app.onrender.com/api/quotes/random
```

---

### **Option 2: Use render.yaml for Infrastructure as Code**

Update your `render.yaml`:

```yaml
services:
  # PostgreSQL Database
  - type: pserv
    name: quotes-postgres
    databaseName: OnlineQuotes
    user: quoteuser
    plan: free
    region: oregon

  # Spring Boot Backend
  - type: web
    name: quotes-backend
    runtime: docker
    plan: free
    region: oregon
    branch: main
    healthCheckPath: /actuator/health
    envVars:
      - key: DATABASE_URL
        fromDatabase:
          name: quotes-postgres
          property: connectionString
      - key: DB_USERNAME
        fromDatabase:
          name: quotes-postgres
          property: user
      - key: DB_PASSWORD
        fromDatabase:
          name: quotes-postgres
          property: password
      - key: DDL_AUTO
        value: update
      - key: SHOW_SQL
        value: false
      - key: PORT
        value: 8080
```

Then commit and push:
```bash
git add render.yaml
git commit -m "Add PostgreSQL to render.yaml"
git push origin main
```

---

## 🔧 Configuration Reference

### **application.properties (Current)**
```properties
# PostgreSQL Configuration
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5433/OnlineQuotes}
spring.datasource.username=${DB_USERNAME:quoteuser}
spring.datasource.password=${DB_PASSWORD:quotepass123}
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### **Docker Compose (Current)**
```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: quotes-postgres
    environment:
      POSTGRES_DB: OnlineQuotes
      POSTGRES_USER: quoteuser
      POSTGRES_PASSWORD: quotepass123
    ports:
      - "5433:5432"  # Host:Container
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./database-setup.sql:/docker-entrypoint-initdb.d/init.sql
```

---

## 📊 Database Schema

### **quotes Table**
```sql
CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    author VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### **Sample Data**
The database is pre-loaded with 10 inspirational quotes on first run.

---

## 🐛 Troubleshooting

### **Port 5432 Already in Use**
Your local PostgreSQL is running. We've configured Docker to use port **5433** instead.

```bash
# Verify Docker PostgreSQL is using 5433
docker compose ps

# Should show: 0.0.0.0:5433->5432/tcp
```

### **Database Connection Failed**
```bash
# Check if PostgreSQL container is healthy
docker compose ps

# View PostgreSQL logs
docker compose logs postgres

# Restart containers
docker compose restart
```

### **Render Deployment Fails**
1. **Check DATABASE_URL format:**
   - ✅ Correct: `jdbc:postgresql://host:5432/database`
   - ❌ Wrong: `postgresql://host:5432/database` (missing `jdbc:`)

2. **Verify environment variables:**
   - Go to Render Dashboard → Your service → Environment
   - Ensure `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD` are set

3. **Check logs:**
   - Render Dashboard → Your service → Logs
   - Look for connection errors

---

## 🔄 Reverting to MySQL (If Needed)

If you need to revert to MySQL:

```bash
# Restore from git
git checkout HEAD~1 pom.xml
git checkout HEAD~1 src/main/resources/application.properties
git checkout HEAD~1 docker-compose.yml

# Rebuild
mvn clean install -DskipTests
docker compose down -v
docker compose up -d --build
```

---

## ✅ Migration Checklist

- [x] Updated `pom.xml` (PostgreSQL driver)
- [x] Updated `application.properties` (PostgreSQL config)
- [x] Updated `docker-compose.yml` (PostgreSQL image)
- [x] Updated `docker-compose.fullstack.yml`
- [x] Created `database-setup.sql` (PostgreSQL syntax)
- [x] Updated `render.yaml` (optional)
- [x] Tested locally with Docker ✅
- [ ] Deploy to Render
- [ ] Test production endpoints
- [ ] Update frontend to use new backend URL

---

## 📚 Resources

- **PostgreSQL Docs:** https://www.postgresql.org/docs/
- **Render PostgreSQL:** https://render.com/docs/databases
- **Spring Boot + PostgreSQL:** https://spring.io/guides/gs/accessing-data-postgresql/
- **Hibernate PostgreSQL:** https://hibernate.org/orm/documentation/

---

## 🎉 Success!

Your application is now running on **PostgreSQL 16** with:
- ✅ No firewall/IP whitelist issues (unlike Clever Cloud MySQL)
- ✅ Better performance and scalability
- ✅ Free tier on Render with managed backups
- ✅ Industry-standard database (used by Airbnb, Instagram, Spotify)

**Next Steps:**
1. Deploy to Render
2. Test all endpoints
3. Update frontend API URL
4. Celebrate! 🚀

