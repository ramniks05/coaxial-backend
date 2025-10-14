#!/bin/sh
set -e

echo "=========================================="
echo "Railway Deployment - Port Configuration"
echo "=========================================="
echo "PORT variable: ${PORT:-not set}"
echo "Using port: ${PORT:-8080}"
echo "=========================================="

exec java -Dserver.port="${PORT:-8080}" -Dspring.profiles.active=prod -jar app.jar

