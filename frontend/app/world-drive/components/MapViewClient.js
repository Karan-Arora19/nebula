'use client'

import { useEffect, useRef, useState, useMemo } from 'react'
import L from 'leaflet'

export default function MapView({
  currentPosition,
  destination,
  startPoint,
  waypoints = [],
  status,
}) {
  const containerRef = useRef(null)
  const mapRef = useRef(null)
  const markersRef = useRef({})
  const polylinesRef = useRef({})
  const [followCar, setFollowCar] = useState(true)

  const routeCoordinates = useMemo(() => {
    return [
      [startPoint.lat, startPoint.lng],
      ...waypoints.map(wp => [wp.lat, wp.lng]),
      [destination.lat, destination.lng],
    ]
  }, [startPoint, waypoints, destination])

  // Initialize map once
  useEffect(() => {
    if (!containerRef.current || mapRef.current) return

    const initialCenter = currentPosition 
      ? [currentPosition.lat, currentPosition.lng]
      : [startPoint.lat, startPoint.lng]

    mapRef.current = L.map(containerRef.current, {
      center: initialCenter,
      zoom: 14,
      zoomControl: false,
    })

    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
    }).addTo(mapRef.current)

    // Start marker
    const startMarker = L.divIcon({
      className: 'start-marker',
      html: `
        <div style="
          background: #34A853;
          border-radius: 50% 50% 50% 0;
          transform: rotate(-45deg);
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 6px rgba(52, 168, 83, 0.5);
          border: 2px solid white;
        ">
          <span style="transform: rotate(45deg); font-size: 12px;">📍</span>
        </div>
      `,
      iconSize: [28, 28],
      iconAnchor: [14, 28],
    })
    markersRef.current.start = L.marker([startPoint.lat, startPoint.lng], { icon: startMarker })
      .addTo(mapRef.current)
      .bindPopup('<strong>🚀 Start Point</strong><br/>Journey begins here')

    // Destination marker
    const destMarker = L.divIcon({
      className: 'dealership-marker',
      html: `
        <div style="
          background: #EA4335;
          border-radius: 50% 50% 50% 0;
          transform: rotate(-45deg);
          width: 36px;
          height: 36px;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 6px rgba(234, 67, 53, 0.5);
          border: 2px solid white;
        ">
          <span style="transform: rotate(45deg); font-size: 16px;">🏁</span>
        </div>
      `,
      iconSize: [36, 36],
      iconAnchor: [18, 36],
    })
    markersRef.current.destination = L.marker([destination.lat, destination.lng], { icon: destMarker })
      .addTo(mapRef.current)
      .bindPopup('<strong>🏁 Dealership</strong><br/>Destination')

    // Initial route polyline
    polylinesRef.current.route = L.polyline(routeCoordinates, {
      color: '#4285F4',
      weight: 5,
      opacity: 0.9,
    }).addTo(mapRef.current)

    polylinesRef.current.routeGlow = L.polyline(routeCoordinates, {
      color: '#4285F4',
      weight: 10,
      opacity: 0.15,
    }).addTo(mapRef.current)

    polylinesRef.current.completed = L.polyline([], {
      color: '#34A853',
      weight: 5,
      opacity: 0.8,
    }).addTo(mapRef.current)

    // Add car marker - Porsche 911 style with motion effect
    const carIcon = L.divIcon({
      className: 'vehicle-marker',
      html: `
        <div style="
          display: flex;
          align-items: center;
          justify-content: center;
          transform: rotate(-90deg);
        ">
          <svg width="56" height="28" viewBox="0 0 56 28" fill="none" xmlns="http://www.w3.org/2000/svg">
            <!-- Motion lines -->
            <line x1="0" y1="10" x2="8" y2="10" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" opacity="0.6"/>
            <line x1="2" y1="14" x2="10" y2="14" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" opacity="0.8"/>
            <line x1="0" y1="18" x2="8" y2="18" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" opacity="0.6"/>
            
            <!-- Porsche 911 Body -->
            <path d="M14 18C14 18 15 16 18 16H21L24 11C24 11 26 9 29 9H39C42 9 44 10.5 46 12L50 16H52C53.5 16 55 17 55 18V20C55 21 54 22 52 22H50C50 24 48 26 46 26C44 26 42 24 42 22H24C24 24 22 26 20 26C18 26 16 24 16 22H15C13.5 22 12 21 12 20V18C12 17 13 16 14 16Z" fill="#dc2626"/>
            
            <!-- Car top/roof -->
            <path d="M25 11.5L22 15.5H29V10C27.5 10 26 10.5 25 11.5Z" fill="#1e293b" opacity="0.9"/>
            <path d="M31 10V15.5H42L46 13C44.5 11 42.5 10 40 10H31Z" fill="#1e293b" opacity="0.9"/>
            
            <!-- Headlights -->
            <ellipse cx="52" cy="17" rx="1.5" ry="1" fill="#fef08a"/>
            <ellipse cx="15" cy="19" rx="1" ry="0.8" fill="#fca5a5"/>
            
            <!-- Front wheel -->
            <circle cx="46" cy="22" r="3.5" fill="#1e293b"/>
            <circle cx="46" cy="22" r="2" fill="#475569"/>
            <circle cx="46" cy="22" r="0.8" fill="#94a3b8"/>
            
            <!-- Rear wheel -->
            <circle cx="20" cy="22" r="3.5" fill="#1e293b"/>
            <circle cx="20" cy="22" r="2" fill="#475569"/>
            <circle cx="20" cy="22" r="0.8" fill="#94a3b8"/>
            
            <!-- Side mirror -->
            <ellipse cx="23" cy="13" rx="1.5" ry="1" fill="#dc2626"/>
            
            <!-- Door line -->
            <path d="M33 15.5V20" stroke="#b91c1c" stroke-width="0.5"/>
          </svg>
        </div>
      `,
      iconSize: [56, 28],
      iconAnchor: [28, 14],
      popupAnchor: [0, -14],
    })
    
    const initialPos = currentPosition 
      ? [currentPosition.lat, currentPosition.lng]
      : [startPoint.lat, startPoint.lng]
    
    markersRef.current.car = L.marker(initialPos, { icon: carIcon })
      .addTo(mapRef.current)
      .bindPopup('<strong>🚗 Your Vehicle</strong>')

    return () => {
      if (mapRef.current) {
        mapRef.current.remove()
        mapRef.current = null
      }
    }
  }, []) // Empty deps - only run once

  // Update car position
  useEffect(() => {
    if (!mapRef.current) return
    
    console.log('Car marker update - currentPosition:', currentPosition)

    // If no position, remove car marker
    if (!currentPosition) {
      if (markersRef.current.car) {
        mapRef.current.removeLayer(markersRef.current.car)
        markersRef.current.car = null
      }
      return
    }

    const pos = [currentPosition.lat, currentPosition.lng]
    console.log('Setting car at position:', pos)

    // Create car icon
    const carIcon = L.divIcon({
      className: 'vehicle-marker-animated',
      html: `
        <div style="
          position: relative;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 80px;
          height: 80px;
        ">
          <!-- Outer pulsing ring -->
          <div style="
            position: absolute;
            width: 80px;
            height: 80px;
            background: rgba(59, 130, 246, 0.3);
            border-radius: 50%;
            animation: vehiclePulse 2s ease-in-out infinite;
          "></div>
          <!-- Shadow -->
          <div style="
            position: absolute;
            bottom: 8px;
            width: 40px;
            height: 10px;
            background: rgba(0, 0, 0, 0.3);
            border-radius: 50%;
            filter: blur(4px);
          "></div>
          <!-- Main car circle -->
          <div style="
            width: 54px;
            height: 54px;
            background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 6px 20px rgba(59, 130, 246, 0.6);
            border: 4px solid white;
            animation: vehicleBounce 1s ease-in-out infinite;
            z-index: 1;
          ">
            <span style="font-size: 28px;">🚗</span>
          </div>
        </div>
        <style>
          @keyframes vehiclePulse {
            0%, 100% { 
              transform: scale(1); 
              opacity: 0.6; 
            }
            50% { 
              transform: scale(1.3); 
              opacity: 0.1; 
            }
          }
          @keyframes vehicleBounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-4px); }
          }
          @keyframes vehiclePing {
            0% { transform: scale(1); opacity: 1; }
            75%, 100% { transform: scale(1.5); opacity: 0; }
          }
        </style>
      `,
      iconSize: [80, 80],
      iconAnchor: [40, 40],
      popupAnchor: [0, -30],
    })

    // Create or update car marker
    if (!markersRef.current.car) {
      console.log('Creating new car marker')
      markersRef.current.car = L.marker(pos, { icon: carIcon })
        .addTo(mapRef.current)
        .bindPopup(`
          <div style="text-align: center; padding: 8px;">
            <div style="font-size: 32px; margin-bottom: 4px;">🚗</div>
            <p style="margin: 0; font-weight: bold; color: #1f2937;">Your Vehicle</p>
            <p style="margin: 4px 0 0 0; font-size: 12px; color: #6b7280;">Status: ${status}</p>
            <p style="margin: 4px 0 0 0; font-size: 10px; color: #9ca3af;">
              ${currentPosition.lat.toFixed(6)}, ${currentPosition.lng.toFixed(6)}
            </p>
          </div>
        `)
    } else {
      console.log('Updating car marker position')
      markersRef.current.car.setLatLng(pos)
      markersRef.current.car.setIcon(carIcon)
    }

    // Follow car if enabled
    if (followCar && status === 'IN_PROGRESS') {
      mapRef.current.setView(pos, mapRef.current.getZoom(), { animate: true, duration: 0.5 })
    }

    // Update completed route
    const findNearestIdx = () => {
      let minDist = Infinity
      let nearestIdx = 0
      routeCoordinates.forEach((coord, idx) => {
        const dist = Math.sqrt(
          Math.pow(coord[0] - currentPosition.lat, 2) + Math.pow(coord[1] - currentPosition.lng, 2)
        )
        if (dist < minDist) {
          minDist = dist
          nearestIdx = idx
        }
      })
      return nearestIdx
    }

    const nearestIdx = findNearestIdx()
    const completedCoords = [...routeCoordinates.slice(0, nearestIdx + 1), pos]
    const remainingCoords = [pos, ...routeCoordinates.slice(nearestIdx)]

    polylinesRef.current.completed?.setLatLngs(completedCoords)
    polylinesRef.current.route?.setLatLngs(remainingCoords)
    polylinesRef.current.routeGlow?.setLatLngs(remainingCoords)

  }, [currentPosition, status, followCar, routeCoordinates])

  const handleZoomIn = () => mapRef.current?.zoomIn()
  const handleZoomOut = () => mapRef.current?.zoomOut()

  return (
    <div className="h-full pt-4 px-16 pb-16 flex flex-col bg-gray-900">
      {/* Title with neon sign effect */}
      <div className="text-center mb-2">
        <h2 
          className="text-4xl font-bold tracking-widest uppercase neon-text" 
          style={{ 
            fontFamily: '"Roboto", sans-serif', 
            letterSpacing: '0.3em',
            fontWeight: 700,
            color: '#fff',
            textShadow: `
              0 0 4px #fff,
              0 0 11px #fff,
              0 0 19px #fff,
              0 0 40px #0ff,
              0 0 80px #0ff,
              0 0 90px #0ff,
              0 0 100px #0ff,
              0 0 150px #0ff
            `
          }}
        >
          World Drive View
        </h2>
      </div>

      {/* Map frame with 3D styling */}
      <div className="flex-1 relative">
        {/* Outer glow */}
        <div className="absolute inset-0 bg-gradient-to-br from-red-600/20 via-rose-500/10 to-pink-500/20 rounded-3xl blur-xl" />
        
        {/* Main frame container */}
        <div className="relative h-full bg-gradient-to-br from-gray-800 via-gray-700 to-gray-800 rounded-3xl p-1.5 shadow-2xl">
          {/* Inner frame with bevel effect */}
          <div className="h-full bg-gradient-to-br from-gray-600 to-gray-700 rounded-[22px] p-[3px]">
            {/* White map container with inner shadow */}
            <div className="h-full bg-white rounded-[19px] relative overflow-hidden shadow-inner">
              <div ref={containerRef} className="w-full h-full" />

              <button
                className={`absolute bottom-4 right-4 z-[1000] px-4 py-2 rounded-lg font-semibold text-sm transition-all shadow-md ${
                  followCar 
                    ? 'bg-red-600 text-white' 
                    : 'bg-white text-gray-700 border border-gray-200'
                }`}
                onClick={() => setFollowCar(!followCar)}
              >
                {followCar ? '📍 Following' : '🗺️ Free View'}
              </button>

              <div className="absolute top-4 right-4 z-[1000] flex flex-col gap-2">
                <button
                  className="w-10 h-10 bg-white hover:bg-gray-100 rounded-lg flex items-center justify-center text-gray-700 font-bold shadow-md border border-gray-200"
                  onClick={handleZoomIn}
                >
                  +
                </button>
                <button
                  className="w-10 h-10 bg-white hover:bg-gray-100 rounded-lg flex items-center justify-center text-gray-700 font-bold shadow-md border border-gray-200"
                  onClick={handleZoomOut}
                >
                  −
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
