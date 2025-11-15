# 🚀 PostgreSQL Migration - Quick Commands Reference

## Local Development

### Start/Stop Docker
```bash
# Start everything
docker compose up -d

# Stop everything
docker compose down

# Reset database (wipe all data)
docker compose down -v && docker compose up -d

# View logs
docker compose logs -f
docker compose logs -f quotes-api
docker compose logs -f postgres
```

### Test Endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Get random quote (external API)
curl http://localhost:8080/api/quotes/random

# Get all quotes (local DB - should have 10 sample quotes)
curl http://localhost:8080/api/quotes

# Save a quote
curl -X POST http://localhost:8080/api/quotes \
  -H "Content-Type: application/json" \
  -d '{"text":"New quote","author":"Author Name"}'

# Delete quote by ID
curl -X DELETE http://localhost:8080/api/quotes/1
```

### Database Access
```bash
# Connect to PostgreSQL container
docker exec -it quotes-postgres psql -U quoteuser -d OnlineQuotes

# Once inside psql:
\dt                           # List tables
SELECT * FROM quotes;         # View all quotes
SELECT COUNT(*) FROM quotes;  # Count quotes
\q                            # Exit
```

---

## Render Deployment

### 1. Create PostgreSQL Database
- Go to https://dashboard.render.com
- Click **New +** → **PostgreSQL**
- Name: `quotes-postgres`
- Database: `OnlineQuotes`
- Region: `Oregon`
- Plan: **Free**
- Click **Create Database**

### 2. Get Connection String
After creation, copy the **Internal Database URL**:
```
postgresql://quoteuser:xxxxx@dpg-xxxxx-internal:5432/OnlineQuotes
```

### 3. Update Web Service Environment Variables
Go to your web service → Environment → Add these:

```bash
DATABASE_URL=jdbc:postgresql://dpg-xxxxx-internal:5432/OnlineQuotes
DB_USERNAME=quoteuser
DB_PASSWORD=xxxxx
DDL_AUTO=update
SHOW_SQL=false
PORT=8080
DB_POOL_SIZE=10
DB_MIN_IDLE=5
```

**Important:** Add `jdbc:` prefix to the Render PostgreSQL URL!

### 4. Deploy
```bash
git add .
git commit -m "Migrate to PostgreSQL"
git push origin main
```

### 5. Verify
```bash
curl https://your-app.onrender.com/actuator/health
curl https://your-app.onrender.com/api/quotes
```

---

## Files Changed

- ✅ `pom.xml` - PostgreSQL driver
- ✅ `application.properties` - PostgreSQL config
- ✅ `docker-compose.yml` - PostgreSQL container (port 5433)
- ✅ `docker-compose.fullstack.yml` - Full stack with PostgreSQL
- ✅ `database-setup.sql` - PostgreSQL init script
- ✅ `Dockerfile.postgres` - Optional Postgres image
- ✅ `render.yaml` - PostgreSQL deployment config

---

## Connection Details

### Local (Docker)
- **Host:** localhost
- **Port:** 5433
- **Database:** OnlineQuotes
- **Username:** quoteuser
- **Password:** quotepass123
- **JDBC URL:** `jdbc:postgresql://localhost:5433/OnlineQuotes`

### Render (Production)
- **Host:** dpg-xxxxx-internal (from Render)
- **Port:** 5432
- **Database:** OnlineQuotes
- **Username:** (from Render)
- **Password:** (from Render)
- **JDBC URL:** `jdbc:postgresql://dpg-xxxxx-internal:5432/OnlineQuotes`

---

## Troubleshooting

### Port 5432 in use locally
We're using port **5433** on host to avoid conflicts with local PostgreSQL.

### Database connection failed
```bash
docker compose logs postgres
docker compose restart
```

### No sample data
```bash
# Recreate database with init script
docker compose down -v
docker compose up -d

# Wait 10 seconds, then:
curl http://localhost:8080/api/quotes
```

### Render deployment fails
- Check DATABASE_URL has `jdbc:` prefix
- Verify all environment variables are set
- Check Render logs for specific errors

---

## Quick Validation

```bash
# 1. Check containers
docker compose ps

# 2. Test health
curl http://localhost:8080/actuator/health

# 3. Test API
curl http://localhost:8080/api/quotes

# 4. Expected: 10 sample quotes
```

---

Read **POSTGRESQL_MIGRATION_GUIDE.md** for complete documentation.

