-- =============================================================================
-- Grant Access Script
-- Grants necessary permissions to the application user for all schemas
-- =============================================================================
-- Note: POSTGRES_USER is set via environment variable
-- This script should run after all schemas and tables are created
-- =============================================================================

DO $$
BEGIN
    -- Grant usage on all schemas
    EXECUTE format('GRANT USAGE ON SCHEMA world_view TO %I', current_user);
    EXECUTE format('GRANT USAGE ON SCHEMA vehicle_service TO %I', current_user);
    EXECUTE format('GRANT USAGE ON SCHEMA user_service TO %I', current_user);
    EXECUTE format('GRANT USAGE ON SCHEMA merchandise_service TO %I', current_user);
    EXECUTE format('GRANT USAGE ON SCHEMA user_vehicle_service TO %I', current_user);

    -- Grant all privileges on all tables in schemas
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA world_view TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA vehicle_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA user_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA merchandise_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA user_vehicle_service TO %I', current_user);

    -- Grant all privileges on all sequences
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA world_view TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA vehicle_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA user_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA merchandise_service TO %I', current_user);
    EXECUTE format('GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA user_vehicle_service TO %I', current_user);

    -- Set default privileges for future tables
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA world_view GRANT ALL ON TABLES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA vehicle_service GRANT ALL ON TABLES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON TABLES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA merchandise_service GRANT ALL ON TABLES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA user_vehicle_service GRANT ALL ON TABLES TO %I', current_user);

    -- Set default privileges for future sequences
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA world_view GRANT ALL ON SEQUENCES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA vehicle_service GRANT ALL ON SEQUENCES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON SEQUENCES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA merchandise_service GRANT ALL ON SEQUENCES TO %I', current_user);
    EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA user_vehicle_service GRANT ALL ON SEQUENCES TO %I', current_user);

    RAISE NOTICE 'Permissions granted successfully to user: %', current_user;
END $$;

