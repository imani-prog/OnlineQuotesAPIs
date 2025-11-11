@echo off
REM 🚀 Quick Start Script for Quote Generator API with Docker (Windows)

echo 🚀 Starting Quote Generator API...
echo ==================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker is not installed. Please install Docker Desktop first.
    echo    Visit: https://docs.docker.com/desktop/windows/install/
    exit /b 1
)

echo ✅ Docker is installed
echo.

REM Stop any existing containers
echo 🛑 Stopping existing containers (if any)...
docker-compose down 2>nul

echo.
echo 🏗️  Building and starting containers...
echo    This may take a few minutes on first run...
echo.

REM Start services
docker-compose up -d --build

REM Wait for services to be ready
echo.
echo ⏳ Waiting for services to be ready...
timeout /t 10 /nobreak >nul

REM Check if containers are running
docker ps | findstr "quotes-api" >nul
if %errorlevel% equ 0 (
    echo.
    echo ✅ Quote Generator API is running!
    echo ==================================
    echo.
    echo 📍 API Base URL: http://localhost:8080
    echo 📍 Database: MySQL on localhost:3307
    echo.
    echo 🧪 Test the API in your browser or Postman:
    echo    http://localhost:8080/api/quotes/random
    echo    http://localhost:8080/api/quotes
    echo.
    echo 📊 View logs:
    echo    docker-compose logs -f
    echo.
    echo 🛑 Stop the application:
    echo    docker-compose down
    echo.
) else (
    echo ❌ Failed to start containers. Check logs:
    echo    docker-compose logs
    exit /b 1
)

pause

