-- =============================================================================
-- Merchandise Service Schema
-- Creates all tables for the Merchandise Service
-- =============================================================================

-- Products table
CREATE TABLE IF NOT EXISTS merchandise_service.products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    price DECIMAL(15, 2) NOT NULL,
    stock INTEGER NOT NULL,
    image_url VARCHAR(500)
);

-- Carts table (one cart per user)
CREATE TABLE IF NOT EXISTS merchandise_service.carts (
    user_id VARCHAR(255) PRIMARY KEY
);

-- Cart Items table (items in a user's cart)
CREATE TABLE IF NOT EXISTS merchandise_service.cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_user_id VARCHAR(255) NOT NULL REFERENCES merchandise_service.carts(user_id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(15, 2) NOT NULL
);

-- Indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_user_id ON merchandise_service.cart_items(cart_user_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product_id ON merchandise_service.cart_items(product_id);