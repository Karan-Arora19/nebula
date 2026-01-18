-- =============================================================================
-- World View Service Schema
-- Creates all tables for the World View Service
-- =============================================================================

-- Driving Routes table
CREATE TABLE IF NOT EXISTS world_view.driving_routes (
    route_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_latitude DOUBLE PRECISION NOT NULL,
    start_longitude DOUBLE PRECISION NOT NULL,
    end_latitude DOUBLE PRECISION NOT NULL,
    end_longitude DOUBLE PRECISION NOT NULL,
    total_distance_meters DOUBLE PRECISION NOT NULL,
    estimated_duration_seconds INTEGER NOT NULL
);

-- Waypoints table (coordinates along a route)
CREATE TABLE IF NOT EXISTS world_view.waypoints (
    id BIGSERIAL PRIMARY KEY,
    route_id VARCHAR(255) NOT NULL REFERENCES world_view.driving_routes(route_id) ON DELETE CASCADE,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    sequence_order INTEGER NOT NULL
);

-- Indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_waypoints_route_id ON world_view.waypoints(route_id);
CREATE INDEX IF NOT EXISTS idx_waypoints_sequence_order ON world_view.waypoints(route_id, sequence_order);