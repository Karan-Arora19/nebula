"use client";

/**
 * Vehicle3DScene - Interactive 3D Car Viewer
 * 
 * Renders a 3D car using React Three Fiber with orbit controls.
 * Users can rotate, zoom, and pan around the car model.
 * See CAR-CONFIGURATOR-DOCS.md for detailed Three.js explanations.
 */

import React, { Suspense, useMemo } from "react";
import { Canvas } from "@react-three/fiber";
import {
  OrbitControls,
  Environment,
  ContactShadows,
  PerspectiveCamera,
} from "@react-three/drei";
import CarModel from "./CarModel";
import * as THREE from "three";
import {
  vehicle3DScenePropTypes,
  vehicle3DSceneDefaultProps,
} from "./propTypes/Vehicle3DScene.propTypes";

// Paint color material definitions (maps color ID to Three.js material props)
const COLOR_MAP = {
  "racing-red": { color: new THREE.Color(0xdc2626), metalness: 0.9, roughness: 0.1 },
  "midnight-black": { color: new THREE.Color(0x0a0a0a), metalness: 0.8, roughness: 0.2 },
  "pearl-white": { color: new THREE.Color(0xf5f5f5), metalness: 0.7, roughness: 0.2 },
  "ocean-blue": { color: new THREE.Color(0x2563eb), metalness: 0.9, roughness: 0.1 },
  "silver-metallic": { color: new THREE.Color(0xc0c0c0), metalness: 0.95, roughness: 0.05 },
  "sunset-orange": { color: new THREE.Color(0xf97316), metalness: 0.9, roughness: 0.1 },
  "electric-green": { color: new THREE.Color(0x10b981), metalness: 0.88, roughness: 0.12 },
};

// Three-point lighting setup for realistic car rendering
function SceneLighting() {
  return (
    <>
      {/* Base ambient light */}
      <ambientLight intensity={0.4} />
      
      {/* Key light - main shadow-casting light */}
      <directionalLight
        position={[5, 8, 5]}
        intensity={1.5}
        castShadow
        shadow-mapSize-width={1024}
        shadow-mapSize-height={1024}
        shadow-bias={-0.0001}
      />
      
      {/* Fill light - softens shadows */}
      <directionalLight position={[-5, 4, 3]} intensity={0.8} color="#ffffff" />
      
      {/* Rim light - red edge glow for brand theme */}
      <directionalLight position={[0, 3, -5]} intensity={0.5} color="#ff4040" />
      
      {/* Dramatic spotlight */}
      <spotLight position={[10, 15, 10]} angle={0.3} penumbra={1} intensity={1} castShadow />
    </>
  );
}

export default function Vehicle3DScene({ configuration, modelPath, onModelLoad }) {
  const paintColor = configuration.paint || "racing-red";
  
  // Memoize material lookup for performance
  const currentMaterial = useMemo(() => {
    return COLOR_MAP[paintColor] || COLOR_MAP["racing-red"];
  }, [paintColor]);

  return (
    <div className="w-full h-full relative bg-gradient-to-br from-gray-900 via-black to-gray-900">
      <div className="absolute inset-0 bg-gradient-to-b from-black/50 via-transparent to-black/50 pointer-events-none"></div>

      {/* 3D Canvas - WebGL rendering context */}
      <Canvas
        shadows
        frameloop="demand"
        gl={{
          antialias: true,
          toneMapping: THREE.ACESFilmicToneMapping,
          toneMappingExposure: 1.2,
          outputColorSpace: THREE.SRGBColorSpace,
          powerPreference: "high-performance",
          stencil: false,
          depth: true,
        }}
        dpr={[1, 1.5]}
        performance={{ min: 0.5 }}
        className="bg-gradient-to-b from-black via-gray-900 to-black"
      >
        {/* Camera: front-left view of car */}
        <PerspectiveCamera makeDefault position={[-6, 2, -2]} fov={50} />

        <SceneLighting />
        
        {/* Environment map for realistic reflections */}
        <Environment preset="city" background={false} blur={0.8} />

        <Suspense fallback={null}>
          <CarModel
            key={modelPath}
            modelPath={modelPath}
            configuration={configuration}
            paintMaterial={currentMaterial}
            onLoad={onModelLoad}
          />

          {/* Ground shadow for visual grounding */}
          <ContactShadows position={[0, -0.5, 0]} opacity={0.5} scale={15} blur={2} far={4} />

          {/* Mouse/touch controls: drag=rotate, scroll=zoom, right-drag=pan */}
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

      {/* UI Overlays */}
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
          {paintColor.replaceAll("-", " ").toUpperCase()}
        </span>
        <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-red-500/10 to-transparent pointer-events-none"></div>
      </div>

      <div className="absolute bottom-6 left-1/2 transform -translate-x-1/2 bg-black/80 backdrop-blur-md px-8 py-3 rounded-xl border border-red-500/30 shadow-xl pointer-events-none z-20">
        <p className="text-gray-200 text-xs font-medium">
          🖱️ <strong className="text-red-300">Drag</strong> to rotate •{" "}
          <strong className="text-red-300">Scroll</strong> to zoom •{" "}
          <strong className="text-red-300">Right-click</strong> to pan
        </p>
      </div>
    </div>
  );
}

Vehicle3DScene.propTypes = vehicle3DScenePropTypes;
Vehicle3DScene.defaultProps = vehicle3DSceneDefaultProps;