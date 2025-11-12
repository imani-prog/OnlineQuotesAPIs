# ✅ Deployment Checklist - InspireMe Quote Generator

## 📋 Pre-Deployment Checklist

### Backend Repository (OnlineQuotesAPIs)

- [x] ✅ Spring Boot 3.5.7 + Java 17
- [x] ✅ MySQL 8.0 configuration
- [x] ✅ All REST endpoints implemented
- [x] ✅ CORS configured for frontend
- [x] ✅ Exception handling implemented
- [x] ✅ Actuator dependency added
- [x] ✅ Health check endpoint working
- [x] ✅ Dockerfile optimized
- [x] ✅ docker-compose.yml configured
- [x] ✅ render.yaml created
- [x] ✅ Production properties configured
- [ ] Commit and push to GitHub
- [ ] Test locally with Docker
- [ ] Deploy to Render.com

### Frontend Repository (InspireMeFrontend)

- [ ] Update `src/services/quoteService.js` to use environment variables
- [ ] Create `.env.local` file with local API URL
- [ ] Create `.env.production` file with production API URL
- [ ] Update `.gitignore` if needed
- [ ] Test locally with backend
- [ ] Build test (`npm run build`)
- [ ] Commit and push to GitHub
- [ ] Deploy to Vercel
- [ ] Test production deployment

---

## 🚀 Step-by-Step Deployment

### Phase 1: Local Testing (15 minutes)

#### 1.1 Start Backend
```bash
cd /home/imanitim/CODE/OnlineQuotes/Quotes
docker compose up -d --build
```

- [ ] Wait for containers to be healthy
- [ ] Check logs: `docker logs quotes-api`
- [ ] Test health: `curl http://localhost:8080/actuator/health`
- [ ] Test API: `curl http://localhost:8080/api/quotes/random`

#### 1.2 Prepare Frontend
In your frontend repository:

```bash
# Create environment file
echo "VITE_API_URL=http://localhost:8080/api/quotes" > .env.local

# Install dependencies
npm install

# Start dev server
npm run dev
```

- [ ] Frontend starts on http://localhost:5173
- [ ] Test "Get Random Quote" button
- [ ] Test "Save Quote" functionality
- [ ] Test "View Saved Quotes"
- [ ] Test "Delete Quote"

#### 1.3 Verification
- [ ] All features work end-to-end
- [ ] No CORS errors in browser console
- [ ] API responses are correct
- [ ] Database persists data

---

### Phase 2: Backend Deployment (20 minutes)

#### 2.1 Prepare Code
```bash
cd /home/imanitim/CODE/OnlineQuotes/Quotes
git status
git add .
git commit -m "Add actuator, deployment configs, and documentation"
git push origin main
```

- [ ] All files committed
- [ ] Pushed to GitHub successfully

#### 2.2 Setup Database

**Option A: PlanetScale (Recommended)**
1. Go to https://planetscale.com
2. Sign up / Login
3. Create new database: `quotes-db`
4. Create password
5. Note connection details:
   - Host
   - Database name
   - Username
   - Password

**Option B: Railway**
1. Go to https://railway.app
2. New Project → MySQL
3. Get connection details

- [ ] Database created
- [ ] Connection string obtained
- [ ] Connection tested (optional)

#### 2.3 Deploy to Render

1. Go to https://render.com
2. Sign up / Login with GitHub
3. Dashboard → New → Web Service
4. Connect repository: `OnlineQuotesAPIs`
5. Configure:
   - **Name:** `quotes-backend` (or your choice)
   - **Environment:** Docker
   - **Branch:** main
   - **Region:** Oregon (US West)
   - **Instance Type:** Free

6. Add Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE = render
   DATABASE_URL = jdbc:mysql://[host]:3306/[dbname]?sslMode=VERIFY_IDENTITY
   DATABASE_USERNAME = [your-username]
   DATABASE_PASSWORD = [your-password]
   PORT = 8080
   ```

7. Advanced Settings:
   - **Health Check Path:** `/actuator/health`

8. Create Web Service

- [ ] Deployment started
- [ ] Build completed successfully
- [ ] Service is live
- [ ] Health check passing
- [ ] Note your backend URL: `https://quotes-backend.onrender.com`

#### 2.4 Test Backend Deployment

```bash
# Replace with your actual URL
BACKEND_URL="https://quotes-backend.onrender.com"

# Test health
curl $BACKEND_URL/actuator/health

# Test random quote
curl $BACKEND_URL/api/quotes/random

# Test save quote
curl -X POST $BACKEND_URL/api/quotes/save \
  -H "Content-Type: application/json" \
  -d '{"text":"Test quote","author":"Test Author"}'

# Test get all quotes
curl $BACKEND_URL/api/quotes
```

- [ ] Health endpoint returns `{"status":"UP"}`
- [ ] Random quote endpoint works
- [ ] Save quote endpoint works
- [ ] Get all quotes endpoint works

---

### Phase 3: Frontend Deployment (15 minutes)

#### 3.1 Update Frontend Code

In your frontend repository:

1. **Update `src/services/quoteService.js`:**
```javascript
import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/quotes'

// ... rest of the code (see FRONTEND_UPDATES.md)
```

2. **Create `.env.production`:**
```bash
echo "VITE_API_URL=https://quotes-backend.onrender.com/api/quotes" > .env.production
```

3. **Test build:**
```bash
npm run build
npm run preview
```

- [ ] Build completes successfully
- [ ] Preview works
- [ ] API calls work in preview

#### 3.2 Commit Changes

```bash
git add .
git commit -m "Configure API URL with environment variables"
git push origin main
```

- [ ] Changes committed
- [ ] Pushed to GitHub

#### 3.3 Deploy to Vercel

1. Go to https://vercel.com
2. Sign up / Login with GitHub
3. New Project
4. Import repository: `InspireMeFrontend`
5. Configure:
   - **Framework Preset:** Vite
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
   - **Install Command:** `npm install`

6. Environment Variables:
   ```
   VITE_API_URL = https://quotes-backend.onrender.com/api/quotes
   ```

7. Deploy

- [ ] Deployment started
- [ ] Build successful
- [ ] Deployment live
- [ ] Note your frontend URL: `https://inspiremefrontend.vercel.app`

#### 3.4 Update CORS (if needed)

If your Vercel URL is different from the ones in CORS config:

1. Edit backend `src/main/java/com/example/quotes/config/CorsConfig.java`
2. Add your exact Vercel URL to `setAllowedOrigins`
3. Commit and push
4. Render will auto-redeploy

- [ ] CORS updated (if needed)
- [ ] Backend redeployed

---

### Phase 4: Production Testing (10 minutes)

#### 4.1 Test Frontend

1. Open your Vercel URL
2. Test all features:

- [ ] Page loads correctly
- [ ] "Get Random Quote" works
- [ ] "Save Quote" works
- [ ] "View Saved Quotes" shows quotes
- [ ] "Delete Quote" works
- [ ] No errors in browser console
- [ ] Network tab shows successful API calls

#### 4.2 Test Backend Directly

```bash
# Test all endpoints
curl https://quotes-backend.onrender.com/actuator/health
curl https://quotes-backend.onrender.com/api/quotes/random
curl https://quotes-backend.onrender.com/api/quotes
```

- [ ] All endpoints respond correctly
- [ ] Response times acceptable
- [ ] No 500 errors

#### 4.3 Test CORS

1. Open browser DevTools (F12)
2. Go to your Vercel URL
3. Check Network tab
4. Click "Get Random Quote"
5. Look for CORS headers in response:
   - `Access-Control-Allow-Origin`
   - `Access-Control-Allow-Methods`

- [ ] CORS headers present
- [ ] No CORS errors
- [ ] API calls successful

---

## 🎉 Post-Deployment

### Documentation

- [ ] Update README.md with live URLs
- [ ] Document environment variables
- [ ] Note any production issues

### URLs to Save

```
Backend API: https://quotes-backend.onrender.com
Frontend: https://inspiremefrontend.vercel.app
Health Check: https://quotes-backend.onrender.com/actuator/health
```

### Share Your Project

- [ ] Add URLs to GitHub repo descriptions
- [ ] Update portfolio
- [ ] Share on LinkedIn
- [ ] Test on mobile devices

---

## 🐛 Troubleshooting Guide

### Backend Issues

**Build fails:**
- Check Render logs
- Verify pom.xml is correct
- Ensure Dockerfile is valid

**Health check failing:**
- Check `/actuator/health` endpoint
- Verify actuator dependency in pom.xml
- Check database connection

**Database connection errors:**
- Verify DATABASE_URL format
- Check username/password
- Ensure SSL settings match database provider

### Frontend Issues

**CORS errors:**
- Add exact Vercel URL to backend CORS config
- Check browser console for exact origin
- Redeploy backend after CORS update

**API not found (404):**
- Verify VITE_API_URL is correct
- Check environment variable in Vercel
- Ensure no trailing slash in URL

**Build fails:**
- Check build logs in Vercel
- Verify all dependencies in package.json
- Test build locally first

---

## 📊 Success Criteria

- [x] Backend runs locally in Docker
- [ ] Backend deployed to Render
- [ ] Backend health check passing
- [ ] Database connected and working
- [ ] All API endpoints responding
- [ ] Frontend runs locally
- [ ] Frontend deployed to Vercel
- [ ] Frontend connects to backend
- [ ] All features work end-to-end
- [ ] No CORS errors
- [ ] No console errors
- [ ] Production URLs documented

---

## 🎯 Next Steps After Deployment

1. **Monitor Performance:**
   - Check Render metrics
   - Monitor Vercel analytics
   - Track error rates

2. **Enhancements:**
   - Add authentication
   - Implement caching
   - Add rate limiting
   - Improve error handling

3. **Documentation:**
   - Create API documentation (Swagger/OpenAPI)
   - Write user guide
   - Add screenshots to README

---

## 📞 Support Resources

- **Render Docs:** https://render.com/docs
- **Vercel Docs:** https://vercel.com/docs
- **PlanetScale Docs:** https://planetscale.com/docs
- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **Vite Docs:** https://vitejs.dev

---

**Project:** InspireMe Quote Generator  
**Last Updated:** November 13, 2025  
**Status:** Ready for Deployment ✅

