'use client'

import { useState, useEffect, useRef, useCallback } from 'react'
import MapView from './components/MapView'
import JourneyStatusDisplay from './components/JourneyStatusDisplay'
import { worldDriveApi } from './lib/api'

// Dealership coordinates
const DEALERSHIP_LOCATION = {
  lat: 48.8354,
  lng: 9.152,
}

// Polling interval for checking current journey (in ms)
const JOURNEY_POLL_INTERVAL = 2000

export default function WorldDrivePage() {
  // State
  const [journeyState, setJourneyState] = useState(null)
  const [currentPosition, setCurrentPosition] = useState(null)
  const [isConnecting, setIsConnecting] = useState(true)
  const [error, setError] = useState(null)
  const [mqttError, setMqttError] = useState(null)

  // Refs
  const cleanupRef = useRef(null)
  const pollIntervalRef = useRef(null)
  const currentJourneyIdRef = useRef(null)

  // Calculate derived values
  const status = journeyState?.status || 'WAITING'
  const progress = journeyState?.progress_percentage || 0
  const speedKmh = (journeyState?.speed_meters_per_second || 0) * 3.6

  const currentRoute = journeyState?.route || null
  const distanceRemaining = currentRoute
    ? currentRoute.total_distance_meters * (1 - progress / 100)
    : 0
  const estimatedTimeRemaining = speedKmh > 0
    ? (distanceRemaining / 1000) / speedKmh * 3600
    : 0

  // Get waypoints from current route
  const waypoints = currentRoute?.waypoints?.map(wp => ({
    lat: wp.latitude,
    lng: wp.longitude,
  })) || []

  const startPoint = currentRoute?.start_point
    ? { lat: currentRoute.start_point.latitude, lng: currentRoute.start_point.longitude }
    : DEALERSHIP_LOCATION

  // Handle coordinate updates from MQTT
  const handleCoordinateUpdate = useCallback((update) => {
    setCurrentPosition({
      lat: update.coordinate.latitude,
      lng: update.coordinate.longitude,
    })

    setJourneyState(prev => {
      if (!prev) return prev
      return {
        ...prev,
        current_position: update.coordinate,
        current_waypoint_index: update.current_waypoint_index,
        progress_percentage: update.progress_percentage,
        status: update.status,
        speed_meters_per_second: update.speed_meters_per_second || prev.speed_meters_per_second,
      }
    })

    if (update.status === 'COMPLETED') {
      console.log('Journey completed, will wait for next journey...')
    }
  }, [])

  // Cleanup MQTT connection
  const cleanupConnection = useCallback(() => {
    if (cleanupRef.current) {
      cleanupRef.current()
      cleanupRef.current = null
    }
  }, [])

  // Subscribe to journey updates
  const subscribeToJourney = useCallback((journeyId) => {
    // Don't resubscribe to same journey
    if (currentJourneyIdRef.current === journeyId && cleanupRef.current) {
      return
    }

    // Cleanup previous subscription
    cleanupConnection()
    currentJourneyIdRef.current = journeyId

    console.log('Subscribing to journey:', journeyId)
    try {
      cleanupRef.current = worldDriveApi.subscribeToJourney(
        journeyId,
        handleCoordinateUpdate,
        {
          onError: (err) => {
            console.error('MQTT connection error:', err)
            setMqttError(err.message || 'Failed to connect to MQTT broker')
          },
          onEvent: (event) => {
            console.log('Journey event:', event)
            // Handle MQTT error events
            if (event.type === 'mqtt-error') {
              setMqttError(event.message || event.error || 'MQTT connection failed')
            } else if (event.type === 'journey-completed') {
              // Clear MQTT error on journey completion
              setMqttError(null)
            }
          }
        }
      )
      // Clear MQTT error on successful subscription
      setMqttError(null)
    } catch (err) {
      console.error('Failed to subscribe to MQTT:', err)
      setMqttError(err.message || 'Failed to connect to MQTT broker. Real-time updates will not be available.')
    }
  }, [handleCoordinateUpdate, cleanupConnection])

  // Poll for current journey
  const pollCurrentJourney = useCallback(async () => {
    try {
      const journey = await worldDriveApi.getCurrentJourney()

      if (journey) {
        setJourneyState(journey)
        setCurrentPosition({
          lat: journey.current_position.latitude,
          lng: journey.current_position.longitude,
        })

        // Subscribe to real-time updates if not already subscribed
        subscribeToJourney(journey.journey_id)
        setIsConnecting(false)
      } else {
        // No active journey - clear state but keep polling
        if (journeyState?.status === 'COMPLETED') {
          // Keep showing completed state briefly
        } else {
          setJourneyState(null)
          currentJourneyIdRef.current = null
          cleanupConnection()
        }
        setIsConnecting(false)
      }

      // Clear any previous errors on successful poll
      setError(null)
    } catch (err) {
      console.error('Failed to poll journey:', err)
      const errorMessage = err.message || 'Failed to connect to backend. Please ensure the gateway-service and world-view service are running.'
      setError(errorMessage)
      setIsConnecting(false)
      
      // Don't keep polling aggressively if there's a persistent error
      // Will retry on next interval
    }
  }, [journeyState?.status, subscribeToJourney, cleanupConnection])

  // Start polling on mount
  useEffect(() => {
    // Initial poll
    pollCurrentJourney()

    // Set up polling interval
    pollIntervalRef.current = setInterval(pollCurrentJourney, JOURNEY_POLL_INTERVAL)

    return () => {
      if (pollIntervalRef.current) {
        clearInterval(pollIntervalRef.current)
      }
      cleanupConnection()
    }
  }, [pollCurrentJourney, cleanupConnection])

  return (
    <div className="h-dvh max-h-dvh bg-gray-900 text-white flex flex-col overflow-hidden">
      {/* Main content */}
      <main className="flex-1 flex flex-row min-h-0">
        {/* Map area */}
        <div className="flex-1 relative min-h-0 overflow-hidden">
          <MapView
            currentPosition={currentPosition}
            destination={DEALERSHIP_LOCATION}
            startPoint={startPoint}
            waypoints={waypoints}
            status={status}
          />

          {/* Backend API Error toast */}
          {error && (
            <div className="absolute top-4 left-4 right-4 md:left-auto md:right-4 md:w-96 bg-red-900 border border-red-700 text-red-100 p-4 rounded-lg shadow-lg z-[1000]">
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <p className="text-sm font-semibold mb-1">Backend Connection Error</p>
                  <p className="text-xs opacity-90">{error}</p>
                </div>
                <button
                  onClick={() => setError(null)}
                  className="text-red-300 hover:text-red-100 ml-2 flex-shrink-0"
                >
                  ✕
                </button>
              </div>
            </div>
          )}

          {/* MQTT Connection Error toast */}
          {mqttError && (
            <div className={`absolute ${error ? 'top-24' : 'top-4'} left-4 right-4 md:left-auto md:right-4 md:w-96 bg-orange-900 border border-orange-700 text-orange-100 p-4 rounded-lg shadow-lg z-[1000]`}>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <p className="text-sm font-semibold mb-1 flex items-center gap-2">
                    <span>⚠️</span>
                    MQTT Broker Not Connected
                  </p>
                  <p className="text-xs opacity-90">{mqttError}</p>
                  <p className="text-xs opacity-75 mt-2">
                    Real-time position updates are disabled. Journey status will still update via API polling.
                  </p>
                </div>
                <button
                  onClick={() => setMqttError(null)}
                  className="text-orange-300 hover:text-orange-100 ml-2 flex-shrink-0"
                >
                  ✕
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Side panel */}
        <div className="w-[400px] max-w-[40vw] flex-shrink-0 bg-gray-800 p-3 border-l border-gray-700 overflow-hidden flex flex-col">
          <div className="space-y-2.5 overflow-y-auto scrollbar-hide">
            {/* Journey status display */}
            <JourneyStatusDisplay
              status={status}
              progress={progress}
              speedMps={journeyState?.speed_meters_per_second || 0}
              distanceRemainingMeters={distanceRemaining}
              etaSeconds={estimatedTimeRemaining}
              routeName={currentRoute?.name}
              isConnecting={isConnecting}
            />

            {/* Destination & Position - right after stats */}
            <div className="space-y-2">
            {/* Destination */}
            <div className="bg-gray-700/50 rounded-xl p-3 border border-gray-600/50">
              <div className="flex items-center gap-3">
                <div className="w-11 h-11 bg-red-500/20 rounded-lg flex items-center justify-center">
                  <span className="text-lg">🏁</span>
                </div>
                <div className="min-w-0">
                  <p className="text-xs text-gray-400">Destination</p>
                  <p className="text-sm font-semibold text-white truncate">Stuttgart, DE</p>
                </div>
              </div>
            </div>

            {/* Current position */}
            <div className="bg-gray-700/50 rounded-xl p-3 border border-gray-600/50">
              <div className="flex items-center gap-3">
                <div className="w-11 h-11 bg-gray-500/20 rounded-lg flex items-center justify-center">
                  <span className="text-lg">📍</span>
                </div>
                <div className="min-w-0">
                  <p className="text-xs text-gray-400">Position</p>
                  {currentPosition ? (
                    <p className="text-xs font-mono text-white truncate">
                      {currentPosition.lat.toFixed(4)}°, {currentPosition.lng.toFixed(4)}°
                    </p>
                  ) : (
                    <p className="text-xs text-gray-400 truncate">—</p>
                  )}
                </div>
              </div>
            </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}