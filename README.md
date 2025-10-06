# Coaxial Learning Management System

A Spring Boot-based online learning management system designed as a backend service for React applications.

## Features

- **Multi-Environment Support**: Development and Production configurations
- **API Documentation**: Swagger/OpenAPI 3 integration
- **CORS Configuration**: Ready for React frontend integration
- **Docker Support**: Containerized deployment
- **Security**: Spring Security with role-based access
- **Database**: PostgreSQL with JPA/Hibernate
- **Health Monitoring**: Actuator endpoints for monitoring

## Prerequisites

### Local Development
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Redis 6.0 or higher (optional, for enhanced rate limiting)

### Docker Deployment
- Docker 20.10 or higher
- Docker Compose 2.0 or higher

## Quick Start

### Option 1: Docker Deployment (Recommended)

1. Clone the repository
2. Run the deployment script:
   ```bash
   # For production
   ./deploy.sh prod
   
   # For development
   ./deploy.sh dev
   ```

### Option 2: Local Development

1. **Database Setup**:
   ```sql
   CREATE DATABASE newcoaxial;
   ```

2. **Redis Setup (Optional)**:
   ```bash
   # Install Redis (Ubuntu/Debian)
   sudo apt-get install redis-server
   
   # Start Redis
   sudo systemctl start redis-server
   
   # Verify Redis is running
   redis-cli ping
   ```

3. **Run the application**:
   ```bash
   # Development mode (default)
   mvn spring-boot:run
   
   # Production mode
   mvn spring-boot:run -Dspring.profiles.active=prod
   ```

## Environment Configuration

### Development Environment
- **Profile**: `dev`
- **Database**: Local PostgreSQL
- **Swagger**: Enabled at `/swagger-ui.html`
- **CORS**: Allows `http://localhost:3000`
- **Logging**: DEBUG level

### Production Environment
- **Profile**: `prod`
- **Database**: Environment variables
- **Swagger**: Disabled for security
- **CORS**: Configurable origins
- **Logging**: INFO level with file output

## Environment Variables (Production)

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/newcoaxial
DB_USERNAME=postgres
DB_PASSWORD=postgres

# CORS
CORS_ALLOWED_ORIGINS=https://yourdomain.com

# JWT (for future authentication)
JWT_SECRET=your-super-secret-jwt-key
JWT_EXPIRATION=86400000

# Logging
LOG_FILE_PATH=/var/log/coaxial/application.log

# Server
PORT=8080
```

## API Documentation

### Swagger UI (Development Only)
- **URL**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`

### Health Check Endpoints
- `GET /` - Welcome message
- `GET /health` - Health check
- `GET /actuator/health` - Detailed health information

### User Management API
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/role/{role}` - Get users by role (ADMIN, INSTRUCTOR, STUDENT)
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Monitoring Endpoints (Production)
- `GET /actuator/health` - Application health
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

## Project Structure

```
src/
├── main/
│   ├── java/com/coaxial/
│   │   ├── CoaxialApplication.java
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   ├── DatabaseConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   └── UserController.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── UserRole.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   └── SecurityConfig.java
│   │   └── service/
│   │       └── UserService.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
├── Dockerfile
├── docker-compose.yml
├── deploy.sh
└── README.md
```

## Docker Commands

```bash
# Build and run with Docker Compose
docker-compose up --build

# Run in background
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Remove volumes (careful - this deletes data)
docker-compose down -v
```

## React Frontend Integration

The backend is configured to work with React applications:

1. **CORS**: Configured to allow requests from `http://localhost:3000` (development)
2. **API Base URL**: `http://localhost:8080/api`
3. **Authentication**: Basic authentication (can be extended to JWT)
4. **Content-Type**: JSON responses

### Example React API Call

```javascript
// Fetch all users
const response = await fetch('http://localhost:8080/api/users', {
  method: 'GET',
  headers: {
    'Authorization': 'Basic ' + btoa('username:password'),
    'Content-Type': 'application/json'
  }
});
const users = await response.json();
```

## Deployment

### Production Deployment

1. **Set environment variables**:
   ```bash
   export DB_URL=jdbc:postgresql://your-db-host:5432/newcoaxial
   export DB_USERNAME=your-username
   export DB_PASSWORD=your-password
   export CORS_ALLOWED_ORIGINS=https://yourdomain.com
   ```

2. **Deploy with Docker**:
   ```bash
   ./deploy.sh prod
   ```

### Server Requirements

- **CPU**: 1 core minimum, 2 cores recommended
- **RAM**: 1GB minimum, 2GB recommended
- **Storage**: 10GB minimum
- **OS**: Linux (Ubuntu 20.04+ recommended)

## Next Steps

This foundation provides:
- ✅ Multi-environment configuration
- ✅ API documentation with Swagger
- ✅ CORS configuration for React
- ✅ Docker containerization
- ✅ Production-ready security
- ✅ Health monitoring

Additional features to implement:
- Course management system
- Enrollment system
- Assignment and quiz functionality
- File upload/download
- Email notifications
- Real-time notifications (WebSocket)
