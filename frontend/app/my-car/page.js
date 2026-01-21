'use client'

import { Car, Fuel, Navigation, MapPin } from 'lucide-react'
import { useMQTT } from '@/hooks/useMQTT'
import { useUserVehicleInfo } from '@/hooks/useUserVehicleInfo'
import { useAuth } from '@/hooks/useAuth'

export default function MyCarPage() {
  const { user } = useAuth()
  // MQTT topic: nebula/user/{userId}/vehicle/info
  const vehicleInfoMqtt = useMQTT(user?.id ? `nebula/user/${user.id}/vehicle/info` : null)
  // REST fallback/API
  const { vehicleInfo, loading } = useUserVehicleInfo()

  // Prefer MQTT if available, else fallback to REST
  const info = vehicleInfoMqtt || vehicleInfo
  const carLocation = info?.carLocation || null

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">My Nebula Car</h1>
        
        <div className="grid md:grid-cols-2 gap-8">
          <div className="bg-gray-800 rounded-lg p-8">
            <h2 className="text-2xl font-bold mb-6">My Nebula Velocity</h2>
            <div className="aspect-video bg-gray-700 rounded-lg mb-6 flex items-center justify-center">
              <Car className="w-32 h-32 text-red-500" />
            </div>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Maintenance Due</span>
                <span className="font-semibold">{info?.maintenanceDueDate ? info.maintenanceDueDate : 'Loading...'}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Tyre Pressures</span>
                <span className="font-mono text-sm">
                  {info?.tyrePressures
                    ? `FL: ${info.tyrePressures.frontLeft} | FR: ${info.tyrePressures.frontRight} | RL: ${info.tyrePressures.rearLeft} | RR: ${info.tyrePressures.rearRight}`
                    : 'Loading...'}
                </span>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            <div className="bg-gray-800 rounded-lg p-8">
              <div className="flex items-center gap-3 mb-4">
                <Fuel className="w-6 h-6 text-red-500" />
                <h3 className="text-2xl font-bold">Fuel Level</h3>
              </div>
              <div className="relative pt-1">
                <div className="flex mb-2 items-center justify-between">
                  <div className="text-3xl font-bold">
                    {info?.fuelLevel ? `${info.fuelLevel.toFixed(1)}%` : 'Loading...'}
                  </div>
                  <span className="text-sm text-gray-400">Live Data</span>
                </div>
                <div className="overflow-hidden h-4 mb-4 text-xs flex rounded-full bg-gray-700">
                  <div
                    style={{ width: `${info?.fuelLevel || 0}%` }}
                    className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-gradient-to-r from-red-600 to-red-400 transition-all duration-1000"
                  ></div>
                </div>
              </div>
            </div>

            <div className="bg-gray-800 rounded-lg p-8">
              <div className="flex items-center gap-3 mb-4">
                <Navigation className="w-6 h-6 text-red-500" />
                <h3 className="text-2xl font-bold">Current Position</h3>
              </div>
              <div className="aspect-video bg-gray-700 rounded-lg mb-4 relative overflow-hidden">
                <div className="absolute inset-0 bg-[url('https://api.mapbox.com/styles/v1/mapbox/dark-v10/static/9.1829,48.7758,13,0/600x400@2x?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw')] bg-cover bg-center"></div>
                <div className="absolute inset-0 flex items-center justify-center">
                  <MapPin className="w-8 h-8 text-red-500 animate-bounce" />
                </div>
              </div>
              <div className="text-center">
                <p className="text-sm text-gray-400 mb-1">GPS Coordinates</p>
                <p className="font-mono text-lg">
                  {carLocation ? `${carLocation.lat.toFixed(4)}°N, ${carLocation.lng.toFixed(4)}°E` : 'Loading...'}
                </p>
                <p className="text-sm text-green-500 mt-2">● Live Tracking Active</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}