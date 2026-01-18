'use client'

/**
 * Configuration for World Drive API and MQTT connections.
 * 
 * Required environment variables:
 * - NEXT_PUBLIC_GATEWAY_URL: API Gateway URL (default: http://localhost:8080)
 *   All REST API requests go through the gateway
 * - NEXT_PUBLIC_MQTT_URL: MQTT WebSocket URL (default: ws://localhost:15675/ws)
 *   MQTT bypasses the gateway and connects directly to RabbitMQ
 * - NEXT_PUBLIC_MQTT_USERNAME: MQTT username (required for authentication)
 * - NEXT_PUBLIC_MQTT_PASSWORD: MQTT password (required for authentication)
 * 
 * Set these in your .env.local file (not committed to git).
 */

// Use gateway for all REST API requests (port 8080)
export const API_BASE_URL = process.env.NEXT_PUBLIC_GATEWAY_URL || 'http://localhost:8080'
export const MQTT_BROKER_URL = process.env.NEXT_PUBLIC_MQTT_URL || 'ws://localhost:15675/ws'
export const MQTT_USERNAME = process.env.NEXT_PUBLIC_MQTT_USERNAME
export const MQTT_PASSWORD = process.env.NEXT_PUBLIC_MQTT_PASSWORD