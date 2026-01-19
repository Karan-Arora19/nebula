"use client";

import React, { useState, useEffect, useRef, useMemo } from "react";
import PropTypes from "prop-types";

/**
 * RenderingEffect Component
 *
 * Displays a beautiful rendering transition effect when:
 * - User first visits the page
 * - User changes to a different car model
 */

// Generate particle positions once (stable between renders)
const generateParticleStyles = (count) =>
  new Array(count).fill(null).map((_, i) => ({
    id: `particle-${i}`,
    left: `${(i * 17 + 23) % 100}%`,
    top: `${(i * 31 + 13) % 100}%`,
    animationDelay: `${(i * 0.1) % 2}s`,
    animationDuration: `${2 + (i % 3)}s`,
  }));

export default function RenderingEffect({
  isRendering,
  onComplete,
  modelLoaded,
}) {
  const [progress, setProgress] = useState(0);
  const [showEffect, setShowEffect] = useState(false);
  const intervalRef = useRef(null);
  const modelLoadedRef = useRef(false);

  // Memoize particle styles to avoid regeneration
  const particleStyles = useMemo(() => generateParticleStyles(20), []);

  // Update ref when modelLoaded changes (without triggering effect)
  useEffect(() => {
    modelLoadedRef.current = modelLoaded;
  }, [modelLoaded]);

  useEffect(() => {
    // Only start effect when isRendering becomes true
    if (isRendering) {
      // Clear any existing interval
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }

      setShowEffect(true);
      setProgress(0);

      // Fast animation - complete in less than 1.5 seconds
      const totalDuration = 1200; // 1.2 seconds total
      const steps = 60;
      const increment = 100 / steps;
      const stepDuration = totalDuration / steps;

      let currentStep = 0;
      intervalRef.current = setInterval(() => {
        currentStep++;
        const newProgress = Math.min(currentStep * increment, 100);
        setProgress(newProgress);

        // Complete when we reach 100% or model is loaded and we're past 70%
        const shouldComplete =
          newProgress >= 100 || (modelLoadedRef.current && newProgress >= 70);

        if (shouldComplete) {
          if (intervalRef.current) {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
          }
          setProgress(100);
          // Quick fade out
          setTimeout(() => {
            setShowEffect(false);
            onComplete?.();
          }, 150);
        }
      }, stepDuration);

      return () => {
        if (intervalRef.current) {
          clearInterval(intervalRef.current);
          intervalRef.current = null;
        }
      };
    } else {
      // Reset when rendering stops
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
      setShowEffect(false);
      setProgress(0);
    }
  }, [isRendering, onComplete]); // Only depends on isRendering to prevent double render

  if (!showEffect) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-gradient-to-br from-black via-gray-900 to-black backdrop-blur-sm">
      {/* Animated Background */}
      <div className="absolute inset-0 overflow-hidden">
        {/* Rotating gradient orbs */}
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-red-500/20 rounded-full blur-3xl animate-pulse"></div>
        <div
          className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-red-600/20 rounded-full blur-3xl animate-pulse"
          style={{ animationDelay: "1s" }}
        ></div>
        <div
          className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-64 h-64 bg-red-500/10 rounded-full blur-2xl animate-pulse"
          style={{ animationDelay: "0.5s" }}
        ></div>
      </div>

      {/* Main Content */}
      <div className="relative z-10 flex flex-col items-center gap-6">
        {/* Animated Spinner */}
        <div className="relative">
          {/* Outer ring */}
          <div className="w-24 h-24 border-4 border-red-500/30 rounded-full animate-spin"></div>
          {/* Inner ring */}
          <div
            className="absolute inset-0 w-24 h-24 border-4 border-transparent border-t-red-500 rounded-full animate-spin"
            style={{ animationDuration: "0.8s" }}
          ></div>
          {/* Center dot */}
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
          </div>
        </div>

        {/* Text Content */}
        <div className="text-center">
          <h2 className="text-3xl font-bold text-white mb-2 tracking-tight">
            Rendering Model
          </h2>
          <p className="text-gray-400 text-sm mb-4">
            Preparing your vehicle...
          </p>

          {/* Progress Bar */}
          <div className="w-64 h-2 bg-gray-800 rounded-full overflow-hidden border border-gray-700">
            <div
              className="h-full bg-gradient-to-r from-red-500 via-red-600 to-red-500 transition-all duration-300 ease-out shadow-lg shadow-red-500/50"
              style={{ width: `${progress}%` }}
            >
              <div className="h-full bg-gradient-to-r from-transparent via-white/30 to-transparent animate-shimmer"></div>
            </div>
          </div>

          {/* Progress Percentage */}
          <p className="text-red-400 text-xs font-semibold mt-3">
            {Math.round(progress)}%
          </p>
        </div>

        {/* Particle Effects */}
        <div className="absolute inset-0 pointer-events-none">
          {particleStyles.map((particle) => (
            <div
              key={particle.id}
              className="absolute w-1 h-1 bg-red-500 rounded-full animate-float"
              style={{
                left: particle.left,
                top: particle.top,
                animationDelay: particle.animationDelay,
                animationDuration: particle.animationDuration,
              }}
            ></div>
          ))}
        </div>
      </div>

      {/* Fade out overlay */}
      {progress >= 100 && (
        <div className="absolute inset-0 bg-gradient-to-br from-black via-gray-900 to-black animate-fadeOut"></div>
      )}
    </div>
  );
}

RenderingEffect.propTypes = {
  isRendering: PropTypes.bool.isRequired,
  onComplete: PropTypes.func,
  modelLoaded: PropTypes.bool,
};

RenderingEffect.defaultProps = {
  onComplete: () => {},
  modelLoaded: false,
};