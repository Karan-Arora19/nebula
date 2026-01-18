'use client'

import React, { useRef, useEffect, useState } from 'react'
import { useFrame } from '@react-three/fiber'
import { useGLTF } from '@react-three/drei'
import * as THREE from 'three'
import FallbackCar from './FallbackCar'

/**
 * CarModel Component
 * 
 * Loads and renders a 3D car model with material customization.
 * 
 * @param {Object} props
 * @param {string} props.modelPath - Path to the 3D model file
 * @param {Object} props.configuration - Vehicle configuration
 * @param {Object} props.paintMaterial - Paint material properties
 * @param {Function} props.onError - Error callback
 * @param {Function} props.onLoad - Load complete callback
 */
export default function CarModel({ modelPath, configuration, paintMaterial, onError, onLoad }) {
  const groupRef = useRef()
  const [hasError, setHasError] = useState(false)

  // Load the 3D model - useGLTF handles errors internally
  const { scene } = useGLTF(modelPath || '/models/furarri.glb', true, true, (loader) => {
    loader.manager.onError = (url) => {
      console.error('Failed to load model:', url)
      setHasError(true)
      onError?.()
    }
  })

  // Notify when model is loaded
  useEffect(() => {
    if (scene && !hasError) {
      // Small delay to ensure model is fully rendered
      const timer = setTimeout(() => {
        onLoad?.()
      }, 100)
      return () => clearTimeout(timer)
    }
  }, [scene, hasError, onLoad, modelPath])

  // Apply materials to the car - Memoized to prevent unnecessary recalculations
  useEffect(() => {
    if (!scene || !paintMaterial) return

    // Create material once outside the loop for better performance
    const bodyMaterial = new THREE.MeshStandardMaterial({
      color: paintMaterial.color.clone(), // Clone to avoid reference issues
      metalness: paintMaterial.metalness,
      roughness: paintMaterial.roughness,
      envMapIntensity: 1.5,
      side: THREE.FrontSide,
    })

    scene.traverse((child) => {
      if (child.isMesh) {
        // Get mesh name and check original material properties
        const name = child.name.toLowerCase()
        const originalMat = child.material
          
          // Detect glass by material properties (transparency, transmission)
          const isGlass = originalMat && (
            originalMat.transparent === true || 
            originalMat.transmission > 0 ||
            originalMat.opacity < 0.9 ||
            name.includes('window') || 
            name.includes('glass') || 
            name.includes('windshield') ||
            name.includes('vitre') // French for glass
          )

          // Detect dark/black parts (likely interior, tires, trim)
          const isDark = originalMat && originalMat.color && (
            (originalMat.color.r < 0.2 && originalMat.color.g < 0.2 && originalMat.color.b < 0.2)
          )

          // Detect metallic/chrome parts (mirrors, trim)
          const isChrome = originalMat && originalMat.metalness > 0.7 && !name.includes('body')
          
          if (isGlass) {
            // Keep glass transparent - DON'T change color
            child.material = new THREE.MeshPhysicalMaterial({
              color: originalMat.color || 0x88ccff,
              metalness: 0.1,
              roughness: 0.05,
              transmission: 0.9,
              transparent: true,
              opacity: 0.3,
              ior: 1.5,
              thickness: 0.5,
            })
            child.castShadow = false
            child.receiveShadow = true
          } else if (isDark && (name.includes('interior') || name.includes('seat') || name.includes('dashboard'))) {
            // Keep interior dark - DON'T change color
            child.material = new THREE.MeshStandardMaterial({
              color: originalMat.color,
              metalness: 0.1,
              roughness: 0.7,
            })
          } else if (name.includes('tire') || name.includes('tyre') || name.includes('wheel_')) {
            // Tires - keep black rubber
            if (isDark || name.includes('tire') || name.includes('tyre')) {
              child.material = new THREE.MeshStandardMaterial({
                color: 0x1a1a1a,
                metalness: 0.1,
                roughness: 0.9,
              })
            } else {
              // Chrome rims
              child.material = new THREE.MeshStandardMaterial({
                color: 0xcccccc,
                metalness: 0.95,
                roughness: 0.1,
                envMapIntensity: 2.0,
              })
            }
            child.castShadow = true
            child.receiveShadow = true
          } else if (name.includes('light') || name.includes('headlight') || name.includes('taillight') || name.includes('lamp')) {
            // Lights - keep original emissive
            const isHeadlight = name.includes('head') || name.includes('front')
            child.material = new THREE.MeshStandardMaterial({
              color: isHeadlight ? 0xffffee : 0xff3333,
              emissive: isHeadlight ? 0xffffaa : 0xff0000,
              emissiveIntensity: 0.5,
              metalness: 0.1,
              roughness: 0.2,
            })
          } else if (isChrome || name.includes('mirror') || name.includes('chrome') || name.includes('trim')) {
            // Chrome details - keep shiny
            child.material = new THREE.MeshStandardMaterial({
              color: 0xdddddd,
              metalness: 1.0,
              roughness: 0.05,
              envMapIntensity: 2.5,
            })
            child.castShadow = true
          } else if (name.includes('body') || name.includes('paint') || name.includes('hood') || name.includes('door') || name.includes('roof') || name.includes('fender') || name.includes('bumper')) {
            // Main car body - use paint material (reuse material instance)
            child.material = bodyMaterial
            child.castShadow = true
            child.receiveShadow = true
          } else {
            // Default: If it's a large colored mesh, it's probably body paint
            // If it's small/dark, keep original
            if (originalMat && !isDark && !isGlass) {
              child.material = bodyMaterial
              child.castShadow = true
              child.receiveShadow = true
            } else if (originalMat) {
              // Keep original material for dark/special parts
              child.castShadow = true
              child.receiveShadow = true
            }
        }
      }
    })
  }, [scene, paintMaterial, configuration])

  // Subtle rotation animation - Optimized to only run when needed
  useFrame((state, delta) => {
    if (groupRef.current) {
      // Very subtle idle animation - reduced frequency for better performance
      groupRef.current.position.y = Math.sin(state.clock.elapsedTime * 0.3) * 0.015
    }
  })

  // If no model found, use fallback geometric car
  if (hasError || !scene) {
    return <FallbackCar paintMaterial={paintMaterial} />
  }

  return (
    <group ref={groupRef}>
      <primitive
        object={scene}
        scale={1}
        position={[0, -0.5, 0]}
        rotation={[0, Math.PI / 4, 0]}
      />
    </group>
  )
}
