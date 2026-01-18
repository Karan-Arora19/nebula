-- =============================================================================
-- Vehicle Service Schema
-- Creates all tables for the Vehicle Service
-- =============================================================================

-- Vehicle table
CREATE TABLE IF NOT EXISTS vehicle_service.vehicle (
    id SERIAL PRIMARY KEY,
    car_name VARCHAR(255) NOT NULL,
    car_type VARCHAR(50) NOT NULL,
    horse_power INTEGER NOT NULL,
    base_price DECIMAL(12, 2) NOT NULL,
    image VARCHAR(500) NOT NULL,
    model_path VARCHAR(500) NOT NULL
);

-- Paint table
CREATE TABLE IF NOT EXISTS vehicle_service.paint (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    visual_key VARCHAR(100) NOT NULL,
    hex VARCHAR(7) NOT NULL
);

-- Paint Price table (prices vary by car type)
CREATE TABLE IF NOT EXISTS vehicle_service.paint_price (
    paint_id INTEGER NOT NULL REFERENCES vehicle_service.paint(id) ON DELETE CASCADE,
    car_type VARCHAR(50) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (paint_id, car_type)
);

-- Rim table
CREATE TABLE IF NOT EXISTS vehicle_service.rim (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    image VARCHAR(500) NOT NULL,
    visual_key VARCHAR(100) NOT NULL
);

-- Rim Price table (prices vary by car type)
CREATE TABLE IF NOT EXISTS vehicle_service.rim_price (
    rim_id INTEGER NOT NULL REFERENCES vehicle_service.rim(id) ON DELETE CASCADE,
    car_type VARCHAR(50) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (rim_id, car_type)
);

-- Interior table
CREATE TABLE IF NOT EXISTS vehicle_service.interior (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    image VARCHAR(500) NOT NULL,
    visual_key VARCHAR(100) NOT NULL
);

-- Interior Price table (prices vary by car type)
CREATE TABLE IF NOT EXISTS vehicle_service.interior_price (
    interior_id INTEGER NOT NULL REFERENCES vehicle_service.interior(id) ON DELETE CASCADE,
    car_type VARCHAR(50) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (interior_id, car_type)
);

-- Indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_vehicle_car_type ON vehicle_service.vehicle(car_type);
CREATE INDEX IF NOT EXISTS idx_paint_price_car_type ON vehicle_service.paint_price(car_type);
CREATE INDEX IF NOT EXISTS idx_rim_price_car_type ON vehicle_service.rim_price(car_type);
CREATE INDEX IF NOT EXISTS idx_interior_price_car_type ON vehicle_service.interior_price(car_type);