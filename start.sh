#!/bin/sh
# Railway startup script with explicit PORT handling

# Log the PORT for debugging
echo "========================================"
echo "Railway PORT environment variable: ${PORT}"
echo "Starting Spring Boot application..."
echo "========================================"

# Start the application with explicit port setting
exec java -Dserver.port="${PORT:-8080}" -Dspring.profiles.active=prod -jar app.jar

