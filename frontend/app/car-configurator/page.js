"use client";

/**
 * Car Configurator Page
 *
 * Main page component for the Nebula car configurator feature.
 * Allows users to customize their vehicle with real-time 3D preview.
 *
 * Features:
 * - 3D interactive car viewer
 * - Customization panel with multiple categories
 * - Real-time price calculation
 * - Configuration management
 * - API integration with vehicle-service
 */

import React, { useState, useMemo, useCallback, useEffect } from "react";
import Vehicle3DScene from "../../components/Vehicle3DScene";
import CustomizationPanel from "../../components/CustomizationPanel";
import CarSelector from "../../components/CarSelector";
import RenderingEffect from "../../components/RenderingEffect";
import VideoIntroEffect from "../../components/VideoIntroEffect";

import LoadingSkeleton, {
  CategoryTabSkeleton,
} from "../../components/LoadingSkeleton";
import { vehicleServiceApi } from "./lib/api";
import { useAudioManager } from "../../hooks/useAudioManager";

export default function CarConfiguratorPage() {
  // API State
  const [vehicles, setVehicles] = useState([]);
  const [vehicleConfig, setVehicleConfig] = useState(null);
  const [serviceStatus, setServiceStatus] = useState("loading"); // 'loading', 'connected', 'error'
  const [serviceMessage, setServiceMessage] = useState("");

  // UI State
  const [configuration, setConfiguration] = useState({});
  const [activeCategory, setActiveCategory] = useState("paint");
  const [currentVehicleId, setCurrentVehicleId] = useState(null);
  const [isRendering, setIsRendering] = useState(true);
  const [modelLoaded, setModelLoaded] = useState(false);
  const [isLoadingConfig, setIsLoadingConfig] = useState(false);
  const [isPaintChanging, setIsPaintChanging] = useState(false);

  // Audio management (paint SFX only)
  const { playPaintSfx } = useAudioManager();

  // Get current vehicle
  const currentVehicle = useMemo(() => {
    if (!currentVehicleId || !vehicles.length) return null;
    const vehicle =
      vehicles.find((v) => v.vehicleId === currentVehicleId) || vehicles[0];
    return vehicle
      ? {
          id: vehicle.vehicleId,
          name: vehicle.carName || vehicle.name,
          modelPath: vehicle.modelPath,
        }
      : null;
  }, [currentVehicleId, vehicles]);

  // Check service health and fetch vehicles
  useEffect(() => {
    let isMounted = true;
    let retryInterval = null;

    const checkServiceAndFetch = async () => {
      try {
        setServiceStatus("loading");
        setServiceMessage("Connecting to vehicle service...");

        // Fetch vehicles - API now returns error object instead of throwing
        const result = await vehicleServiceApi.getAllVehicles();

        if (!isMounted) return;

        // Handle API error response
        if (result.error) {
          setServiceStatus("error");
          setServiceMessage(result.message);
          setVehicles([]);

          // Start retry mechanism only if not already running
          if (!retryInterval && isMounted) {
            retryInterval = setInterval(() => {
              if (isMounted) {
                checkServiceAndFetch();
              }
            }, 5000);
          }
          return;
        }

        // Success case
        const fetchedVehicles = result.vehicles;

        if (!fetchedVehicles || fetchedVehicles.length === 0) {
          setServiceStatus("error");
          setServiceMessage(
            "No vehicles found in database. Please seed the database.",
          );
          setVehicles([]);
          return;
        }

        setVehicles(fetchedVehicles);
        setServiceStatus("connected");
        setServiceMessage("");

        // Set first vehicle as default
        if (fetchedVehicles.length > 0 && !currentVehicleId) {
          setCurrentVehicleId(fetchedVehicles[0].vehicleId);
        }

        // Clear retry interval on success
        if (retryInterval) {
          clearInterval(retryInterval);
          retryInterval = null;
        }
      } catch (error) {
        // This catch block should rarely be hit now
        if (!isMounted) return;
        console.error("Unexpected error:", error);
        setServiceStatus("error");
        setServiceMessage("An unexpected error occurred. Please try again.");
        setVehicles([]);
      }
    };

    // Initial check
    checkServiceAndFetch();

    return () => {
      isMounted = false
      if (retryInterval) {
        clearInterval(retryInterval);
      }
    };
  }, []); // Empty dependency array - only run once on mount

  // Fetch configuration when vehicle changes
  useEffect(() => {
    if (!currentVehicleId || serviceStatus !== "connected") return;

    let isMounted = true;
    let abortController = new AbortController();

    const fetchConfiguration = async () => {
      try {
        setIsLoadingConfig(true);
        setModelLoaded(false);
        setIsRendering(true);

        const config =
          await vehicleServiceApi.getVehicleConfiguration(currentVehicleId);

        if (!isMounted || abortController.signal.aborted) return;

        // Handle API error response
        if (config.error) {
          setServiceStatus("error");
          setServiceMessage(config.message);
          return;
        }

        setVehicleConfig(config);

        // Set default configuration from first options
        const defaultConfig = {};
        if (config.categories) {
          config.categories.forEach((category) => {
            if (category.parts && category.parts.length > 0) {
              defaultConfig[category.id] = category.parts[0].visualKey;
            }
          });
        }
        setConfiguration(defaultConfig);
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
  }, [currentVehicleId, serviceStatus]);

  /**
   * Handles vehicle change
   */
  const handleVehicleChange = useCallback(
    (newVehicleId) => {
      if (newVehicleId !== currentVehicleId) {
        setCurrentVehicleId(newVehicleId);
        setConfiguration({});
        setModelLoaded(false);
        setIsRendering(true);
      }
    },
    [currentVehicleId],
  );

  /**
   * Called when 3D model finishes loading
   */
  const handleModelLoad = useCallback(() => {
    setModelLoaded(true);
  }, []);

  /**
   * Handles part selection in a category
   * For paint changes, syncs with audio SFX and flash animation
   */
  const handlePartSelect = useCallback(
    (categoryId, partVisualKey) => {
      // Special handling for paint selection - sync with audio + flash
      if (categoryId === "paint") {
        // Trigger flash effect
        setIsPaintChanging(true);

        // Play paint SFX, apply color change mid-flash, then end flash
        playPaintSfx(() => {
          // Apply the color change
          setConfiguration((prev) => ({
            ...prev,
            [categoryId]: partVisualKey,
          }));

          // Small delay before ending flash for smooth reveal
          setTimeout(() => {
            setIsPaintChanging(false);
          }, 150);
        });
      } else {
        // Non-paint selections apply immediately
        setConfiguration((prev) => ({
          ...prev,
          [categoryId]: partVisualKey,
        }));
      }
    },
    [playPaintSfx],
  );

  /**
   * Calculate pricing and selected parts count
   */
  const { totalPrice, customizationCost, selectedPartsCount } = useMemo(() => {
    if (!vehicleConfig) {
      return { totalPrice: 0, customizationCost: 0, selectedPartsCount: 0 };
    }

    let total = vehicleConfig.basePrice || 0;
    const selectedParts = [];

    vehicleConfig.categories?.forEach((category) => {
      const selectedPartKey = configuration[category.id];
      const selectedPart = category.parts?.find(
        (part) => part.visualKey === selectedPartKey,
      );
      if (selectedPart) {
        total += selectedPart.cost || 0;
        if (selectedPart.cost > 0) {
          selectedParts.push({ category: category.name, part: selectedPart });
        }
      }
    });

    return {
      totalPrice: total,
      customizationCost: total - (vehicleConfig.basePrice || 0),
      selectedPartsCount: selectedParts.length,
    };
  }, [configuration, vehicleConfig]);

  /**
   * Resets the configuration to default values
   * Uses paint change animation if the paint color changes
   */
  const handleReset = useCallback(() => {
    if (!vehicleConfig) return;

    const defaultConfig = {};
    vehicleConfig.categories?.forEach((category) => {
      if (category.parts && category.parts.length > 0) {
        defaultConfig[category.id] = category.parts[0].visualKey;
      }
    });

    // Check if paint color is changing
    const currentPaint = configuration.paint;
    const defaultPaint = defaultConfig.paint;

    if (currentPaint !== defaultPaint && defaultPaint) {
      // Paint is changing - use the same animation as handlePartSelect
      setIsPaintChanging(true);

      playPaintSfx(() => {
        // Apply the full reset
        setConfiguration(defaultConfig);

        // Small delay before ending flash for smooth reveal
        setTimeout(() => {
          setIsPaintChanging(false);
        }, 150);
      });
    } else {
      // No paint change, just reset directly
      setConfiguration(defaultConfig);
    }
  }, [vehicleConfig, configuration.paint, playPaintSfx]);

  // Show loading state
  if (
    serviceStatus === "loading" ||
    (serviceStatus === "connected" && !vehicleConfig && isLoadingConfig)
  ) {
    return (
      <div className="flex flex-col h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
        <div className="flex flex-1 items-center justify-center">
          <div className="text-center">
            <div className="w-16 h-16 border-4 border-red-500/30 border-t-red-500 rounded-full animate-spin mx-auto mb-4"></div>
            <p className="text-gray-400 text-sm">
              Loading vehicle configuration...
            </p>
          </div>
        </div>
      </div>
    );
  }

  // Show error state - ONLY show the main error UI, no duplicate notification
  if (serviceStatus === "error" || !vehicleConfig) {
    return (
      <div className="flex flex-col h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
        <div className="flex flex-1 items-center justify-center">
          <div className="text-center max-w-lg px-6">
            {/* Icon */}
            <div className="w-24 h-24 mx-auto mb-6 rounded-full bg-red-500/10 border-2 border-red-500/50 flex items-center justify-center">
              <svg
                className="w-12 h-12 text-red-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
            </div>

            {/* Title */}
            <h2 className="text-2xl font-bold mb-3 text-white">
              Vehicle Service Unavailable
            </h2>

            {/* Error Message */}
            <div className="bg-red-500/10 border border-red-500/30 rounded-lg px-4 py-3 mb-6">
              <p className="text-red-200 text-sm">
                {serviceMessage || "Unable to connect to the vehicle service."}
              </p>
            </div>

            {/* Help Text */}
            <p className="text-gray-400 text-sm mb-6">
              Make sure the vehicle-service is running and accessible.
            </p>

            {/* Retry Button */}
            <button
              onClick={() => {
                setServiceStatus("loading");
                setCurrentVehicleId(null);
                setVehicleConfig(null);
              }}
              className="px-8 py-3 bg-gradient-to-r from-red-600 to-red-700 hover:from-red-500 hover:to-red-600 rounded-lg text-sm font-semibold transition-all shadow-lg shadow-red-500/20 hover:shadow-red-500/30"
            >
              Retry Connection
            </button>

            {/* Auto-retry indicator */}
            <p className="text-gray-500 text-xs mt-4">
              Auto-retrying every 5 seconds...
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
      {/* Rendering Effect Overlay */}
      <RenderingEffect
        isRendering={isRendering}
        modelLoaded={modelLoaded}
        onComplete={() => setIsRendering(false)}
      />

      {/* Main Content */}
      <div className="flex flex-1 overflow-hidden relative">
        {/* Decorative corner accents */}
        <div className="absolute top-0 left-0 w-32 h-32 bg-gradient-to-br from-red-500/10 to-transparent pointer-events-none z-10"></div>
        <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-bl from-red-500/10 to-transparent pointer-events-none z-10"></div>

        {/* Left: 3D Viewer */}
        <div className="flex-1 overflow-hidden p-4">
          <div className="w-full h-full relative rounded-xl overflow-hidden border border-gray-700/50 shadow-xl">
            <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-red-500/20 via-transparent to-red-500/20 opacity-50 pointer-events-none"></div>
            <div className="absolute inset-[1px] rounded-xl border border-red-500/30 pointer-events-none"></div>

            <div className="w-full h-full relative">
              {/* Video Intro Effect - Only plays on FIRST visit, positioned within 3D viewer */}
              <VideoIntroEffect
                videoSrc="/videos/car-intro.mp4"
                onComplete={() => {}}
              />
              {currentVehicle && (
                <Vehicle3DScene
                  vehicleName={
                    currentVehicle.name || vehicleConfig?.name || "Vehicle"
                  }
                  configuration={configuration}
                  modelPath={
                    currentVehicle.modelPath || vehicleConfig?.modelPath
                  }
                  onModelLoad={handleModelLoad}
                />
              )}

              {/* Paint Change Flash Effect */}
              <div
                className={`absolute inset-0 pointer-events-none transition-opacity duration-200 ${
                  isPaintChanging ? "opacity-100" : "opacity-0"
                }`}
                style={{
                  background:
                    "radial-gradient(circle at center, rgba(239, 68, 68, 0.4) 0%, rgba(0, 0, 0, 0.8) 70%)",
                }}
              >
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="w-32 h-32 border-4 border-red-500/50 rounded-full animate-ping" />
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Customization Panel */}
        <div className="w-80 border-l border-gray-700/50 overflow-hidden bg-gray-900/50 backdrop-blur-sm shadow-xl">
          <div className="h-full border-l border-red-500/20">
            {isLoadingConfig ? (
              <div className="flex flex-col h-full">
                <CategoryTabSkeleton />
                <div className="flex-1 overflow-y-auto p-4">
                  <LoadingSkeleton count={4} />
                </div>
              </div>
            ) : (
              <CustomizationPanel
                categories={vehicleConfig.categories || []}
                activeCategory={activeCategory}
                setActiveCategory={setActiveCategory}
                configuration={configuration}
                onPartSelect={handlePartSelect}
              />
            )}
          </div>
        </div>
      </div>

      {/* Footer: Price Summary */}
      <footer className="bg-gradient-to-r from-gray-900 via-black to-gray-900 border-t border-gray-700/50 backdrop-blur-sm shadow-xl">
        <div className="px-6 py-4">
          <div className="flex items-center justify-between gap-6">
            {/* Left: Branding & Summary */}
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center shadow-md shadow-red-500/30">
                  <span className="text-white font-bold text-sm">N</span>
                </div>
                <div>
                  <p className="text-white font-semibold text-xs">
                    {currentVehicle?.name ||
                      vehicleConfig?.name ||
                      "Loading..."}
                  </p>
                  <p className="text-gray-400 text-[10px]">
                    {selectedPartsCount} premium upgrade
                    {selectedPartsCount !== 1 ? "s" : ""}
                  </p>
                </div>
              </div>
            </div>

            {/* Center: Car Selector */}
            <div className="flex items-center justify-center flex-1">
              {vehicles.length > 0 ? (
                <CarSelector
                  currentCarId={currentVehicleId}
                  onCarChange={handleVehicleChange}
                  availableCars={vehicles.map((v) => ({
                    id: v.vehicleId,
                    name: v.carName || v.name,
                    modelPath: v.modelPath,
                  }))}
                />
              ) : (
                <div className="h-10 w-32 bg-gray-800/50 rounded-lg animate-pulse"></div>
              )}
            </div>

            {/* Right: Price Breakdown */}
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-4">
                <div className="text-center">
                  <p className="text-gray-500 text-[9px] uppercase tracking-wider mb-1 font-semibold">
                    Base Price
                  </p>
                  <p className="text-white text-base font-bold">
                    €{(vehicleConfig.basePrice || 0).toLocaleString()}
                  </p>
                </div>

                <div className="h-10 w-px bg-gradient-to-b from-transparent via-gray-600 to-transparent"></div>

                <div className="text-center">
                  <p className="text-gray-500 text-[9px] uppercase tracking-wider mb-1 font-semibold">
                    Customization
                  </p>
                  <p
                    className={`text-base font-bold transition-colors ${
                      customizationCost > 0 ? "text-red-400" : "text-gray-500"
                    }`}
                  >
                    {customizationCost > 0 ? "+" : ""}€
                    {customizationCost.toLocaleString()}
                  </p>
                </div>

                <div className="h-10 w-px bg-gradient-to-b from-transparent via-gray-600 to-transparent"></div>

                <div className="text-center relative">
                  <p className="text-gray-500 text-[9px] uppercase tracking-wider mb-1 font-semibold">
                    Total Price
                  </p>
                  <div className="relative">
                    <p className="text-green-400 text-2xl font-bold tracking-tight">
                      €{totalPrice.toLocaleString()}
                    </p>
                    <div className="absolute -inset-1 bg-green-400/20 blur-xl rounded-lg"></div>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center gap-2">
                <button
                  onClick={handleReset}
                  disabled={!vehicleConfig}
                  className="px-4 py-2 bg-gray-800/80 hover:bg-gray-700 rounded-lg text-xs font-semibold transition-all duration-200 border border-gray-700 hover:border-gray-600 backdrop-blur-sm disabled:opacity-50 disabled:cursor-not-allowed"
                  title="Reset All Customizations"
                >
                  Reset All
                </button>

                <button
                  disabled={!vehicleConfig}
                  className="px-6 py-2 bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 rounded-lg text-xs font-bold transition-all duration-200 shadow-lg shadow-red-500/40 hover:shadow-red-500/60 transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Save Configuration
                </button>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
