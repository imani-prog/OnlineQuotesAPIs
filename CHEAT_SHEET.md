# 🚀 DEPLOYMENT CHEAT SHEET
Quick reference for deploying Quote Generator API

---

## 🎯 PICK YOUR PLATFORM (30 seconds)

### Railway.app ⭐ **RECOMMENDED**
```
✅ Free: $5/mo credit
✅ Time: 10 minutes
✅ DB: MySQL included
→ Go to: railway.app
```

### Render.com
```
✅ Free: 750 hrs/mo  
⚠️ Cold start: 30s
✅ DB: PostgreSQL
→ Go to: render.com
```

### Fly.io
```
✅ Free: 3 VMs
✅ No cold start
⚠️ DB: External
→ Go to: fly.io
```

---

## 🐳 TEST LOCALLY (2 commands)

```bash
chmod +x start-docker.sh
./start-docker.sh
```

**Test:**
```bash
curl http://localhost:8080/api/quotes/random
```

**Stop:**
```bash
docker-compose down
```

---

## 🚂 DEPLOY TO RAILWAY (5 steps)

```
1. railway.app → Login with GitHub
2. New Project → Deploy from GitHub
3. Select repo: OnlineQuotesAPIs
4. Add → Database → MySQL
5. Done! Get URL
```

---

## 🎨 DEPLOY TO RENDER (6 steps)

```
1. render.com → New → Web Service
2. Connect GitHub repo
3. Build: mvn clean package -DskipTests
4. Start: java -jar target/Quotes-0.0.1-SNAPSHOT.jar
5. New → PostgreSQL
6. Add env: DATABASE_URL=<url>
```

---

## ✈️ DEPLOY TO FLY.IO (3 commands)

```bash
curl -L https://fly.io/install.sh | sh
flyctl auth login
flyctl launch
```

---

## 📖 DOCUMENTATION

- **Quick start**: `QUICK_DEPLOY.md` (5 min)
- **Full guide**: `DEPLOYMENT_GUIDE.md` (30 min)
- **Databases**: `FREE_DATABASE_OPTIONS.md`
- **Navigation**: `INDEX.md`

---

## 🔧 DOCKER COMMANDS

```bash
# Start
docker-compose up -d

# Logs
docker-compose logs -f

# Stop
docker-compose down

# Fresh start
docker-compose down -v
```

---

## 🐛 TROUBLESHOOTING

### Port in use
```bash
ss -ltnp | grep :8080
kill <PID>
```

### Build failed
```bash
mvn clean package -DskipTests
```

### Database error
```bash
docker-compose logs mysql
docker-compose restart
```

---

## ✅ PRE-DEPLOYMENT

- [ ] Test local: `./start-docker.sh`
- [ ] Push to GitHub
- [ ] Read: `QUICK_DEPLOY.md`
- [ ] Pick platform
- [ ] Deploy!

---

## 📊 COMPARISON

| Platform | Time | Free | DB | Cold Start |
|----------|------|------|-----|------------|
| Railway | 10m | ✅ | MySQL | ❌ |
| Render | 15m | ✅ | PostgreSQL | ⚠️ |
| Fly.io | 20m | ✅ | External | ❌ |

---

## 🎯 MY RECOMMENDATION

**Use Railway.app**
- Easiest (10 min)
- MySQL included
- Auto-deploy
- Free tier
- Beginner-friendly

---

## 📞 HELP

- Docs: `INDEX.md`
- Railway: docs.railway.app
- Render: render.com/docs
- Fly: fly.io/docs

---

**NEXT STEP:**
```bash
./start-docker.sh
```

Then deploy to Railway! 🚀

