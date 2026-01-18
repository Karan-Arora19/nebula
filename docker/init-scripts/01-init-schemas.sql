-- =============================================================================
-- Nebula Database Schema Initialization
-- Creates schemas for all microservices in the shared PostgreSQL database
-- =============================================================================
-- This script runs on first PostgreSQL container startup.
-- Each service has its own schema for data isolation.
-- =============================================================================

-- World View Service Schema
-- Stores driving routes, waypoints, and journey data
CREATE SCHEMA IF NOT EXISTS world_view;

-- Vehicle Service Schema
-- Stores vehicle models, configurations, and inventory
CREATE SCHEMA IF NOT EXISTS vehicle_service;

-- User Service Schema
-- Stores user accounts, authentication, and profiles
CREATE SCHEMA IF NOT EXISTS user_service;

-- Merchandise Service Schema
-- Stores merchandise catalog, orders, and inventory
CREATE SCHEMA IF NOT EXISTS merchandise_service;

-- Gateway Service does not need a schema (no persistence)
-- Platform Core does not need a schema (config server only)

-- Log successful initialization
DO $$
BEGIN
    RAISE NOTICE 'Nebula database schemas initialized successfully:';
    RAISE NOTICE '  - world_view';
    RAISE NOTICE '  - vehicle_service';
    RAISE NOTICE '  - user_service';
    RAISE NOTICE '  - merchandise_service';
END $$;