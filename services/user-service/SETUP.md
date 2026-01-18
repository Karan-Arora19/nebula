# User Service - Setup Guide

## ✅ Configuration Updated (Following Integration Guide)

Your User Service now uses the **centralized configuration pattern** from Platform-Core Config Server.

---

## 🔧 How It Works

### Configuration Flow:
```
Environment Variables → Platform-Core Config Server → User Service
```

1. **Environment variables** define database credentials
2. **Platform-Core** provides shared configuration via `application-shared-database.yaml`
3. **User Service** imports this configuration

---

## 🚀 Running the Service

### Option 1: With Environment Variables (Recommended for local dev)

```bash
# Set environment variables
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=nebula_db
export POSTGRES_USER=nebula_user
export POSTGRES_PASSWORD=nebula_pass

# Run the service
cd /path/to/nebula
./mvnw spring-boot:run -pl services/user-service
```

### Option 2: With Docker (Uses .env file)

```bash
cd docker
docker-compose up postgres platform-core user-service
```

The `.env` file is automatically loaded by Docker Compose.

---

## 📋 Configuration Details

### Current Setup:

**File:** `application.yml`
```yaml
spring:
  application:
    name: user-service
  
  config:
    import: optional:configserver:http://localhost:8761  # Import from Config Server
  
  profiles:
    active: shared-database  # Use shared database configuration
```

**What happens:**
1. Service starts and connects to Config Server (port 8761)
2. Imports `application-shared-database.yaml` profile
3. Gets database config with environment variable substitution:
   - `jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}`
4. Uses **public schema** (default) for the `users` table

---

## 🗄️ Database Schema

**Database:** `nebula_db`
- **Schema:** `public` (default)
- **Table:** `users`

**Auto-created by Hibernate:**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);
```

---

## 🔍 Troubleshooting

### Service won't start?

**Check 1: Environment variables set?**
```bash
echo $POSTGRES_HOST
echo $POSTGRES_DB
```

**Check 2: Platform-Core running?**
```bash
curl http://localhost:8761/actuator/health
```

**Check 3: Database accessible?**
```bash
psql -h localhost -p 5432 -U nebula_user -d nebula_db
```

### Service starts but can't connect to database?

The Config Server substitutes environment variables. If they're not set, you'll see errors like:
```
Could not resolve placeholder 'POSTGRES_HOST'
```

**Solution:** Set the environment variables before starting the service.

---

## 📊 Comparison with Old Configuration

### ❌ Before (Hardcoded):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nebula_db
    username: nebula_user
    password: nebula_pass
```
- Hardcoded values
- Not following team pattern
- Duplicate configuration

### ✅ After (Config Server):
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8761
  profiles:
    active: shared-database
```
- Centralized configuration
- Follows Integration Guide
- DRY principle (Don't Repeat Yourself)

---

## 🎯 Benefits

✅ **Consistency** - Same pattern as World-View service  
✅ **Maintainability** - Change DB config in one place  
✅ **Security** - Credentials in environment variables, not in code  
✅ **Flexibility** - Easy to switch between dev/test/prod environments  

---

## 📝 Environment Variables Reference

| Variable | Purpose | Example |
|----------|---------|---------|
| `POSTGRES_HOST` | Database server hostname | `localhost` or `nebula-postgres` |
| `POSTGRES_PORT` | Database server port | `5432` |
| `POSTGRES_DB` | Database name | `nebula_db` |
| `POSTGRES_USER` | Database username | `nebula_user` |
| `POSTGRES_PASSWORD` | Database password | `nebula_pass` |

---

## 🔐 JWT Configuration

JWT private/public keys are still in `application.yml` for testing.

**In production**, these should also be externalized to environment variables or a secure vault.
