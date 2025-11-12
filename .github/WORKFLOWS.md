# 🔄 GitHub Workflows Documentation

This repository includes comprehensive GitHub Actions workflows for CI/CD, testing, security, and deployment automation.

## 📋 Available Workflows

### 1. CI/CD Pipeline (`ci-cd.yml`)
**Trigger:** Push/PR to `main` or `develop` branches

**Jobs:**
- ✅ **Build and Test** - Compiles code and runs tests with MySQL
- 🐳 **Build Docker** - Creates Docker image and tests it
- 📊 **Code Quality** - Analyzes code quality
- 🔒 **Security Scan** - Scans for vulnerabilities with Trivy

**What it does:**
- Sets up Java 17 and Maven
- Spins up MySQL 8.0 service
- Runs all unit tests
- Builds Docker image
- Tests Docker container
- Uploads test reports

### 2. Deploy to Render (`deploy-render.yml`)
**Trigger:** Push to `main` branch or manual workflow dispatch

**Jobs:**
- 🚀 **Deploy** - Triggers Render deployment
- 🧪 **Health Check** - Verifies deployment
- ✅ **API Tests** - Tests live endpoints

**Setup Required:**
1. Go to Render → Your Service → Settings
2. Copy the "Deploy Hook URL"
3. Add to GitHub Secrets as `RENDER_DEPLOY_HOOK_URL`
4. Optionally add `BACKEND_URL` secret

### 3. Docker Build and Push (`docker-build.yml`)
**Trigger:** Push to `main`, tags, or PR

**Jobs:**
- 🐳 **Build Multi-arch** - Builds for AMD64 and ARM64
- 📤 **Push to Docker Hub** - Pushes to Docker Hub
- 🔍 **Vulnerability Scan** - Scans image for CVEs

**Setup Required:**
1. Create Docker Hub account
2. Generate access token
3. Add secrets:
   - `DOCKER_USERNAME` - Your Docker Hub username
   - `DOCKER_PASSWORD` - Your Docker Hub access token

### 4. Release (`release.yml`)
**Trigger:** Push tags matching `v*.*.*` (e.g., `v1.0.0`)

**Jobs:**
- 📦 **Create Release** - Builds JAR and creates GitHub release
- 📝 **Generate Changelog** - Auto-generates changelog
- 🏷️ **Tag Docker Image** - Tags Docker image with version

**How to use:**
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

### 5. Dependency Review (`dependency-review.yml`)
**Trigger:** Pull requests to `main` or `develop`

**Jobs:**
- 🔍 **Review Dependencies** - Checks for vulnerable dependencies
- 📊 **Maven Updates** - Shows available updates
- 💬 **PR Comments** - Comments on PR with findings

### 6. CodeQL Analysis (`codeql-analysis.yml`)
**Trigger:** Push/PR to `main`, or weekly schedule

**Jobs:**
- 🔐 **Security Analysis** - Scans for security vulnerabilities
- 🐛 **Quality Analysis** - Finds potential bugs
- 📈 **Upload Results** - Sends results to GitHub Security tab

---

## 🔧 Setup Instructions

### Initial Setup

1. **Enable GitHub Actions:**
   - Go to repository Settings → Actions → General
   - Select "Allow all actions and reusable workflows"

2. **Add Required Secrets:**

Go to Settings → Secrets and variables → Actions → New repository secret

**Required for Render Deployment:**
```
RENDER_DEPLOY_HOOK_URL = https://api.render.com/deploy/srv-xxxxx?key=xxxxx
BACKEND_URL = https://your-backend.onrender.com
```

**Required for Docker Hub (Optional):**
```
DOCKER_USERNAME = your-dockerhub-username
DOCKER_PASSWORD = your-dockerhub-access-token
```

3. **Enable Security Features:**
   - Go to Settings → Code security and analysis
   - Enable:
     - Dependency graph
     - Dependabot alerts
     - Dependabot security updates
     - Code scanning (CodeQL)
     - Secret scanning

### Getting Render Deploy Hook

1. Go to your Render dashboard
2. Select your web service
3. Click **Settings** tab
4. Scroll to **Deploy Hook**
5. Click "Create Deploy Hook"
6. Copy the URL
7. Add to GitHub secrets as `RENDER_DEPLOY_HOOK_URL`

### Getting Docker Hub Access Token

1. Go to https://hub.docker.com/settings/security
2. Click "New Access Token"
3. Name: `github-actions`
4. Copy the token (only shown once!)
5. Add to GitHub secrets:
   - `DOCKER_USERNAME`: Your Docker Hub username
   - `DOCKER_PASSWORD`: The access token

---

## 🚀 Usage Examples

### Automatic Deployment

**On every push to main:**
```bash
git add .
git commit -m "Update API endpoint"
git push origin main
```

Triggers:
1. CI/CD Pipeline (builds, tests)
2. Deploy to Render (if secrets configured)
3. Docker Build (if secrets configured)

### Manual Deployment

Go to Actions → Deploy to Render → Run workflow

### Creating a Release

```bash
# Tag your version
git tag -a v1.0.0 -m "First production release"
git push origin v1.0.0
```

This creates:
- GitHub Release with changelog
- JAR artifact
- Docker image tagged as `v1.0.0`

### Testing Pull Requests

When you create a PR:
```bash
git checkout -b feature/new-endpoint
git commit -m "Add new endpoint"
git push origin feature/new-endpoint
```

GitHub will automatically:
- Run tests
- Check code quality
- Review dependencies
- Scan for vulnerabilities
- Comment on PR with results

---

## 📊 Workflow Status Badges

Add these to your README.md:

```markdown
[![CI/CD](https://github.com/imani-prog/OnlineQuotesAPIs/workflows/CI%2FCD%20Pipeline/badge.svg)](https://github.com/imani-prog/OnlineQuotesAPIs/actions)
[![Deploy](https://github.com/imani-prog/OnlineQuotesAPIs/workflows/Deploy%20to%20Render/badge.svg)](https://github.com/imani-prog/OnlineQuotesAPIs/actions)
[![CodeQL](https://github.com/imani-prog/OnlineQuotesAPIs/workflows/CodeQL%20Analysis/badge.svg)](https://github.com/imani-prog/OnlineQuotesAPIs/actions)
```

---

## 🔍 Monitoring Workflows

### View Workflow Runs

1. Go to **Actions** tab in your repository
2. Click on any workflow to see its runs
3. Click on a specific run to see details

### Check Logs

1. Click on a workflow run
2. Click on a job (e.g., "Build and Test")
3. Expand steps to see logs

### View Test Reports

1. Go to completed workflow run
2. Scroll to **Artifacts** section
3. Download `test-results`

### Security Alerts

1. Go to **Security** tab
2. Click **Code scanning alerts**
3. Review and fix issues

---

## 🐛 Troubleshooting

### Workflow fails on "Build with Maven"

**Issue:** Dependencies not downloading

**Fix:**
```yaml
# Check Maven cache in workflow
- name: Cache Maven packages
  uses: actions/cache@v4
```

### Workflow fails on "Run tests"

**Issue:** MySQL not ready

**Fix:** Already handled with health checks:
```yaml
options: >-
  --health-cmd="mysqladmin ping"
  --health-interval=10s
```

### Docker build fails

**Issue:** Multi-stage build error

**Fix:** Ensure Dockerfile is valid:
```bash
docker build -t test .
```

### Render deployment not triggered

**Fix:**
1. Check `RENDER_DEPLOY_HOOK_URL` secret is set
2. Verify the URL is correct
3. Check Render service is active

### CodeQL analysis timeout

**Fix:** This is normal for large projects. CodeQL will complete eventually.

---

## 🎯 Best Practices

### Branch Protection

1. Go to Settings → Branches
2. Add rule for `main` branch:
   - ✅ Require status checks to pass
   - ✅ Require branches to be up to date
   - ✅ Required checks:
     - Build and Test
     - Code Quality Analysis
     - Security Scan

### Commit Messages

Use conventional commits:
```
feat: Add new quote endpoint
fix: Fix CORS configuration
docs: Update README
chore: Update dependencies
test: Add integration tests
```

### Version Tagging

Follow semantic versioning:
- `v1.0.0` - Major release (breaking changes)
- `v1.1.0` - Minor release (new features)
- `v1.1.1` - Patch release (bug fixes)

---

## 📝 Workflow File Locations

```
.github/
└── workflows/
    ├── ci-cd.yml              # Main CI/CD pipeline
    ├── deploy-render.yml      # Render deployment
    ├── docker-build.yml       # Docker image building
    ├── release.yml            # Release automation
    ├── dependency-review.yml  # Dependency checks
    └── codeql-analysis.yml    # Security scanning
```

---

## 🔐 Security Considerations

1. **Never commit secrets** - Always use GitHub Secrets
2. **Use personal access tokens** - Not passwords
3. **Limit token permissions** - Only what's needed
4. **Rotate tokens regularly** - Every 90 days
5. **Review security alerts** - Check weekly

---

## 📈 Metrics and Reports

### Available Reports

1. **Test Coverage** - `target/surefire-reports/`
2. **Security Scan** - Security tab → Code scanning
3. **Dependency Graph** - Insights → Dependency graph
4. **Workflow Runs** - Actions tab

### CI/CD Metrics

Monitor:
- ✅ Build success rate
- ⏱️ Build duration
- 🐛 Test pass rate
- 🔒 Security vulnerabilities found
- 📦 Deployment frequency

---

## 🚀 Advanced Features

### Custom Environment

Create custom environments for staging/production:

1. Settings → Environments → New environment
2. Add protection rules
3. Add environment secrets
4. Reference in workflow:

```yaml
environment:
  name: production
  url: https://quotes-backend.onrender.com
```

### Scheduled Jobs

Add to any workflow:

```yaml
on:
  schedule:
    - cron: '0 0 * * *'  # Daily at midnight
```

### Matrix Builds

Test multiple versions:

```yaml
strategy:
  matrix:
    java: [17, 21]
    os: [ubuntu-latest, windows-latest]
```

---

## 📞 Support

**Issues with workflows?**

1. Check Actions tab for error logs
2. Verify all secrets are set correctly
3. Review workflow syntax with GitHub's validator
4. Check this documentation for setup steps

**Common Issues:**
- Missing secrets → Add in repository settings
- Failed tests → Check MySQL connection
- Docker build fails → Verify Dockerfile locally
- Deployment fails → Check Render dashboard

---

## ✅ Checklist

Before enabling workflows:

- [ ] All secrets added to GitHub
- [ ] Branch protection rules configured
- [ ] Render deploy hook obtained
- [ ] Docker Hub account created (optional)
- [ ] Security features enabled
- [ ] First workflow run successful
- [ ] Status badges added to README

---

**Last Updated:** November 13, 2025  
**Maintained by:** Timothy Imani  
**Repository:** [OnlineQuotesAPIs](https://github.com/imani-prog/OnlineQuotesAPIs)

