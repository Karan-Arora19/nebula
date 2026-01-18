# Vehicle Service

## Overview

The Vehicle Service is responsible for managing vehicle inventory and car configuration options. It provides two main UI flows:

1. **Cars Overview** - Lists all available vehicles with basic information
2. **Car Configurator** - Provides configuration options (paints, rims, interiors) with prices based on car type

## Architecture

This service follows a hexagonal (ports and adapters) architecture:

```
src/main/java/pse/nebula/vehicleservice/
├── VehicleServiceApplication.java    # Spring Boot entry point
├── domain/
│   ├── model/                        # Domain entities and enums
│   ├── port/                         # Repository interfaces
│   └── exception/                    # Domain exceptions
├── application/
│   └── service/                      # Business logic services
└── infrastructure/
    ├── adapter/                      # JPA repository implementations
    ├── config/                       # Spring configuration
    └── rest/                         # REST controllers and DTOs
```

## Configuration

The service uses the shared PostgreSQL database with its own schema: `vehicle_service`

### Default Port

- **8081** (configurable via `SERVER_PORT` environment variable)

### Database Schema

All tables are created in the `vehicle_service` schema. See `docker/init-scripts/02-vehicle-service-schema.sql` for the full schema definition.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/vehicles` | Get all vehicles for overview (paginated) |
| GET | `/api/v1/vehicles/{id}` | Get a specific vehicle by ID |
| GET | `/api/v1/vehicles/{id}/configuration` | Get configuration options for a vehicle |

### Response Examples

#### GET /api/v1/vehicles
```json
{
  "vehicles": [
    {
      "vehicleId": 1,
      "carName": "Furari",
      "carType": "SPORTS",
      "horsePower": 670,
      "basePrice": 245000.00,
      "image": "furarri-hero",
      "modelPath": "/models/furarri.glb"
    }
  ],
  "currentPage": 0,
  "totalPages": 1,
  "totalElements": 12,
  "pageSize": 20,
  "hasNext": false,
  "hasPrevious": false
}
```

#### GET /api/v1/vehicles/{id}/configuration
```json
{
  "id": 1,
  "name": "Furari",
  "modelPath": "/models/furarri.glb",
  "basePrice": 245000,
  "categories": [
    {
      "id": "paint",
      "name": "Exterior Color",
      "icon": "🎨",
      "parts": [
        {
          "id": "paint-1",
          "name": "Black",
          "cost": 500,
          "visualKey": "black",
          "description": "Timeless deep black metallic finish",
          "hex": "#000000",
          "image": null
        }
      ]
    },
    {
      "id": "rims",
      "name": "Rims",
      "icon": "⭕",
      "parts": [...]
    },
    {
      "id": "interior",
      "name": "Interior",
      "icon": "🪑",
      "parts": [...]
    }
  ]
}
```

## Running Locally

```bash
# From the project root
./mvnw spring-boot:run -pl services/vehicle-service
```

## Running Tests

```bash
# From the project root
./mvnw test -pl services/vehicle-service
```

