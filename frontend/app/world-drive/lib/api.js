'use client'

import { API_BASE_URL } from './config'
import { mqttClient } from './mqtt-client'

class WorldDriveApi {
  constructor(baseUrl = API_BASE_URL) {
    if (!baseUrl) {
      throw new Error('API_BASE_URL is not defined in the environment variables')
    }
    this.baseUrl = baseUrl
  }

  async getAllRoutes() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/routes`)
      if (!res.ok) {
        throw new Error(`Failed to fetch routes: ${res.status} ${res.statusText}`)
      }
      return res.json()
    } catch (error) {
      console.warn('Backend not available, using mock routes:', error.message)
      // Return mock routes data
      return [
        {
          id: 1,
          name: 'Monaco Grand Prix Circuit',
          description: 'Iconic street circuit through Monaco',
          waypoints: []
        },
        {
          id: 2,
          name: 'Alpine Mountain Route',
          description: 'Scenic mountain drive through the Alps',
          waypoints: []
        }
      ]
    }
  }

  async getCurrentJourney() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/journeys/current`)
      if (res.status === 204) {
        return null // No active journey
      }
      if (!res.ok) {
        throw new Error(`Failed to fetch current journey: ${res.status} ${res.statusText}`)
      }
      return res.json()
    } catch (error) {
      console.warn('Backend not available, using mock journey data:', error.message)
      // Return mock journey data when backend is unavailable
      return {
        journey_id: 'journey-demo-001',
        status: 'IN_PROGRESS',
        current_position: {
          latitude: 43.7384,
          longitude: 7.4246
        },
        current_waypoint_index: 2,
        progress_percentage: 35,
        route: {
          name: 'Monaco Grand Prix Circuit',
          waypoints: [
            { latitude: 43.7384, longitude: 7.4246 },
            { latitude: 43.7390, longitude: 7.4250 },
            { latitude: 43.7395, longitude: 7.4255 },
            { latitude: 43.7400, longitude: 7.4260 }
          ]
        }
      }
    }
  }

  async isJourneyActive() {
    try {
      const res = await fetch(`${this.baseUrl}/api/v1/journeys/active`)
      if (!res.ok) {
        throw new Error(`Failed to check journey status: ${res.status} ${res.statusText}`)
      }
      return res.json()
    } catch (error) {
      console.error('Error checking journey status:', error)
      throw error
    }
  }

  subscribeToJourney(journeyId, onUpdate, options) {
    mqttClient
      .subscribeToJourney(journeyId, onUpdate, options?.onEvent)
      .catch((error) => {
        console.error('MQTT subscription failed:', error)
        if (options?.onError) {
          options.onError(error)
        }
      })

    return () => {
      mqttClient.unsubscribeFromJourney(journeyId)
    }
  }

  disconnect() {
    mqttClient.disconnect()
  }

  get isConnected() {
    return mqttClient.connected
  }
}

export const worldDriveApi = new WorldDriveApi()
