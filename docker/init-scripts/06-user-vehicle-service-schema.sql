-- =============================================================================
-- User Vehicle Service Schema
-- Stores user-vehicle assignments with maintenance data
-- =============================================================================

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS user_vehicle_service;

-- Set search path
SET search_path TO user_vehicle_service;

-- =============================================================================
-- Table: user_vehicle
-- Stores the mapping between users and their assigned vehicles
-- =============================================================================
CREATE TABLE IF NOT EXISTS user_vehicle (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    vehicle_id INTEGER NOT NULL,
    vehicle_name VARCHAR(255) NOT NULL,
    maintenance_due_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast lookup by user_id
CREATE INDEX IF NOT EXISTS idx_user_vehicle_user_id ON user_vehicle(user_id);

-- Comment on table and columns for documentation
COMMENT ON TABLE user_vehicle IS 'Stores user-vehicle assignments with maintenance data';
COMMENT ON COLUMN user_vehicle.id IS 'Auto-generated primary key';
COMMENT ON COLUMN user_vehicle.user_id IS 'Unique identifier of the user';
COMMENT ON COLUMN user_vehicle.vehicle_id IS 'Reference to vehicle in vehicle-service';
COMMENT ON COLUMN user_vehicle.vehicle_name IS 'Name of the assigned vehicle';
COMMENT ON COLUMN user_vehicle.maintenance_due_date IS 'Next maintenance due date (6 months from assignment)';
COMMENT ON COLUMN user_vehicle.created_at IS 'Timestamp when the assignment was created';
COMMENT ON COLUMN user_vehicle.updated_at IS 'Timestamp when the assignment was last updated';

