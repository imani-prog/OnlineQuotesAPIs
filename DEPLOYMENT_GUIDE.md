
# Run MySQL container
docker run -d \
  --name quotes-mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=OnlineQuotes \
  -p 3307:3306 \
  mysql:8.0

# Run Spring Boot app
docker run -d \
  --name quotes-api \
  -p 8080:8080 \
  --link quotes-mysql:mysql \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/OnlineQuotes \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=rootpassword \
  quotes-api
```

### Test Your API
```bash
# Get random quote from API
curl http://localhost:8080/api/quotes/random

# Get all saved quotes
curl http://localhost:8080/api/quotes

# Save a quote
curl -X POST http://localhost:8080/api/quotes \
  -H "Content-Type: application/json" \
  -d '{"text":"Success is not final","author":"Winston Churchill"}'
```

---

## 2. 🚂 Railway.app (RECOMMENDED — Easiest!)

### ✅ Pros
- **$5 FREE credit/month** (500 hours runtime)
- Built-in MySQL database
- Auto-deploy from GitHub
- Zero configuration
- Free SSL certificates
- Persistent storage included

### 📝 Setup Steps

#### Step 1: Sign Up
1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Verify your email

#### Step 2: Create New Project
```bash
# Option A: Deploy from GitHub
1. Click "New Project"
2. Select "Deploy from GitHub repo"
3. Choose your repo: imani-prog/OnlineQuotesAPIs
4. Railway auto-detects Spring Boot

# Option B: Deploy with CLI
npm install -g @railway/cli
railway login
railway init
railway up
```

#### Step 3: Add MySQL Database
1. Click "+ New" → "Database" → "MySQL"
2. Railway creates database automatically
3. Copy connection details

#### Step 4: Set Environment Variables
In Railway dashboard, add these variables:
```env
SPRING_DATASOURCE_URL=mysql://railway.app:PORT/railway
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=<from-railway-mysql>
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
PORT=8080
```

Railway auto-injects `DATABASE_URL` — use it:
```env
SPRING_DATASOURCE_URL=${DATABASE_URL}
```

#### Step 5: Deploy
- Push to GitHub → Railway auto-deploys
- Get public URL: `https://your-app.railway.app`

### 🔧 Railway Configuration File (Optional)
Create `railway.json`:
```json
{
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean package -DskipTests"
  },
  "deploy": {
    "startCommand": "java -jar target/Quotes-0.0.1-SNAPSHOT.jar",
    "healthcheckPath": "/api/quotes/random",
    "restartPolicyType": "ON_FAILURE"
  }
}
```

---

## 3. 🎨 Render.com

### ✅ Pros
- **Free tier** (spins down after 15 min inactivity)
- Free PostgreSQL database (better than MySQL on free tier)
- Auto-deploy from GitHub
- Free SSL

### ❌ Cons
- Cold start (30s-1min after inactivity)
- Limited to 750 hours/month

### 📝 Setup Steps

#### Step 1: Modify for PostgreSQL
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Create `application-prod.properties`:
```properties
spring.datasource.url=${DATABASE_URL}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

#### Step 2: Create Render Web Service
1. Go to [render.com](https://render.com)
2. Connect GitHub repo
3. Create "Web Service"
4. Settings:
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/Quotes-0.0.1-SNAPSHOT.jar`
   - **Environment**: Java 17

#### Step 3: Add PostgreSQL Database
1. Create "PostgreSQL" service
2. Copy Internal Database URL
3. Add env var in Web Service:
   ```
   DATABASE_URL=<internal-postgres-url>
   SPRING_PROFILES_ACTIVE=prod
   ```

#### Step 4: Deploy
- Render auto-deploys on push
- URL: `https://your-app.onrender.com`

---

## 4. ✈️ Fly.io

### ✅ Pros
- **Free tier**: 3 VMs, 3GB storage
- Fast global CDN
- Persistent volumes
- Docker-native

### 📝 Setup Steps

#### Step 1: Install Fly CLI
```bash
# Linux/macOS
curl -L https://fly.io/install.sh | sh

# Login
flyctl auth login
```

#### Step 2: Initialize Fly App
```bash
flyctl launch

# Answer prompts:
# - App name: quotes-api
# - Region: choose nearest
# - PostgreSQL: Yes (or use PlanetScale MySQL)
```

#### Step 3: Configure `fly.toml`
```toml
app = "quotes-api"
primary_region = "iad"

[build]
  dockerfile = "Dockerfile"

[env]
  PORT = "8080"
  SPRING_PROFILES_ACTIVE = "prod"

[http_service]
  internal_port = 8080
  force_https = true
  auto_stop_machines = true
  auto_start_machines = true
  min_machines_running = 0

[[vm]]
  cpu_kind = "shared"
  cpus = 1
  memory_mb = 256
```

#### Step 4: Set Secrets
```bash
flyctl secrets set \
  SPRING_DATASOURCE_URL=jdbc:postgresql://... \
  SPRING_DATASOURCE_USERNAME=postgres \
  SPRING_DATASOURCE_PASSWORD=yourpassword
```

#### Step 5: Deploy
```bash
flyctl deploy

# Get URL
flyctl status
```

---

## 5. 💜 Heroku (with ClearDB)

### ✅ Pros
- Well-known platform
- Free MySQL via ClearDB addon (5MB free)
- Easy CLI

### ❌ Cons
- No more free tier (starting 2022)
- Eco Dyno: $5/month

### 📝 Setup Steps

#### Step 1: Create `Procfile`
```
web: java -jar target/Quotes-0.0.1-SNAPSHOT.jar
```

#### Step 2: Deploy
```bash
heroku login
heroku create quotes-api-prod

# Add ClearDB MySQL
heroku addons:create cleardb:ignite

# Get database URL
heroku config:get CLEARDB_DATABASE_URL

# Set as Spring datasource
heroku config:set SPRING_DATASOURCE_URL=jdbc:mysql://...

# Deploy
git push heroku main
```

---

## 6. ☁️ Google Cloud Run (Free Tier)

### ✅ Pros
- **Free tier**: 2M requests/month
- Serverless (pay per use)
- Auto-scaling
- Use Cloud SQL or external DB

### 📝 Setup Steps

#### Step 1: Build Docker Image
```bash
# Build and tag
docker build -t gcr.io/YOUR_PROJECT_ID/quotes-api .

# Push to Google Container Registry
docker push gcr.io/YOUR_PROJECT_ID/quotes-api
```

#### Step 2: Deploy to Cloud Run
```bash
gcloud run deploy quotes-api \
  --image gcr.io/YOUR_PROJECT_ID/quotes-api \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars SPRING_DATASOURCE_URL=jdbc:mysql://... \
  --max-instances 1
```

#### Use Free Database Options:
- **PlanetScale** (Free 5GB MySQL)
- **Aiven** (Free 1GB MySQL)
- **FreeSQLDatabase.com**

---

## 7. 🔷 Azure Container Apps (Free Tier)

### ✅ Pros
- **Free tier**: 180K vCPU-seconds/month
- Built-in CI/CD
- Azure MySQL or external DB

### 📝 Setup Steps
```bash
# Install Azure CLI
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Login
az login

# Create resource group
az group create --name quotes-rg --location eastus

# Deploy container app
az containerapp up \
  --name quotes-api \
  --resource-group quotes-rg \
  --source .
```

---

## 📊 Comparison Table

| Platform | Free Tier | Database | Cold Start | Ease | Best For |
|----------|-----------|----------|------------|------|----------|
| **Railway** | ✅ $5 credit/mo | MySQL ✅ | ❌ No | ⭐⭐⭐⭐⭐ | Beginners |
| **Render** | ✅ 750hrs/mo | PostgreSQL ✅ | ⚠️ Yes (30s) | ⭐⭐⭐⭐ | Small projects |
| **Fly.io** | ✅ 3 VMs | PostgreSQL ✅ | ❌ No | ⭐⭐⭐ | Production-ready |
| **Heroku** | ❌ $5/mo | ClearDB MySQL | ❌ No | ⭐⭐⭐⭐ | Legacy apps |
| **Google Cloud Run** | ✅ 2M req/mo | External DB | ⚠️ Yes | ⭐⭐ | High traffic |
| **Azure** | ✅ Limited | External DB | ⚠️ Yes | ⭐⭐ | Enterprise |

---

## 🏆 My Top Recommendations

### For Beginners (You!)
**🥇 Railway.app**
- Easiest setup
- Built-in MySQL
- Auto-deploy from GitHub
- Free $5/month credit
- No credit card needed initially

### For Production
**🥈 Fly.io**
- Always-on (no cold starts)
- Better performance
- Docker-native
- Free tier sufficient

### For Learning
**🥉 Render.com**
- Free forever
- Great for portfolio
- Auto-deploy
- Accept cold starts

---

## 🔧 Pre-Deployment Checklist

### 1. Update `application.properties` for Production
```properties
# Use environment variables
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

# Production settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
server.error.include-message=never
server.error.include-stacktrace=never
```

### 2. Add Health Check Endpoint
Already exists: `GET /api/quotes/random`

### 3. Update CORS for Production
In `CorsConfig.java`, add your frontend domain:
```java
corsConfig.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",
    "https://your-frontend-domain.com",
    "https://your-production-domain.com"
));
```

### 4. Test Locally First
```bash
# Build jar
mvn clean package -DskipTests

# Run jar
java -jar target/Quotes-0.0.1-SNAPSHOT.jar

# Test endpoints
curl http://localhost:8080/api/quotes/random
```

---

## 🐛 Troubleshooting

### Port Issues
```bash
# Find process on port 8080
ss -ltnp | grep :8080

# Kill process
kill <PID>
```

### Database Connection
```bash
# Test database connection
mysql -h localhost -u root -p OnlineQuotes

# Check Docker logs
docker-compose logs mysql
docker-compose logs quotes-api
```

### Build Failures
```bash
# Clean build
mvn clean install -U

# Skip tests
mvn clean package -DskipTests
```

---

## 📚 Next Steps

1. ✅ Choose a platform (I recommend Railway.app)
2. ✅ Push code to GitHub
3. ✅ Follow platform-specific setup
4. ✅ Test deployed API
5. ✅ Update frontend to use production URL
6. ✅ Add custom domain (optional)

---

## 🆘 Support

- **Railway**: [docs.railway.app](https://docs.railway.app)
- **Render**: [render.com/docs](https://render.com/docs)
- **Fly.io**: [fly.io/docs](https://fly.io/docs)

---

## 📞 Contact
For issues with this app, create an issue on GitHub: 
https://github.com/imani-prog/OnlineQuotesAPIs/issues

---

**Good luck with your deployment! 🚀**
# Multi-stage build for optimized Spring Boot application
FROM maven:3.9.9-amazoncorretto-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Runtime stage - smaller image
FROM amazoncorretto:17-alpine

# Add labels for metadata
LABEL maintainer="timothyimani128@gmail.com"
LABEL description="Quote Generator Spring Boot API"
LABEL version="1.0"

# Create app directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

