-- =============================================================================
-- User Service Schema
-- Creates all tables for the User Service
-- =============================================================================

-- Users table
CREATE TABLE IF NOT EXISTS user_service.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    profile_image VARCHAR(500),
    country VARCHAR(100),
    city VARCHAR(100),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN'))
);

-- Blacklisted Tokens table (for JWT token blacklisting on logout)
CREATE TABLE IF NOT EXISTS user_service.blacklisted_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(1000) NOT NULL UNIQUE,
    blacklisted_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

-- Indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON user_service.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON user_service.users(email);
CREATE INDEX IF NOT EXISTS idx_blacklisted_tokens_token ON user_service.blacklisted_tokens(token);
CREATE INDEX IF NOT EXISTS idx_blacklisted_tokens_expires_at ON user_service.blacklisted_tokens(expires_at);