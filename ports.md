# Nebula Platform - Port Reference

Complete reference of all ports used by Nebula services and infrastructure.

## 🌐 Backend Services

| Service | Port | Environment Variable | Description |
|---------|------|---------------------|-------------|
| **Gateway Service** | `8080` | `SERVER_PORT` | API Gateway - Main entry point for all API requests |
| **Vehicle Service** | `8081` | `SERVER_PORT` | Vehicle inventory and car configurator API |
| **World View Service** | `8082` | `SERVER_PORT` | Journey simulation and route management API |
| **User Service** | `8083` | `SERVER_PORT` | User authentication and profile management API |
| **Merchandise Service** | `8084` | `SERVER_PORT` | Product catalog and shopping cart API |
| **User Vehicle Service** | `8085` | `SERVER_PORT` | User vehicle assignments and real-time telemetry API |
| **Platform Core** | `8761` | `SERVER_PORT` | Config Server and Eureka Service Discovery |

## 🎨 Frontend

| Application | Port | Description |
|-------------|------|-------------|
| **Next.js Dev Server** | `3000` | Frontend development server (default) |
| **Next.js Prod Server** | `3000` | Frontend production server (configurable) |

## 🗄️ Database

| Service | Port | Environment Variable | Description |
|---------|------|---------------------|-------------|
| **PostgreSQL** | `5432` (default) | `POSTGRES_PORT` | Main application database |
| **PostgreSQL (Docker)** | Configurable | `POSTGRES_PORT` | Typically `5434` in development |

## 📨 Message Broker (RabbitMQ)

| Service | Port | Environment Variable | Description |
|---------|------|---------------------|-------------|
| **RabbitMQ AMQP** | `5672` | `RABBITMQ_AMQP_PORT` | AMQP protocol port for message queuing |
| **RabbitMQ MQTT** | `1883` | `RABBITMQ_MQTT_PORT` | MQTT protocol port for real-time updates |
| **RabbitMQ Management** | `15672` | `RABBITMQ_MANAGEMENT_PORT` | Web UI for RabbitMQ management |
| **RabbitMQ Web MQTT** | `15675` | `RABBITMQ_WEB_MQTT_PORT` | WebSocket MQTT for browser clients |

## 🔧 Development Tools

| Service | Port | Description |
|---------|------|-------------|
| **SonarQube** | `9000` | Code quality analysis (optional) |

## 📋 Port Summary Table

### Quick Reference

```
Gateway Service:     8080
Vehicle Service:      8081
World View Service:   8082
User Service:         8083
Merchandise Service:  8084
Platform Core:        8761
Frontend:             3000
PostgreSQL:           5432 (default) / 5434 (Docker)
RabbitMQ AMQP:        5672
RabbitMQ MQTT:        1883
RabbitMQ Management:  15672
RabbitMQ Web MQTT:    15675
SonarQube:            9000
```

## 🔍 Port Conflicts

If you encounter port conflicts:

1. **Check what's using a port:**
   ```bash
   # macOS/Linux
   lsof -i :<port>
   
   # Windows
   netstat -ano | findstr :<port>
   ```

2. **Change service port:**
   - Set `SERVER_PORT` environment variable
   - Or modify `application.yaml` in the service

3. **Common conflicts:**
   - Port 8080: Often used by other web servers
   - Port 5432: Default PostgreSQL port
   - Port 3000: Common for Node.js applications

## 🌍 Network Architecture

```
Internet
   │
   ├─→ Gateway Service (8080) ──┐
   │                            │
   ├─→ Frontend (3000)          │
   │                            │
   └─→ Direct Service Access    │
        (Development only)      │
                                 │
        ┌────────────────────────┘
        │
        ├─→ Vehicle Service (8081)
        ├─→ World View Service (8082)
        ├─→ User Service (8083)
        ├─→ Merchandise Service (8084)
        │
        ├─→ Platform Core (8761) ──→ Eureka & Config Server
        │
        ├─→ PostgreSQL (5432/5434)
        │
        └─→ RabbitMQ
             ├─→ AMQP (5672)
             ├─→ MQTT (1883)
             ├─→ Management (15672)
             └─→ Web MQTT (15675)
```

## 🔐 Security Notes

- **Production:** All services should be behind a reverse proxy (nginx/traefik)
- **Development:** Direct access to services is allowed for debugging
- **Firewall:** Configure firewall rules to restrict access to necessary ports only
- **Internal Services:** Database and message broker should not be exposed publicly

## 📝 Environment Variable Override

All service ports can be overridden using environment variables:

```bash
# Example: Run vehicle service on port 9091
export SERVER_PORT=9091
./mvnw spring-boot:run -pl services/vehicle-service
```

## 🔄 Port Changes

If you need to change a port:

1. **Backend Service:**
   - Update `application.yaml`: `server.port: ${SERVER_PORT:NEW_PORT}`
   - Or set `SERVER_PORT` environment variable

2. **Frontend:**
   - Update API URLs in `.env.local`
   - Or set `NEXT_PUBLIC_API_URL` environment variable

3. **Docker Services:**
   - Update `docker-compose.yml` port mappings
   - Update `.env` file with new port values

## 📚 Related Documentation

- [README.md](./README.md) - Main project documentation
- Service-specific READMEs in `services/*/README.md`
- [Docker Compose](./docker/docker-compose.yml) - Infrastructure port configuration
