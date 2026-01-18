'use client'

import React from 'react'

/**
 * LoadingSkeleton Component
 * 
 * Displays animated loading skeletons for configuration options
 */
export default function LoadingSkeleton({ count = 3 }) {
  return (
    <div className="space-y-2">
      {[...Array(count)].map((_, i) => (
        <div
          key={i}
          className="p-3 rounded-lg border border-gray-700/50 bg-gray-800/30 relative overflow-hidden"
          style={{ animationDelay: `${i * 0.1}s` }}
        >
          {/* Shimmer effect */}
          <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent animate-shimmer"></div>
          
          <div className="flex items-start justify-between relative z-10">
            <div className="flex-1 space-y-2">
              <div className="h-4 bg-gray-700/50 rounded w-3/4 animate-pulse"></div>
              <div className="h-3 bg-gray-700/30 rounded w-1/2 animate-pulse"></div>
              <div className="h-4 bg-gray-700/40 rounded w-1/4 animate-pulse"></div>
            </div>
            <div className="w-5 h-5 bg-gray-700/50 rounded-full ml-3 animate-pulse"></div>
          </div>
        </div>
      ))}
    </div>
  )
}

/**
 * CategoryTabSkeleton Component
 * 
 * Loading skeleton for category tabs
 */
export function CategoryTabSkeleton() {
  return (
    <div className="bg-gray-900/80 backdrop-blur-sm border-b border-gray-700/50 shadow-md">
      <div className="flex">
        {[...Array(3)].map((_, i) => (
          <div
            key={i}
            className="shrink-0 px-4 py-3 border-b-2 border-transparent relative overflow-hidden"
          >
            {/* Shimmer effect */}
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent animate-shimmer"></div>
            
            <div className="flex items-center gap-2 relative z-10">
              <div className="w-4 h-4 bg-gray-700/50 rounded animate-pulse"></div>
              <div className="h-4 bg-gray-700/50 rounded w-20 animate-pulse"></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
