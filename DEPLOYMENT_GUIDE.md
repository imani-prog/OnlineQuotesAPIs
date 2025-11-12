# 🚀 Full Stack Deployment Guide: InspireMe Quote Generator

## 📋 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Local Development with Docker](#local-development-with-docker)
3. [Backend Deployment on Render](#backend-deployment-on-render)
4. [Frontend Deployment on Vercel](#frontend-deployment-on-vercel)
5. [Environment Configuration](#environment-configuration)
6. [API Endpoints](#api-endpoints)
7. [Troubleshooting](#troubleshooting)

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      PRODUCTION SETUP                        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┐         ┌──────────────┐                 │
│  │   Vercel     │  HTTPS  │   Render     │                 │
│  │  (Frontend)  │────────▶│  (Backend)   │                 │
│  │  React/Vite  │         │  Spring Boot │                 │
│  └──────────────┘         └───────┬──────┘                 │
│                                    │                         │
│                                    │ JDBC                    │
│                                    │                         │
│                            ┌───────▼──────┐                 │
│                            │   Render     │                 │
│                            │   MySQL DB   │                 │
│                            └──────────────┘                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   LOCAL DEVELOPMENT SETUP                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┐         ┌──────────────┐                 │
│  │   Browser    │  :5173  │    Docker    │                 │
│  │ localhost    │────────▶│  Container   │                 │
│  │              │         │  (Frontend)  │                 │
│  └──────────────┘         └──────────────┘                 │
│         │                                                    │
│         │ :8080                                             │
│         │                                                    │
│  ┌──────▼──────────────────────────────────┐               │
│  │        Docker Container Network          │               │
│  │  ┌──────────────┐    ┌──────────────┐  │               │
│  │  │   Backend    │    │    MySQL     │  │               │
│  │  │ Spring Boot  │───▶│   Database   │  │               │
│  │  │    :8080     │    │    :3306     │  │               │
│  │  └──────────────┘    └──────────────┘  │               │
│  └─────────────────────────────────────────┘               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🐳 Local Development with Docker

### Prerequisites
- Docker Desktop installed and running
- Docker Compose v3.8+
- Git

### Quick Start

#### Option 1: Backend Only (with local MySQL)
```bash
cd /home/imanitim/CODE/OnlineQuotes/Quotes
docker-compose up -d
```

This will start:
- MySQL on `localhost:3307`
- Spring Boot API on `localhost:8080`

#### Option 2: Full Stack (Backend + Frontend)
```bash
cd /home/imanitim/CODE/OnlineQuotes/Quotes
docker-compose -f docker-compose.fullstack.yml --profile with-frontend up -d
```

This will start:
- MySQL on `localhost:3307`
- Spring Boot API on `localhost:8080`
- React Frontend on `localhost:5173`

### Verify Services

```bash
# Check running containers
docker ps

# Check backend health
curl http://localhost:8080/actuator/health

# Check API endpoints
curl http://localhost:8080/api/quotes/random

# View logs
docker logs quotes-backend -f
docker logs quotes-mysql -f
```

### Stop Services

```bash
# Stop all containers
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

---

## ☁️ Backend Deployment on Render

### Step 1: Prepare Your Repository

1. **Ensure all files are committed:**
```bash
cd /home/imanitim/CODE/OnlineQuotes/Quotes
git add .
git commit -m "Add Render deployment configuration"
git push origin main
```

### Step 2: Create Render Account & Services

1. Go to [Render.com](https://render.com) and sign up/login
2. Connect your GitHub account

### Step 3: Create MySQL Database

1. Click **"New +"** → **"PostgreSQL"** or **"MySQL"** (if available)
   
   > **Note:** Render's free tier primarily offers PostgreSQL. If you need MySQL, you may need to:
   > - Use Render's paid plan
   > - Or use external MySQL services like:
   >   - [PlanetScale](https://planetscale.com) (Free tier available)
   >   - [Railway](https://railway.app) (Free tier with MySQL)
   >   - [Aiven](https://aiven.io) (Free tier)

2. For this guide, let's use **PlanetScale** for MySQL:

#### Using PlanetScale (Recommended for Free MySQL)

1. Go to [PlanetScale](https://planetscale.com)
2. Create account and new database: `quotes-db`
3. Create a password and note the connection string
4. Format: `mysql://username:password@host:port/database?sslaccept=strict`

### Step 4: Deploy Backend on Render

1. In Render Dashboard, click **"New +"** → **"Web Service"**
2. Connect your GitHub repository: `OnlineQuotesAPIs`
3. Configure:
   - **Name:** `quotes-backend`
   - **Environment:** `Docker`
   - **Branch:** `main`
   - **Region:** `Oregon (US West)`
   - **Instance Type:** `Free`

4. **Environment Variables:**
   ```
   SPRING_PROFILES_ACTIVE=render
   DATABASE_URL=jdbc:mysql://[your-planetscale-host]:3306/quotes-db?sslMode=VERIFY_IDENTITY
   DATABASE_USERNAME=[your-planetscale-username]
   DATABASE_PASSWORD=[your-planetscale-password]
   PORT=8080
   ```

5. **Health Check Path:** `/actuator/health`

6. Click **"Create Web Service"**

7. Wait for deployment (5-10 minutes)

8. Your backend URL: `https://quotes-backend.onrender.com`

### Step 5: Test Backend Deployment

```bash
# Test health endpoint
curl https://quotes-backend.onrender.com/actuator/health

# Test API
curl https://quotes-backend.onrender.com/api/quotes/random
```

---

## 🎨 Frontend Deployment on Vercel

### Step 1: Update Frontend API Configuration

1. **Update your frontend repository** (`InspireMeFrontend`)

2. **Update `src/services/quoteService.js`:**
```javascript
import axios from 'axios'

// Use environment variable or fallback to localhost
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/quotes'

export async function getRandomQuote() {
  const res = await axios.get(`${BASE_URL}/random`)
  return res.data
}

export async function saveQuote(quote) {
  const res = await axios.post(`${BASE_URL}/save`, quote)
  return res.data
}

export async function getSavedQuotes() {
  const res = await axios.get(BASE_URL)
  return res.data
}

export async function deleteQuote(id) {
  const res = await axios.delete(`${BASE_URL}/${id}`)
  return res.data
}

export async function fetchAndSaveRandomQuote() {
  const res = await axios.post(`${BASE_URL}/random/save`)
  return res.data
}

export default {
  getRandomQuote,
  saveQuote,
  getSavedQuotes,
  deleteQuote,
  fetchAndSaveRandomQuote,
}
```

3. **Create `.env.production` file in frontend repo:**
```env
VITE_API_URL=https://quotes-backend.onrender.com/api/quotes
```

4. **Commit and push:**
```bash
cd /path/to/InspireMeFrontend
git add .
git commit -m "Update API URL for production"
git push origin main
```

### Step 2: Deploy to Vercel

1. Go to [Vercel](https://vercel.com)
2. Sign up/Login with GitHub
3. Click **"Add New Project"**
4. Import `InspireMeFrontend` repository
5. Configure:
   - **Framework Preset:** Vite
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
   - **Install Command:** `npm install`

6. **Environment Variables:**
   ```
   VITE_API_URL=https://quotes-backend.onrender.com/api/quotes
   ```

7. Click **"Deploy"**

8. Your frontend URL: `https://inspiremefrontend.vercel.app`

---

## 🔧 Environment Configuration

### Backend Environment Variables

| Variable | Local (Docker) | Production (Render) |
|----------|---------------|---------------------|
| `SPRING_PROFILES_ACTIVE` | `default` | `render` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql:3306/OnlineQuotes` | `jdbc:mysql://[host]:3306/quotes-db` |
| `SPRING_DATASOURCE_USERNAME` | `quoteuser` | `[your-db-user]` |
| `SPRING_DATASOURCE_PASSWORD` | `quotepass123` | `[your-db-password]` |
| `PORT` | `8080` | `8080` |

### Frontend Environment Variables

| Variable | Local | Production |
|----------|-------|-----------|
| `VITE_API_URL` | `http://localhost:8080/api/quotes` | `https://quotes-backend.onrender.com/api/quotes` |

---

## 📡 API Endpoints

### Base URL
- **Local:** `http://localhost:8080/api/quotes`
- **Production:** `https://quotes-backend.onrender.com/api/quotes`

### Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/random` | Fetch random quote from ZenQuotes API | - | `Quote` object |
| GET | `/` | Get all saved quotes | - | `{ count, quotes, message }` |
| GET | `/{id}` | Get quote by ID | - | `Quote` object |
| POST | `/` | Save new quote | `{ text, author }` | `Quote` object |
| POST | `/save` | Save new quote (alt) | `{ text, author }` | `{ message, quote }` |
| POST | `/random/save` | Fetch random & save | - | `{ message, quote }` |
| DELETE | `/{id}` | Delete quote by ID | - | `{ message, deletedId }` |

### Example Requests

```bash
# Get random quote
curl -X GET http://localhost:8080/api/quotes/random

# Get all quotes
curl -X GET http://localhost:8080/api/quotes

# Save a quote
curl -X POST http://localhost:8080/api/quotes/save \
  -H "Content-Type: application/json" \
  -d '{"text":"Your time is limited","author":"Steve Jobs"}'

# Fetch random and save
curl -X POST http://localhost:8080/api/quotes/random/save

# Delete quote
curl -X DELETE http://localhost:8080/api/quotes/1
```

---

## 🐛 Troubleshooting

### Backend Issues

#### 1. Container is Unhealthy
```bash
# Check logs
docker logs quotes-backend

# Check health endpoint
curl http://localhost:8080/actuator/health

# Rebuild container
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

#### 2. Database Connection Failed
```bash
# Check MySQL container
docker logs quotes-mysql

# Test MySQL connection
docker exec -it quotes-mysql mysql -u quoteuser -pquotepass123 -e "SHOW DATABASES;"

# Verify environment variables
docker exec quotes-backend env | grep SPRING_DATASOURCE
```

#### 3. Port 8080 Already in Use
```bash
# Find process using port 8080
sudo lsof -i :8080

# Kill the process
kill -9 [PID]

# Or change port in docker-compose.yml
ports:
  - "8081:8080"
```

### Frontend Issues

#### 1. CORS Errors
- **Solution:** Make sure your frontend URL is added to backend CORS configuration
- Check `src/main/java/com/example/quotes/config/CorsConfig.java`

#### 2. API Connection Failed
```bash
# Test backend directly
curl https://quotes-backend.onrender.com/actuator/health

# Check frontend environment variables
console.log(import.meta.env.VITE_API_URL)
```

#### 3. Frontend Not Connecting to Local Backend
- Make sure `VITE_API_URL=http://localhost:8080/api/quotes` in `.env.local`
- Restart Vite dev server: `npm run dev`

### Render Deployment Issues

#### 1. Build Fails
- Check Render logs in dashboard
- Ensure Dockerfile is correct
- Verify `render.yaml` configuration

#### 2. Database Connection Issues
- Verify DATABASE_URL format
- Check database is running (PlanetScale dashboard)
- Ensure SSL settings match

#### 3. Health Check Failing
- Verify `/actuator/health` endpoint works locally
- Check if actuator dependency is in `pom.xml`
- View Render logs for errors

---

## 🔄 CI/CD Workflow (Optional)

### GitHub Actions for Automated Deployment

Create `.github/workflows/deploy.yml` in your backend repo:

```yaml
name: Deploy to Render

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Trigger Render Deploy
        run: |
          curl -X POST ${{ secrets.RENDER_DEPLOY_HOOK }}
```

---

## 📝 Additional Notes

### Cost Breakdown
- **Render Free Tier:** Backend API (sleeps after 15 min inactivity)
- **PlanetScale Free Tier:** MySQL Database (5GB storage)
- **Vercel Free Tier:** Frontend hosting (100GB bandwidth)

### Performance Tips
1. **Render:** Service sleeps after inactivity - first request may be slow
2. **Use connection pooling:** HikariCP is configured in application.properties
3. **Enable caching:** Consider adding Spring Cache for frequently accessed data

### Security Best Practices
1. Never commit `.env` files with credentials
2. Use environment variables for all sensitive data
3. Enable HTTPS in production (Render and Vercel do this automatically)
4. Implement rate limiting if needed

---

## 🎯 Quick Reference

### Local Development
```bash
# Start backend only
docker-compose up -d

# Start full stack
docker-compose -f docker-compose.fullstack.yml --profile with-frontend up -d

# Stop
docker-compose down
```

### URLs
- **Local Frontend:** http://localhost:5173
- **Local Backend:** http://localhost:8080
- **Local MySQL:** localhost:3307
- **Production Frontend:** https://inspiremefrontend.vercel.app
- **Production Backend:** https://quotes-backend.onrender.com

### Important Files
- Backend Config: `src/main/resources/application.properties`
- Docker: `docker-compose.yml`, `Dockerfile`
- Render: `render.yaml`
- Frontend API: `src/services/quoteService.js`

---

## 📞 Support

If you encounter issues:
1. Check logs: `docker logs [container-name]`
2. Verify health: `curl http://localhost:8080/actuator/health`
3. Review this guide's troubleshooting section
4. Check Render/Vercel deployment logs

---

**Last Updated:** November 13, 2025  
**Version:** 1.0.0

