# User Vehicle Service

Manages user-vehicle assignments and publishes real-time vehicle telemetry (location and fuel) via MQTT.

## Overview

This service handles:
- **Vehicle Assignment**: Assigns a random vehicle from the vehicle catalog to users on their first request
- **Maintenance Tracking**: Sets maintenance due date to 6 months from assignment
- **Tyre Pressure**: Generates random tyre pressures (28-35 PSI) for all 4 tyres
- **Real-time Telemetry**: Publishes vehicle location and fuel data to MQTT every 5 minutes

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    User Vehicle Service (8085)                  │
├─────────────────────────────────────────────────────────────────┤
│  Infrastructure Layer                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ REST Controller │  │ MQTT Publisher  │  │ JPA Repository  │ │
│  │ /api/v1/user-   │  │ (5 min interval)│  │                 │ │
│  │ vehicle/info    │  │                 │  │                 │ │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘ │
│           │                    │                    │           │
├───────────┼────────────────────┼────────────────────┼───────────┤
│  Application Layer             │                    │           │
│  ┌─────────────────────────────┴────────────────────┘           │
│  │  UserVehicleAssignmentService                                │
│  │  - Vehicle assignment logic                                  │
│  │  - Maintenance date calculation                              │
│  │  - Tyre pressure generation                                  │
│  └──────────────────────────────────────────────────────────────┤
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  Domain Layer                                                   │
│  ┌─────────────────┐                                            │
│  │  UserVehicle    │                                            │
│  │  (Entity)       │                                            │
│  └─────────────────┘                                            │
└─────────────────────────────────────────────────────────────────┘
```

## API Endpoints

### Get User Vehicle Info
```
GET /api/v1/user-vehicle/info
Header: X-User-Id: <userId>

Response 200 OK:
{
  "maintenanceDueDate": "2026-07-18",
  "tyrePressures": {
    "frontLeft": 32.5,
    "frontRight": 33.0,
    "rearLeft": 31.5,
    "rearRight": 32.0
  }
}
```

## MQTT Topics

| Topic | Description | Interval |
|-------|-------------|----------|
| `nebula/user/{userId}/vehicle/info` | Real-time vehicle telemetry | Every 5 minutes |

### MQTT Payload Structure
```json
{
  "vehicleName": "Furari",
  "location": {
    "lat": 48.7758,
    "lng": 9.1829
  },
  "fuel": 75.0,
  "timestamp": "2026-01-18T10:30:00Z"
}
```

## Database

**Schema**: `user_vehicle_service`

**Table: user_vehicle**
| Column | Type | Description |
|--------|------|-------------|
| id | SERIAL | Primary key |
| user_id | VARCHAR(255) | Unique user identifier |
| vehicle_id | INTEGER | Reference to vehicle-service |
| vehicle_name | VARCHAR(255) | Name of assigned vehicle |
| maintenance_due_date | DATE | Next maintenance date |
| created_at | TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | Last update time |

> **Note:** Tyre pressures are generated randomly on each API call (28-35 PSI) and are not stored in the database.

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8085 | Service port |
| `mqtt.broker.host` | localhost | MQTT broker host |
| `mqtt.broker.port` | 1883 | MQTT broker port |
| `mqtt.topic.prefix` | nebula/user | MQTT topic prefix |
| `vehicle-service.base-url` | http://localhost:8081 | Vehicle service URL |

## Running the Service

### Prerequisites
1. PostgreSQL database running (via Docker)
2. RabbitMQ with MQTT plugin running (via Docker)
3. Vehicle Service running on port 8081
4. Platform Core (Eureka) running on port 8761

### Start Infrastructure (Docker)
```bash
# From project root
cd docker
docker-compose up -d postgres rabbitmq
```

### Run the Service
```bash
# From project root - using Maven wrapper
./mvnw spring-boot:run -pl services/user-vehicle-service

# Windows
mvnw.cmd spring-boot:run -pl services/user-vehicle-service

# With specific profile
./mvnw spring-boot:run -pl services/user-vehicle-service -Dspring-boot.run.profiles=dev
```

### Run Tests
```bash
# Run all tests
./mvnw test -pl services/user-vehicle-service

# Windows
mvnw.cmd test -pl services/user-vehicle-service

# Run specific test class
./mvnw test -pl services/user-vehicle-service -Dtest=UserVehicleAssignmentServiceTest

# Run integration tests only
./mvnw test -pl services/user-vehicle-service -Dtest=*IntegrationTest

# Run with coverage report
./mvnw test jacoco:report -pl services/user-vehicle-service
```

### Build the Service
```bash
# Build without tests
./mvnw package -pl services/user-vehicle-service -DskipTests

# Build with tests
./mvnw package -pl services/user-vehicle-service

# Clean build
./mvnw clean package -pl services/user-vehicle-service
```

### Test the API
```bash
# Get user vehicle info (replace USER_ID with actual user ID)
curl -X GET http://localhost:8085/api/v1/user-vehicle/info \
  -H "X-User-Id: user-123" \
  -H "Content-Type: application/json"

# Via Gateway (port 8080)
curl -X GET http://localhost:8080/api/v1/user-vehicle/info \
  -H "X-User-Id: user-123" \
  -H "Content-Type: application/json"
```

### Health Check
```bash
curl http://localhost:8085/actuator/health
```

## Dependencies

- **Vehicle Service**: REST calls to fetch available vehicles
- **RabbitMQ**: MQTT broker for real-time updates
- **PostgreSQL**: Persistent storage for user-vehicle assignments
- **Eureka**: Service discovery (required)

