"use client";

import { API_TIMEOUT_MS } from "../constants";

// Use gateway for all API requests (port 8080)
// Gateway routes: /api/v1/vehicles/** → vehicle-service
const VEHICLE_SERVICE_URL =
  process.env.NEXT_PUBLIC_GATEWAY_URL || "http://localhost:8080";

/**
 * Error types for better error handling
 */
export const API_ERROR_TYPES = {
  NETWORK: "NETWORK",
  TIMEOUT: "TIMEOUT",
  NOT_FOUND: "NOT_FOUND",
  SERVER_ERROR: "SERVER_ERROR",
  UNKNOWN: "UNKNOWN",
};

/**
 * Creates a standardized API error object
 */
const createApiError = (type, message, originalError = null) => ({
  error: true,
  type,
  message,
  originalError,
});

class VehicleServiceApi {
  constructor(baseUrl = VEHICLE_SERVICE_URL) {
    this.baseUrl = baseUrl;
  }

  /**
   * Get all available vehicles
   * Returns { vehicles: [] } on success, or { error: true, type, message } on failure
   */
  async getAllVehicles() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/vehicles?size=100`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        signal: AbortSignal.timeout(API_TIMEOUT_MS), // 5 second timeout
      });

      if (!res.ok) {
        if (res.status === 500 || res.status === 502 || res.status === 503) {
          return createApiError(
            API_ERROR_TYPES.SERVER_ERROR,
            "Vehicle service is not running. Please start the vehicle-service.",
          );
        }
        if (res.status === 404) {
          return createApiError(
            API_ERROR_TYPES.NOT_FOUND,
            "Vehicle API endpoint not found. Check service configuration.",
          );
        }
        return createApiError(
          API_ERROR_TYPES.UNKNOWN,
          `Unexpected error: ${res.status} ${res.statusText}`,
        );
      }

      const data = await res.json();
      return { vehicles: data.vehicles || [] };
    } catch (error) {
      console.error("Error fetching vehicles:", error);

      if (error.name === "AbortError") {
        return createApiError(
          API_ERROR_TYPES.TIMEOUT,
          "Vehicle service is not responding. Please check if it is running.",
        );
      }
      if (error.name === "TypeError" && error.message.includes("fetch")) {
        return createApiError(
          API_ERROR_TYPES.NETWORK,
          "Cannot connect to gateway. Please ensure gateway (port 8080) is running.",
        );
      }

      return createApiError(
        API_ERROR_TYPES.UNKNOWN,
        "An unexpected error occurred. Please try again.",
      );
    }
  }

  /**
   * Get configuration options for a specific vehicle
   * Returns config object on success, or { error: true, type, message } on failure
   */
  async getVehicleConfiguration(vehicleId) {
    try {
      const res = await fetch(
        `${this.baseUrl}/api/v1/vehicles/${vehicleId}/configuration`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          signal: AbortSignal.timeout(API_TIMEOUT_MS), // 5 second timeout
        },
      );

      if (!res.ok) {
        if (res.status === 404) {
          return createApiError(
            API_ERROR_TYPES.NOT_FOUND,
            `Vehicle configuration not found for ID: ${vehicleId}`,
          );
        }
        if (res.status === 500 || res.status === 502 || res.status === 503) {
          return createApiError(
            API_ERROR_TYPES.SERVER_ERROR,
            "Vehicle service is not running.",
          );
        }
        return createApiError(
          API_ERROR_TYPES.UNKNOWN,
          `Failed to fetch configuration: ${res.status}`,
        );
      }

      return await res.json();
    } catch (error) {
      console.error("Error fetching vehicle configuration:", error);

      if (error.name === "AbortError") {
        return createApiError(
          API_ERROR_TYPES.TIMEOUT,
          "Request timed out. Vehicle service may be overloaded.",
        );
      }
      if (error.name === "TypeError" && error.message.includes("fetch")) {
        return createApiError(
          API_ERROR_TYPES.NETWORK,
          "Network error. Cannot connect to gateway.",
        );
      }

      return createApiError(
        API_ERROR_TYPES.UNKNOWN,
        "An unexpected error occurred.",
      );
    }
  }
}

export const vehicleServiceApi = new VehicleServiceApi();