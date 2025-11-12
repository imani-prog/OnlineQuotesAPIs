# 🔧 Frontend Configuration Updates

## Required Changes for InspireMeFrontend Repository

### 1. Update API Service Configuration

**File:** `src/services/quoteService.js`

Replace the entire file with:

```javascript
import axios from 'axios'

// Use environment variable with fallback to localhost for development
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

**What Changed:**
- ✅ Uses `import.meta.env.VITE_API_URL` from environment variables
- ✅ Falls back to localhost for local development
- ✅ More flexible for different environments

---

### 2. Create Environment Files

#### `.env.local` (for local development)
```env
VITE_API_URL=http://localhost:8080/api/quotes
```

#### `.env.production` (for production deployment)
```env
VITE_API_URL=https://quotes-backend.onrender.com/api/quotes
```

---

### 3. Update `.gitignore`

Make sure these are in your `.gitignore`:

```gitignore
# Environment files
.env
.env.local
.env.production.local
.env.development.local

# Note: .env.production can be committed as it's public URL
```

---

### 4. Update Dockerfile (if using Docker)

Your existing Dockerfile should handle build-time environment variables:

```dockerfile
# Multi-stage build
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Build with environment variable
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# Production stage
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

### 5. Update docker-compose.yml (for local development)

```yaml
version: '3.8'

services:
  frontend:
    build:
      context: .
      dockerfile: Dockerfile
      args:
        VITE_API_URL: http://localhost:8080/api/quotes
    container_name: inspireme-frontend
    ports:
      - "3000:80"
    environment:
      - NODE_ENV=production
    restart: unless-stopped
```

---

### 6. Vercel Deployment Configuration

#### Update `vercel.json`:

```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "framework": "vite",
  "env": {
    "VITE_API_URL": "https://quotes-backend.onrender.com/api/quotes"
  }
}
```

---

### 7. Testing the Changes

#### Local Testing:
```bash
# 1. Create .env.local file
echo "VITE_API_URL=http://localhost:8080/api/quotes" > .env.local

# 2. Install dependencies
npm install

# 3. Start dev server
npm run dev

# 4. Test in browser
# Open http://localhost:5173
```

#### Production Build Test:
```bash
# 1. Create .env.production
echo "VITE_API_URL=https://quotes-backend.onrender.com/api/quotes" > .env.production

# 2. Build
npm run build

# 3. Preview
npm run preview
```

---

### 8. Deployment Steps

#### Deploy to Vercel:

1. **Via Vercel Dashboard:**
   - Go to https://vercel.com
   - Import `InspireMeFrontend` repository
   - Add environment variable:
     - Key: `VITE_API_URL`
     - Value: `https://quotes-backend.onrender.com/api/quotes`
   - Deploy

2. **Via Vercel CLI:**
```bash
# Install Vercel CLI
npm i -g vercel

# Login
vercel login

# Deploy
vercel

# Add environment variable
vercel env add VITE_API_URL production
# Enter: https://quotes-backend.onrender.com/api/quotes

# Redeploy with new env
vercel --prod
```

---

### 9. Troubleshooting Frontend Issues

#### CORS Errors:
```javascript
// Check if API URL is correct
console.log('API URL:', import.meta.env.VITE_API_URL)

// Test API directly
fetch(import.meta.env.VITE_API_URL + '/random')
  .then(res => res.json())
  .then(data => console.log(data))
  .catch(err => console.error('API Error:', err))
```

#### Environment Variables Not Working:
1. Make sure variable starts with `VITE_`
2. Restart dev server after changing `.env` files
3. Clear build cache: `rm -rf node_modules/.vite`

#### API Connection Issues:
```bash
# Test backend is running
curl https://quotes-backend.onrender.com/actuator/health

# Check CORS
curl -H "Origin: https://inspiremefrontend.vercel.app" \
     -H "Access-Control-Request-Method: GET" \
     -X OPTIONS \
     https://quotes-backend.onrender.com/api/quotes/random -v
```

---

### 10. Git Workflow

```bash
# 1. Update files
# 2. Test locally
npm run dev

# 3. Commit changes
git add .
git commit -m "Configure API URL with environment variables"

# 4. Push to GitHub
git push origin main

# 5. Vercel will auto-deploy (if connected)
```

---

## Summary Checklist

- [ ] Update `src/services/quoteService.js` to use environment variables
- [ ] Create `.env.local` for local development
- [ ] Create `.env.production` for production
- [ ] Update `.gitignore` to exclude sensitive env files
- [ ] Test locally with `npm run dev`
- [ ] Build and test with `npm run build && npm run preview`
- [ ] Deploy to Vercel with proper environment variables
- [ ] Verify production frontend connects to backend
- [ ] Test all API endpoints (fetch, save, delete)

---

## Expected Behavior

### Local Development:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Frontend calls backend at localhost

### Production:
- Frontend: `https://inspiremefrontend.vercel.app`
- Backend: `https://quotes-backend.onrender.com`
- Frontend calls backend at Render URL

---

## Additional Resources

- [Vite Environment Variables](https://vitejs.dev/guide/env-and-mode.html)
- [Vercel Environment Variables](https://vercel.com/docs/concepts/projects/environment-variables)
- [CORS Configuration](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)

---

**Need Help?**
- Check browser console for errors
- Check Network tab in DevTools
- Verify backend is running: https://quotes-backend.onrender.com/actuator/health
- Review backend CORS configuration

---

Last Updated: November 13, 2025

