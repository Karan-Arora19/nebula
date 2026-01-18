'use client'

import { Car, Clock, Loader2, CheckCircle2, Gauge, Navigation, Timer, MapPin } from 'lucide-react'

function formatDistance(meters) {
  if (meters >= 1000) return `${(meters / 1000).toFixed(1)} km`
  return `${Math.round(meters)} m`
}

function formatEta(seconds) {
  if (!Number.isFinite(seconds) || seconds <= 0) return '—'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  if (mins >= 60) {
    const hrs = Math.floor(mins / 60)
    const rem = mins % 60
    return `${hrs}h ${rem}m`
  }
  return `${mins}m ${secs}s`
}

function getStatusInfo(status, isConnecting) {
  if (isConnecting) {
    return {
      badge: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30',
      text: 'Connecting...',
      icon: <Loader2 className="w-4 h-4 animate-spin" />,
    }
  }

  switch (status) {
    case 'WAITING':
      return {
        badge: 'bg-gray-500/20 text-gray-400 border-gray-500/30',
        text: 'Waiting',
        icon: <Clock className="w-4 h-4" />,
      }
    case 'IN_PROGRESS':
      return {
        badge: 'bg-green-500/20 text-green-400 border-green-500/30',
        text: 'In Progress',
        icon: <Car className="w-4 h-4" />,
      }
    case 'COMPLETED':
      return {
        badge: 'bg-purple-500/20 text-purple-400 border-purple-500/30',
        text: 'Completed',
        icon: <CheckCircle2 className="w-4 h-4" />,
      }
    default:
      return {
        badge: 'bg-gray-500/20 text-gray-400 border-gray-500/30',
        text: 'No Journey',
        icon: <Clock className="w-4 h-4" />,
      }
  }
}

export default function JourneyStatusDisplay({
  status = 'WAITING',
  progress = 0,
  speedMps = 0,
  distanceRemainingMeters = 0,
  etaSeconds = 0,
  routeName,
  isConnecting = false,
}) {
  const speedKmh = Math.max(0, speedMps) * 3.6
  const statusInfo = getStatusInfo(status, isConnecting)
  const progressInt = Math.round(progress)

  return (
    <div className="space-y-3">
      {/* Header with Mac-style location icon */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          {/* Mac-style location pin */}
          <div className="w-8 h-8 bg-gradient-to-br from-red-600 to-red-700 rounded-full flex items-center justify-center shadow-lg shadow-red-600/30">
            <MapPin className="w-4 h-4 text-white" />
          </div>
          <h3 className="text-lg font-bold text-white">Live Journey</h3>
        </div>
        <span className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold border backdrop-blur-sm ${statusInfo.badge}`}>
          {statusInfo.icon}
          {statusInfo.text}
        </span>
      </div>

      {/* Route name pill */}
      {routeName && (
        <div className="flex items-center gap-2 px-3 py-2 bg-gray-700/50 rounded-lg border border-gray-600/50">
          <Navigation className="w-4 h-4 text-red-600" />
          <span className="text-sm font-medium text-white">{routeName}</span>
        </div>
      )}

      {/* Dynamic Progress bar */}
      {(status === 'IN_PROGRESS' || status === 'COMPLETED') && (
        <div className="bg-gray-700/50 rounded-xl p-3 border border-gray-600/50">
          <div className="flex justify-between items-center mb-2">
            <span className="text-xs font-medium text-gray-400 uppercase tracking-wide">Progress</span>
            <span className="text-lg font-bold text-white">{progressInt}%</span>
          </div>
          <div className="relative h-3 bg-gray-800 rounded-full overflow-hidden">
            {/* Animated background shimmer */}
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent animate-pulse" />
            {/* Progress fill with gradient */}
            <div
              className="absolute inset-y-0 left-0 bg-gradient-to-r from-red-600 via-red-500 to-red-400 rounded-full transition-all duration-700 ease-out"
              style={{ width: `${progress}%` }}
            >
              {/* Moving shine effect */}
              <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent animate-shimmer" />
            </div>
            {/* Progress indicator dot */}
            {progress > 0 && progress < 100 && (
              <div
                className="absolute top-1/2 -translate-y-1/2 w-4 h-4 bg-white rounded-full shadow-lg shadow-white/50 border-2 border-green-400 transition-all duration-700"
                style={{ left: `calc(${progress}% - 8px)` }}
              />
            )}
          </div>
        </div>
      )}

      {/* Stats grid - larger boxes */}
      {(status === 'IN_PROGRESS' || status === 'COMPLETED') && (
        <div className="grid grid-cols-3 gap-2">
          {/* Speed */}
          <div className="bg-gradient-to-br from-gray-700 to-gray-800 rounded-xl p-4 border border-gray-600/50 flex flex-col items-center">
            <div className="w-12 h-12 bg-orange-500/20 rounded-full flex items-center justify-center mb-2">
              <Gauge className="w-6 h-6 text-orange-400" />
            </div>
            <div className="text-2xl font-bold text-white">{speedKmh.toFixed(0)}</div>
            <div className="text-xs text-gray-400 font-medium">km/h</div>
          </div>

          {/* Distance */}
          <div className="bg-gradient-to-br from-gray-700 to-gray-800 rounded-xl p-4 border border-gray-600/50 flex flex-col items-center">
            <div className="w-12 h-12 bg-red-500/20 rounded-full flex items-center justify-center mb-2">
              <Navigation className="w-6 h-6 text-red-600" />
            </div>
            <div className="text-2xl font-bold text-white">{formatDistance(distanceRemainingMeters)}</div>
            <div className="text-xs text-gray-400 font-medium">remaining</div>
          </div>

          {/* ETA */}
          <div className="bg-gradient-to-br from-gray-700 to-gray-800 rounded-xl p-4 border border-gray-600/50 flex flex-col items-center">
            <div className="w-12 h-12 bg-green-500/20 rounded-full flex items-center justify-center mb-2">
              <Timer className="w-6 h-6 text-green-400" />
            </div>
            <div className="text-2xl font-bold text-white">{formatEta(etaSeconds)}</div>
            <div className="text-xs text-gray-400 font-medium">ETA</div>
          </div>
        </div>
      )}

      {/* Waiting state - compact */}
      {(status === 'WAITING' || status === 'NOT_STARTED') && !isConnecting && (
            <div className="flex items-center justify-center gap-3 py-4 bg-gray-700/30 rounded-xl border border-gray-600/30">
          <div className="relative">
            <div className="w-10 h-10 border-3 border-gray-600 border-t-red-600 rounded-full animate-spin" />
            <Car className="w-4 h-4 text-red-600 absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2" />
          </div>
          <p className="text-sm text-gray-400">Waiting for journey...</p>
        </div>
      )}

      {/* Loading state - compact */}
      {isConnecting && (
        <div className="flex items-center justify-center gap-3 py-4 bg-gray-700/30 rounded-xl border border-gray-600/30">
          <Loader2 className="w-8 h-8 text-red-600 animate-spin" />
          <p className="text-sm text-gray-400">Connecting...</p>
        </div>
      )}
    </div>
  )
}
