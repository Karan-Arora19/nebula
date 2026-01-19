"use client";

import { useState, useEffect, useCallback } from "react";
import { vehicleServiceApi } from "../lib/api";

/**
 * Helper to extract default configuration from vehicle config
 * Prevents duplicate code between initial load and reset
 */
export const getDefaultConfiguration = (config) => {
  if (!config?.categories) return {};

  const defaultConfig = {};
  config.categories.forEach((category) => {
    if (category.parts?.length > 0) {
      defaultConfig[category.id] = category.parts[0].visualKey;
    }
  });
  return defaultConfig;
};

/**
 * useVehicleConfiguration Hook
 *
 * Handles vehicle configuration loading and state management.
 * Extracted from page.js for better maintainability.
 */
export function useVehicleConfiguration(
  currentVehicleId,
  setServiceStatus,
  setServiceMessage,
) {
  const [vehicleConfig, setVehicleConfig] = useState(null);
  const [configuration, setConfiguration] = useState({});
  const [activeCategory, setActiveCategory] = useState("paint");
  const [isLoadingConfig, setIsLoadingConfig] = useState(false);
  const [isRendering, setIsRendering] = useState(true);
  const [modelLoaded, setModelLoaded] = useState(false);

  // Fetch configuration when vehicle changes
  useEffect(() => {
    if (!currentVehicleId) return;

    let isMounted = true;
    const abortController = new AbortController();

    const fetchConfiguration = async () => {
      try {
        setIsLoadingConfig(true);
        setModelLoaded(false);
        setIsRendering(true);

        const config =
          await vehicleServiceApi.getVehicleConfiguration(currentVehicleId);

        if (!isMounted || abortController.signal.aborted) return;

        if (config.error) {
          setServiceStatus("error");
          setServiceMessage(config.message);
          return;
        }

        setVehicleConfig(config);
        setConfiguration(getDefaultConfiguration(config));
        setActiveCategory(config.categories?.[0]?.id || "paint");
      } catch (error) {
        if (abortController.signal.aborted || !isMounted) return;

        console.error("Failed to fetch configuration:", error);
        if (isMounted) {
          setServiceStatus("error");
          setServiceMessage(
            "Failed to load vehicle configuration. Please check the service.",
          );
        }
      } finally {
        if (isMounted && !abortController.signal.aborted) {
          setIsLoadingConfig(false);
        }
      }
    };

    fetchConfiguration();

    return () => {
      isMounted = false;
      abortController.abort();
    };
  }, [currentVehicleId, setServiceStatus, setServiceMessage]);

  // Handle model load completion - finish rendering effect
  const handleModelLoad = useCallback(() => {
    setModelLoaded(true);
  }, []);

  // Reset configuration to defaults
  const handleReset = useCallback(() => {
    if (!vehicleConfig) return;
    setConfiguration(getDefaultConfiguration(vehicleConfig));
  }, [vehicleConfig]);

  // Set rendering complete after model loads
  useEffect(() => {
    if (modelLoaded) {
      const timer = setTimeout(() => setIsRendering(false), 500);
      return () => clearTimeout(timer);
    }
  }, [modelLoaded]);

  return {
    vehicleConfig,
    configuration,
    setConfiguration,
    activeCategory,
    setActiveCategory,
    isLoadingConfig,
    isRendering,
    modelLoaded,
    handleModelLoad,
    handleReset,
  };
}