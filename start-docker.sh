#!/bin/bash

# 🚀 Quick Start Script for Quote Generator API with Docker
# This script helps you quickly start the application locally using Docker Compose

echo "🚀 Starting Quote Generator API..."
echo "=================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    echo "   Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    echo "   Visit: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "✅ Docker and Docker Compose are installed"
echo ""

# Stop any existing containers
echo "🛑 Stopping existing containers (if any)..."
docker-compose down 2>/dev/null

echo ""
echo "🏗️  Building and starting containers..."
echo "   This may take a few minutes on first run..."
echo ""

# Start services
docker-compose up -d --build

# Wait for services to be healthy
echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check if containers are running
if docker ps | grep -q "quotes-api"; then
    echo ""
    echo "✅ Quote Generator API is running!"
    echo "=================================="
    echo ""
    echo "📍 API Base URL: http://localhost:8080"
    echo "📍 Database: MySQL on localhost:3307"
    echo ""
    echo "🧪 Test the API:"
    echo "   Get random quote:  curl http://localhost:8080/api/quotes/random"
    echo "   Get all quotes:    curl http://localhost:8080/api/quotes"
    echo ""
    echo "📊 View logs:"
    echo "   docker-compose logs -f"
    echo ""
    echo "🛑 Stop the application:"
    echo "   docker-compose down"
    echo ""
    echo "🗑️  Stop and remove data:"
    echo "   docker-compose down -v"
    echo ""
else
    echo "❌ Failed to start containers. Check logs:"
    echo "   docker-compose logs"
    exit 1
fi

