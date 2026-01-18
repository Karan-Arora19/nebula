# User Service

Authentication and user management microservice for the Nebula platform.

## Features
- User registration with secure password hashing (BCrypt)
- User authentication and JWT token generation (RS256)
- JWKS endpoint for public key distribution
- User profile management (CRUD operations)
- Integration with API Gateway for secure routing

## Endpoints

### Public Endpoints (No Authentication Required)
- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - Login and receive JWT token
- `GET /api/users/.well-known/jwks.json` - Get public key for JWT verification

### Protected Endpoints (JWT Required)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/by-username/{username}` - Get user by username
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## JWT Token Structure
```json
{
  "sub": "user-id",
  "email": "user@example.com",
  "iat": 1704358800,
  "exp": 1704362400,
  "roles": []
}
```

## Database
Uses PostgreSQL with JPA/Hibernate.
- **Table:** `users`
- **Columns:** id, username, first_name, last_name, email, password

## Configuration
- **Port:** 8083
- **Database:** PostgreSQL (nebula_db)
- **JWT Algorithm:** RS256 (RSA asymmetric encryption)
- **Token Expiry:** 1 hour (3600 seconds)