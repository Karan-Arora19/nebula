'use client'

import React from 'react'
import { ChevronLeft, ChevronRight } from 'lucide-react'

/**
 * CarSelector Component
 * 
 * Provides navigation controls to switch between available car models.
 * 
 * @param {Object} props
 * @param {string} props.currentCarId - Currently selected car ID
 * @param {Function} props.onCarChange - Callback when car is changed
 * @param {Array} props.availableCars - Array of available cars
 */

export default function CarSelector({ currentCarId, onCarChange, availableCars }) {
  const currentIndex = availableCars.findIndex((car) => car.id === currentCarId)
  const currentCar = availableCars[currentIndex] || availableCars[0]

  const handlePrevious = () => {
    const prevIndex = currentIndex > 0 ? currentIndex - 1 : availableCars.length - 1
    onCarChange(availableCars[prevIndex].id)
  }

  const handleNext = () => {
    const nextIndex = currentIndex < availableCars.length - 1 ? currentIndex + 1 : 0
    onCarChange(availableCars[nextIndex].id)
  }

  return (
    <div className="flex items-center gap-3 bg-black/80 backdrop-blur-md px-4 py-2 rounded-lg border border-red-500/50 shadow-lg shadow-red-500/20">
      {/* Previous Button */}
      <button
        onClick={handlePrevious}
        className="p-1.5 rounded-lg bg-gray-800/50 hover:bg-red-500/20 border border-gray-700 hover:border-red-500/50 transition-all duration-200 group"
        aria-label="Previous car"
      >
        <ChevronLeft className="w-4 h-4 text-gray-300 group-hover:text-red-300 transition-colors" />
      </button>

      {/* Car Name Display */}
      <div className="text-center min-w-[100px]">
        <p className="text-[10px] text-gray-400 uppercase tracking-wider mb-0.5">Model</p>
        <p className="text-white font-bold text-xs">{currentCar.name}</p>
        <p className="text-[10px] text-gray-500 mt-0.5">
          {currentIndex + 1} / {availableCars.length}
        </p>
      </div>

      {/* Next Button */}
      <button
        onClick={handleNext}
        className="p-1.5 rounded-lg bg-gray-800/50 hover:bg-red-500/20 border border-gray-700 hover:border-red-500/50 transition-all duration-200 group"
        aria-label="Next car"
      >
        <ChevronRight className="w-4 h-4 text-gray-300 group-hover:text-red-300 transition-colors" />
      </button>
    </div>
  )
}
