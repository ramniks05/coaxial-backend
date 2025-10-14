# Multi-stage build for production
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-alpine

# Install necessary packages
RUN apk add --no-cache curl

# Create app user (Alpine uses addgroup and adduser)
RUN addgroup -S appuser && adduser -S appuser -G appuser

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/coaxial-*.jar app.jar

# Change ownership to app user
RUN chown -R appuser:appuser /app

# Expose port (Railway uses dynamic PORT)
EXPOSE 8080

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Switch to app user
USER appuser

# Health check - REMOVED TEMPORARILY
# HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
#     CMD curl -f http://localhost:8080/health || exit 1

# Run the application with dynamic port from Railway
# PORT environment variable is read by Spring Boot via application-prod.properties
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
