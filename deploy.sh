#!/bin/bash

# Coaxial LMS Deployment Script

set -e

echo "🚀 Starting Coaxial LMS Deployment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose and try again."
    exit 1
fi

# Set environment
ENVIRONMENT=${1:-prod}

echo "📦 Building application for $ENVIRONMENT environment..."

# Build the application
if [ "$ENVIRONMENT" = "dev" ]; then
    echo "🔧 Building for development..."
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d
else
    echo "🏭 Building for production..."
    docker-compose up --build -d
fi

echo "⏳ Waiting for services to start..."
sleep 30

# Check if services are healthy
echo "🔍 Checking service health..."

# Check PostgreSQL
if docker-compose exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "✅ PostgreSQL is healthy"
else
    echo "❌ PostgreSQL is not responding"
    exit 1
fi

# Check Spring Boot application
if curl -f http://localhost:8080/health > /dev/null 2>&1; then
    echo "✅ Spring Boot application is healthy"
else
    echo "❌ Spring Boot application is not responding"
    exit 1
fi

echo "🎉 Deployment completed successfully!"
echo ""
echo "📋 Service URLs:"
echo "   - Application: http://localhost:8080"
echo "   - Health Check: http://localhost:8080/health"
if [ "$ENVIRONMENT" = "dev" ]; then
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - API Docs: http://localhost:8080/api-docs"
fi
echo "   - PostgreSQL: localhost:5432"
echo ""
echo "📊 To view logs:"
echo "   docker-compose logs -f app"
echo ""
echo "🛑 To stop services:"
echo "   docker-compose down"
