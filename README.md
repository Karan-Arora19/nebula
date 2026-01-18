# Nebula - Vehicle Buying & Management Platform

A production-ready microservices-based platform for vehicle buying, customization, and management. Built with Spring Boot, Next.js, and modern cloud-native technologies.

## 🏗️ Architecture

Nebula follows a microservices architecture pattern with the following components:

### Backend Services (Spring Boot)

- **Gateway Service** (Port 8080) - API Gateway with routing, CORS, and JWT authentication
- **Vehicle Service** (Port 8081) - Vehicle inventory and car configurator with customization options
- **World View Service** (Port 8082) - Real-time journey simulation and route management
- **User Service** (Port 8083) - User authentication, authorization, and profile management
- **Merchandise Service** (Port 8084) - Product catalog and shopping cart management
- **Platform Core** (Port 8761) - Config Server and Eureka Service Discovery

### Frontend (Next.js)

- **Web Application** (Port 3000) - React-based frontend with 3D car visualization

### Infrastructure

- **PostgreSQL** - Shared database with schema-per-service isolation
- **RabbitMQ** - Message broker with MQTT plugin for real-time updates
- **SonarQube** (Port 9000) - Code quality analysis (optional)

## 📋 Prerequisites

- **Java 17+** - Required for all backend services
- **Maven 3.8+** - Build tool for Java services
- **Node.js 18+** - Required for frontend
- **npm/yarn/pnpm** - Package manager for frontend
- **Docker & Docker Compose** - For infrastructure services (PostgreSQL, RabbitMQ)
- **PostgreSQL 15+** - Database (can run via Docker)
- **RabbitMQ 4.0+** - Message broker with MQTT plugin (can run via Docker)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd nebula
```

### 2. Configure Environment Variables

Create a `.env` file in the `docker/` directory:

```bash
cd docker
cp .env.example .env
# Edit .env with your credentials
```

Required environment variables:
- `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`
- `RABBITMQ_USER`, `RABBITMQ_PASSWORD`, `RABBITMQ_VHOST`
- `RABBITMQ_AMQP_PORT`, `RABBITMQ_MQTT_PORT`, `RABBITMQ_MANAGEMENT_PORT`, `RABBITMQ_WEB_MQTT_PORT`

### 3. Start Infrastructure Services

```bash
cd docker
docker-compose up -d postgres rabbitmq
```

This will:
- Start PostgreSQL with automatic schema initialization
- Start RabbitMQ with MQTT plugin enabled
- Initialize all database schemas and tables

### 4. Start Backend Services

Start services in the following order:

```bash
# 1. Platform Core (Config Server & Eureka)
cd services/platform-core
./mvnw spring-boot:run

# 2. Gateway Service
cd ../gateway-service
./mvnw spring-boot:run

# 3. User Service
cd ../user-service
./mvnw spring-boot:run

# 4. Vehicle Service
cd ../vehicle-service
./mvnw spring-boot:run

# 5. World View Service
cd ../world-view
./mvnw spring-boot:run

# 6. Merchandise Service
cd ../merchandise-service
./mvnw spring-boot:run
```

**Note:** Services can be started in parallel after Platform Core is running. They will register with Eureka automatically.

### 5. Start Frontend

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Copy environment file
cp .env.example .env.local
# Edit .env.local with your service URLs and MQTT credentials

# Start development server
npm run dev
```

The frontend will be available at `http://localhost:3000`

## 🔧 Configuration

### Service Configuration

Each service has its own configuration files:
- `application.yaml` - Base configuration
- `application-dev.yaml` - Development profile (gitignored, contains credentials)

### Database Configuration

Database schemas are automatically initialized via SQL scripts in `docker/init-scripts/`:
1. `01-init-schemas.sql` - Creates all service schemas
2. `02-vehicle-service-schema.sql` - Vehicle service tables
3. `03-user-service-schema.sql` - User service tables
4. `04-merchandise-service-schema.sql` - Merchandise service tables
5. `05-world-view-service-schema.sql` - World view service tables
6. `06-grant-access.sql` - Grants permissions

### Environment Variables

#### Backend Services

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Service port | Service-specific |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `POSTGRES_HOST` | PostgreSQL host | `localhost` |
| `POSTGRES_PORT` | PostgreSQL port | `5432` |
| `POSTGRES_DB` | Database name | `nebula_db` |
| `POSTGRES_USER` | Database user | `nebula_user` |
| `POSTGRES_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing secret (min 32 chars) | - |
| `ADMIN_DEFAULT_PASSWORD` | Default admin password | - |

#### Frontend

| Variable | Description | Default |
|----------|-------------|---------|
| `NEXT_PUBLIC_API_URL` | World-view service URL | `http://localhost:8082` |
| `NEXT_PUBLIC_MQTT_URL` | MQTT WebSocket URL | `ws://localhost:15675/ws` |
| `NEXT_PUBLIC_MQTT_USERNAME` | MQTT username | - |
| `NEXT_PUBLIC_MQTT_PASSWORD` | MQTT password | - |
| `NEXT_PUBLIC_VEHICLE_SERVICE_URL` | Vehicle service URL | `http://localhost:8081` |

## 🏭 Production Deployment

### Security Checklist

- [ ] Change all default passwords
- [ ] Set strong `JWT_SECRET` (minimum 32 characters)
- [ ] Configure `ADMIN_DEFAULT_PASSWORD` for admin user
- [ ] Use environment variables for all sensitive data
- [ ] Enable HTTPS/TLS for all services
- [ ] Configure CORS with specific origins (not `*`)
- [ ] Set up proper firewall rules
- [ ] Enable database connection encryption
- [ ] Configure proper logging levels (INFO for production)
- [ ] Set up monitoring and alerting

### Database Setup

1. **Initialize Database:**
   ```bash
   # Run init scripts in order
   psql -U $POSTGRES_USER -d $POSTGRES_DB -f docker/init-scripts/01-init-schemas.sql
   psql -U $POSTGRES_USER -d $POSTGRES_DB -f docker/init-scripts/02-vehicle-service-schema.sql
   psql -U $POSTGRES_USER -d $POSTGRES_DB -f docker/init-scripts/03-user-service-schema.sql
   psql -U $POSTGRES_USER -d $POSTGRES_DB -f docker/init-scripts/04-merchandise-service-schema.sql
   psql -U $POSTGRES_USER -d $POSTGRES_DB -f docker/init-scripts/05-world-view-service-schema.sql
   psql -U $POSTGRES_USER -d $POSTGRES_DB -f docker/init-scripts/06-grant-access.sql
   ```

2. **Seed Data:**
   - Vehicle service seeds data automatically via `DataSeeder` on first startup
   - World view service requires route data (can be seeded via API or SQL)

### Service Deployment

1. **Build Services:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Run Services:**
   ```bash
   java -jar services/<service-name>/target/<service-name>-*.jar \
     --spring.profiles.active=prod \
     --server.port=<port> \
     --spring.datasource.url=jdbc:postgresql://<db-host>:<db-port>/<db-name> \
     --spring.datasource.username=<db-user> \
     --spring.datasource.password=<db-password>
   ```

3. **Frontend Build:**
   ```bash
   cd frontend
   npm run build
   npm start  # Production server
   ```

### Docker Deployment (Recommended)

Create `docker-compose.prod.yml` for production with:
- Environment variables from secure vault
- Health checks for all services
- Resource limits
- Logging configuration
- Network isolation

## 📚 API Documentation

### Gateway Service (Port 8080)

All API requests should go through the gateway:

- **User Service:** `http://localhost:8080/api/users/**`
- **Vehicle Service:** `http://localhost:8080/api/v1/vehicles/**`
- **World View Service:** `http://localhost:8080/api/v1/routes/**`, `/api/v1/journeys/**`
- **Merchandise Service:** `http://localhost:8080/api/merchandise/**` (if configured)

### Service-Specific Endpoints

Each service also exposes Swagger UI:
- Vehicle Service: `http://localhost:8081/swagger-ui.html`
- World View Service: `http://localhost:8082/swagger-ui.html`
- User Service: `http://localhost:8083/swagger-ui.html`
- Merchandise Service: `http://localhost:8084/swagger-ui.html`

## 🧪 Testing

### Backend Tests

```bash
# Run all tests
./mvnw test

# Run tests for specific service
./mvnw test -pl services/vehicle-service
```

### Frontend Tests

```bash
cd frontend
npm test
```

## 📊 Monitoring & Health Checks

All services expose actuator endpoints:

- Health: `http://localhost:<port>/actuator/health`
- Info: `http://localhost:<port>/actuator/info`
- Metrics: `http://localhost:<port>/actuator/metrics`

## 🗂️ Project Structure

```
nebula/
├── docker/                    # Docker Compose and init scripts
│   ├── docker-compose.yml    # Infrastructure services
│   ├── init-scripts/         # Database initialization scripts
│   └── rabbitmq/             # RabbitMQ configuration
├── frontend/                  # Next.js frontend application
│   ├── app/                  # Next.js app directory
│   ├── components/           # React components
│   └── public/              # Static assets
├── services/                 # Backend microservices
│   ├── gateway-service/      # API Gateway
│   ├── vehicle-service/      # Vehicle management
│   ├── world-view/           # Journey simulation
│   ├── user-service/         # User management
│   ├── merchandise-service/  # Product catalog
│   └── platform-core/        # Config Server & Eureka
└── pom.xml                   # Root Maven POM
```

## 🔐 Security

- **JWT Authentication:** All protected endpoints require valid JWT tokens
- **Token Blacklisting:** Logout tokens are blacklisted in database
- **CORS:** Configured per service (currently permissive for development)
- **Password Hashing:** BCrypt with strength 10
- **SQL Injection:** Protected via JPA/Hibernate parameterized queries
- **XSS Protection:** Frontend uses React's built-in XSS protection

## 🐛 Troubleshooting

### Service Won't Start

1. Check if required infrastructure is running (PostgreSQL, RabbitMQ)
2. Verify environment variables are set correctly
3. Check service logs for specific errors
4. Ensure port is not already in use (see `ports.md`)

### Database Connection Issues

1. Verify PostgreSQL is running: `docker ps`
2. Check connection credentials in `application-dev.yaml`
3. Ensure database and schemas are initialized
4. Check network connectivity

### Frontend Can't Connect to Backend

1. Verify backend services are running
2. Check CORS configuration in backend services
3. Verify API URLs in `frontend/.env.local`
4. Check browser console for specific errors

## 📝 Development Guidelines

- Follow existing code style and patterns
- Write unit tests for new features
- Update documentation when adding new endpoints
- Use meaningful commit messages
- Create feature branches for new work

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Update documentation
5. Submit a pull request

## 📄 License

[Add your license information here]

## 🔗 Additional Resources

- [Ports Documentation](./ports.md) - Complete port reference
- [Service READMEs](./services/*/README.md) - Service-specific documentation
- [Frontend README](./frontend/README.md) - Frontend setup guide