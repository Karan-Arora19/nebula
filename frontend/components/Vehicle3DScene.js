'use client'

import React, { Suspense, useState, useMemo } from 'react'
import { Canvas } from '@react-three/fiber'
import { OrbitControls, Environment, ContactShadows, PerspectiveCamera, Html } from '@react-three/drei'
import CarModel from './CarModel'
import * as THREE from 'three'

// Color mapping - moved outside component to prevent recreation on every render
// This is a major performance optimization
const COLOR_MAP = {
  'racing-red': { color: new THREE.Color(0xdc2626), metalness: 0.9, roughness: 0.1 },
  'midnight-black': { color: new THREE.Color(0x0a0a0a), metalness: 0.8, roughness: 0.2 },
  'pearl-white': { color: new THREE.Color(0xf5f5f5), metalness: 0.7, roughness: 0.2 },
  'ocean-blue': { color: new THREE.Color(0x2563eb), metalness: 0.9, roughness: 0.1 },
  'silver-metallic': { color: new THREE.Color(0xc0c0c0), metalness: 0.95, roughness: 0.05 },
  'sunset-orange': { color: new THREE.Color(0xf97316), metalness: 0.9, roughness: 0.1 },
  'electric-green': { color: new THREE.Color(0x10b981), metalness: 0.88, roughness: 0.12 },
}

// Loading component for 3D scene - adapted to Nebula red theme
function Loader() {
  return (
    <Html center>
      <div className="flex flex-col items-center gap-4">
        <div className="relative">
          <div className="w-16 h-16 border-4 border-red-500/30 border-t-red-500 rounded-full animate-spin"></div>
          <div className="absolute inset-0 w-16 h-16 border-4 border-red-400/20 border-b-red-400 rounded-full animate-spin" style={{ animationDirection: 'reverse', animationDuration: '1.5s' }}></div>
        </div>
        <div className="bg-black/90 backdrop-blur-md px-6 py-3 rounded-xl border border-red-500/50">
          <p className="text-white text-sm font-semibold">Loading 3D Model...</p>
          <p className="text-gray-400 text-xs mt-1">Preparing realistic rendering</p>
        </div>
      </div>
    </Html>
  )
}

// Fallback component when no model is available
function NoModelFallback() {
  return (
    <Html center>
      <div className="bg-black/95 backdrop-blur-md px-8 py-6 rounded-2xl border-2 border-yellow-500/50 max-w-md">
        <div className="text-center">
          <div className="text-6xl mb-4">🚗</div>
          <h3 className="text-white text-xl font-bold mb-3">3D Model Not Found</h3>
          <p className="text-gray-300 text-sm mb-4 leading-relaxed">
            Please download a 3D car model and place it at:
          </p>
          <code className="block bg-gray-900 text-green-400 px-4 py-2 rounded text-xs mb-4">
            /public/models/furarri.glb
          </code>
          <p className="text-gray-400 text-xs">
            📖 See documentation for download instructions
          </p>
        </div>
      </div>
    </Html>
  )
}

// Scene lighting setup
function SceneLighting() {
  return (
    <>
      {/* Ambient light for overall illumination */}
      <ambientLight intensity={0.4} />
      
      {/* Main key light (front-right) - Reduced shadow quality for performance */}
      <directionalLight
        position={[5, 8, 5]}
        intensity={1.5}
        castShadow
        shadow-mapSize-width={1024}
        shadow-mapSize-height={1024}
        shadow-bias={-0.0001}
      />
      
      {/* Fill light (front-left) */}
      <directionalLight
        position={[-5, 4, 3]}
        intensity={0.8}
        color="#ffffff"
      />
      
      {/* Back rim light for edge definition */}
      <directionalLight
        position={[0, 3, -5]}
        intensity={0.5}
        color="#ff4040"
      />
      
      {/* Spotlight for dramatic effect */}
      <spotLight
        position={[10, 15, 10]}
        angle={0.3}
        penumbra={1}
        intensity={1}
        castShadow
      />
    </>
  )
}

/**
 * Vehicle3DScene Component
 * 
 * Renders the 3D interactive car viewer using React Three Fiber.
 * Displays the car model with real-time color/material updates based on configuration.
 * 
 * @param {Object} props
 * @param {string} props.vehicleName - Name of the vehicle to display
 * @param {Object} props.configuration - Current vehicle configuration
 */
/**
 * Vehicle3DScene Component
 * 
 * Renders the 3D interactive car viewer using React Three Fiber.
 * Displays the car model with real-time color/material updates based on configuration.
 * 
 * @param {Object} props
 * @param {string} props.vehicleName - Name of the vehicle to display
 * @param {Object} props.configuration - Current vehicle configuration
 * @param {string} props.modelPath - Path to the 3D car model
 * @param {Function} props.onModelLoad - Callback when model finishes loading
 */
export default function Vehicle3DScene({ vehicleName, configuration, modelPath, onModelLoad }) {
  const [modelError, setModelError] = useState(false)

  // Get paint color from configuration - memoized to prevent unnecessary recalculations
  const paintColor = configuration.paint || 'racing-red'
  
  // Memoize material lookup - only recalculate when paintColor changes
  const currentMaterial = useMemo(() => {
    return COLOR_MAP[paintColor] || COLOR_MAP['racing-red']
  }, [paintColor])

  return (
    <div className="w-full h-full relative bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Ambient background glow */}
      <div className="absolute inset-0 bg-gradient-to-b from-black/50 via-transparent to-black/50 pointer-events-none"></div>
      
      {/* 3D Canvas - Optimized settings for better performance */}
      <Canvas
        shadows
        gl={{
          antialias: true,
          toneMapping: THREE.ACESFilmicToneMapping,
          toneMappingExposure: 1.2,
          outputColorSpace: THREE.SRGBColorSpace,
          powerPreference: "high-performance", // Prefer GPU performance
          stencil: false, // Disable stencil buffer for better performance
          depth: true,
        }}
        dpr={[1, 2]} // Limit pixel ratio for better performance on high-DPI displays
        performance={{ min: 0.5 }} // Reduce quality if FPS drops below 30
        className="bg-gradient-to-b from-black via-gray-900 to-black"
      >
        {/* Camera setup */}
        <PerspectiveCamera makeDefault position={[4, 2, 6]} fov={50} />

        {/* Scene lighting */}
        <SceneLighting />

        {/* Environment map for realistic reflections */}
        <Environment
          preset="city"
          background={false}
          blur={0.8}
        />

        {/* Suspense boundary for async model loading */}
        <Suspense fallback={<Loader />}>
          {/* Main car model (with automatic fallback) - Key forces remount on model change */}
          <CarModel
            key={modelPath}
            modelPath={modelPath}
            configuration={configuration}
            paintMaterial={currentMaterial}
            onError={() => setModelError(true)}
            onLoad={onModelLoad}
          />
          
          {/* Contact shadows for realism */}
          <ContactShadows
            position={[0, -0.5, 0]}
            opacity={0.5}
            scale={15}
            blur={2}
            far={4}
          />

          {/* Orbit controls for 360° viewing - Optimized for performance */}
          <OrbitControls
            enablePan={true}
            enableZoom={true}
            enableRotate={true}
            minDistance={3}
            maxDistance={15}
            minPolarAngle={Math.PI / 6}
            maxPolarAngle={Math.PI / 2}
            autoRotate={false}
            dampingFactor={0.1}
            rotateSpeed={0.5}
            target={[0, 0.5, 0]}
            makeDefault
          />
        </Suspense>
      </Canvas>

      {/* Status badges overlay - Enhanced Red theme */}
      <div className="absolute top-6 left-6 bg-black/80 backdrop-blur-md px-5 py-3 rounded-xl border-2 border-red-500/50 flex items-center gap-2 shadow-2xl shadow-red-500/20 pointer-events-none z-20">
        <span className="relative flex h-3 w-3">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-3 w-3 bg-red-500"></span>
        </span>
        <span className="text-white text-xs font-bold tracking-wider">3D REAL-TIME</span>
        <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-red-500/10 to-transparent pointer-events-none"></div>
      </div>

      <div className="absolute top-6 right-6 bg-black/80 backdrop-blur-md px-5 py-2.5 rounded-xl border-2 border-red-500/50 shadow-2xl shadow-red-500/20 pointer-events-none z-20">
        <span className="text-red-300 text-xs font-semibold tracking-wider">
          {paintColor.replace(/-/g, ' ').toUpperCase()}
        </span>
        <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-red-500/10 to-transparent pointer-events-none"></div>
      </div>

      {/* Controls hint - Enhanced */}
      <div className="absolute bottom-6 left-1/2 transform -translate-x-1/2 bg-black/80 backdrop-blur-md px-8 py-3 rounded-xl border border-red-500/30 shadow-xl pointer-events-none z-20">
        <p className="text-gray-200 text-xs font-medium">
          🖱️ <strong className="text-red-300">Drag</strong> to rotate • <strong className="text-red-300">Scroll</strong> to zoom • <strong className="text-red-300">Right-click</strong> to pan
        </p>
      </div>
    </div>
  )
}
