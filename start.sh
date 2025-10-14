#!/bin/sh
# Railway startup script with explicit PORT handling

# Log environment for debugging
echo "========================================"
echo "Environment Variables Check:"
echo "PORT=${PORT}"
echo "RAILWAY_ENVIRONMENT=${RAILWAY_ENVIRONMENT}"
printenv | grep -i port || echo "No PORT-related variables found"
echo "========================================"

# Determine the port to use
if [ -z "$PORT" ]; then
    echo "WARNING: PORT not set by Railway, using default 8080"
    JAVA_PORT=8080
else
    echo "Using Railway PORT: $PORT"
    JAVA_PORT=$PORT
fi

echo "Starting Spring Boot on port: $JAVA_PORT"
echo "========================================"

# Start the application with explicit port setting
exec java -Dserver.port="$JAVA_PORT" -Dspring.profiles.active=prod -jar app.jar

