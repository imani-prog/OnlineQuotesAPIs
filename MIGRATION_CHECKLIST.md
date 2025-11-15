# ✅ PostgreSQL Migration Checklist

## 📋 Pre-Migration (Completed)
- [x] Backup existing MySQL data (if any)
- [x] Review project requirements
- [x] Identify MySQL-specific code
- [x] Plan migration strategy

## 🔧 Code Migration (Completed)
- [x] Update `pom.xml` - Replace `mysql-connector-j` with `postgresql`
- [x] Update `application.properties` - PostgreSQL driver and dialect
- [x] Update `docker-compose.yml` - PostgreSQL container
- [x] Update `docker-compose.fullstack.yml` - Full stack setup
- [x] Create `database-setup.sql` - PostgreSQL init script
- [x] Update `render.yaml` - Render PostgreSQL config
- [x] Create `Dockerfile.postgres` - Optional PostgreSQL image
- [x] Fix port conflicts (5432 → 5433 on host)

## 🧪 Local Testing (Completed)
- [x] Maven build successful (`mvn clean install`)
- [x] Docker containers start (`docker compose up -d`)
- [x] PostgreSQL container healthy
- [x] Spring Boot connects to database
- [x] Sample data loaded (10 quotes)
- [x] Health endpoint returns UP
- [x] Random quote API works
- [x] Get all quotes returns data
- [x] POST new quote works
- [x] DELETE quote works

## 📚 Documentation (Completed)
- [x] Create migration summary (`00_MIGRATION_SUMMARY.md`)
- [x] Create detailed guide (`POSTGRESQL_MIGRATION_GUIDE.md`)
- [x] Create quick reference (`QUICK_POSTGRES_COMMANDS.md`)
- [x] Update README (if needed)

## ☁️ Render Deployment (Pending)
- [ ] **Step 1:** Create PostgreSQL database on Render
  - [ ] Login to https://dashboard.render.com
  - [ ] Click "New +" → "PostgreSQL"
  - [ ] Name: `quotes-postgres`
  - [ ] Database: `OnlineQuotes`
  - [ ] Region: `Oregon`
  - [ ] Plan: **Free**
  - [ ] Click "Create Database"

- [ ] **Step 2:** Get connection details
  - [ ] Wait for database creation (~2 minutes)
  - [ ] Copy **Internal Database URL**
  - [ ] Example: `postgresql://user:pass@dpg-xxxxx-internal:5432/OnlineQuotes`

- [ ] **Step 3:** Update web service environment variables
  - [ ] Go to your backend service → Environment
  - [ ] Add/update these variables:
    ```
    DATABASE_URL    = jdbc:postgresql://dpg-xxxxx-internal:5432/OnlineQuotes
    DB_USERNAME     = (from Render database)
    DB_PASSWORD     = (from Render database)
    DDL_AUTO        = update
    SHOW_SQL        = false
    PORT            = 8080
    DB_POOL_SIZE    = 10
    DB_MIN_IDLE     = 5
    ```
  - [ ] **Important:** Add `jdbc:` prefix to DATABASE_URL!

- [ ] **Step 4:** Deploy to Render
  ```bash
  git add .
  git commit -m "Migrate from MySQL to PostgreSQL"
  git push origin main
  ```

- [ ] **Step 5:** Monitor deployment
  - [ ] Watch Render logs
  - [ ] Look for "DATABASE CONNECTION SUCCESSFUL!"
  - [ ] Check for "Started QuotesApplication"

## 🧪 Production Testing (After Deployment)
- [ ] Health check: `curl https://your-app.onrender.com/actuator/health`
- [ ] Random quote: `curl https://your-app.onrender.com/api/quotes/random`
- [ ] Get all quotes: `curl https://your-app.onrender.com/api/quotes`
- [ ] POST new quote (test with Postman/curl)
- [ ] DELETE quote (test with Postman/curl)

## 🔗 Frontend Integration (After Backend Deployed)
- [ ] Update frontend API base URL
- [ ] Update CORS origins in backend (if needed)
- [ ] Test quote fetching from frontend
- [ ] Test quote saving from frontend
- [ ] Test quote deletion from frontend
- [ ] Deploy frontend

## 📊 Post-Deployment Validation
- [ ] Verify database connections (check Render logs)
- [ ] Monitor API response times
- [ ] Check database usage in Render dashboard
- [ ] Verify sample data exists
- [ ] Test all CRUD operations
- [ ] Monitor error logs

## 🎯 Optional Improvements
- [ ] Set up database backups (automatic with Render)
- [ ] Add database indexes for performance
- [ ] Configure connection pooling (already done)
- [ ] Set up monitoring/alerts
- [ ] Add database migrations tool (Flyway/Liquibase)
- [ ] Configure SSL for database (Render handles this)

## 🗑️ Cleanup
- [ ] Remove old MySQL guides (Clever Cloud docs)
- [ ] Archive old MySQL docker volumes
- [ ] Remove orphan MySQL containers
- [ ] Update project README
- [ ] Tag git commit: `git tag v2.0-postgresql`

## 📝 Notes

### Database URL Format
```
# PostgreSQL Internal URL from Render:
postgresql://user:password@host-internal:5432/database

# Spring Boot JDBC URL (add jdbc: prefix):
jdbc:postgresql://host-internal:5432/database
```

### Common Issues
1. **Missing `jdbc:` prefix** → Add it to DATABASE_URL
2. **Wrong credentials** → Match Render database user/password
3. **Connection timeout** → Check network/firewall settings
4. **Table not created** → Verify DDL_AUTO=update

### Success Indicators
- ✅ Render logs show: "DATABASE CONNECTION SUCCESSFUL!"
- ✅ Health endpoint returns `{"status":"UP"}`
- ✅ API endpoints return data
- ✅ No errors in Render logs

### Rollback Plan (If Needed)
```bash
# Revert to MySQL
git checkout HEAD~1 pom.xml
git checkout HEAD~1 src/main/resources/application.properties
git checkout HEAD~1 docker-compose.yml
mvn clean install
docker compose down -v
docker compose up -d
```

## 🎉 Migration Complete When:
- [x] All local tests pass
- [x] Documentation complete
- [ ] Render PostgreSQL created
- [ ] Environment variables set
- [ ] Backend deployed successfully
- [ ] Production tests pass
- [ ] Frontend connected
- [ ] No errors in logs

---

**Current Status:** Local migration complete ✅  
**Next Step:** Deploy to Render ☁️  
**Estimated Time:** 10-15 minutes  

**Read:** `00_MIGRATION_SUMMARY.md` for complete overview

