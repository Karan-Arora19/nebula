'use client'

/**
 * CustomizationPanel Component
 * 
 * Displays customization options for the selected category.
 * Shows parts list with selection state, pricing, and descriptions.
 * 
 * @param {Object} props
 * @param {Array} props.categories - Array of customization categories
 * @param {string} props.activeCategory - Currently active category ID
 * @param {Function} props.setActiveCategory - Function to change active category
 * @param {Object} props.configuration - Current configuration state
 * @param {Function} props.onPartSelect - Callback when a part is selected
 */

import React from 'react'

export default function CustomizationPanel({
  categories,
  activeCategory,
  setActiveCategory,
  configuration,
  onPartSelect,
}) {
  const currentCategory = categories.find((cat) => cat.id === activeCategory)

  return (
    <div className="flex flex-col h-full bg-gradient-to-b from-gray-900 to-black text-white">
      {/* Category Tabs - Enhanced */}
      <div className="bg-gray-900/80 backdrop-blur-sm border-b border-gray-700/50 shadow-md">
        <div className="flex overflow-x-auto scrollbar-hide">
          {categories.map((category) => (
            <button
              key={category.id}
              onClick={() => setActiveCategory(category.id)}
              className={`shrink-0 px-4 py-3 text-xs font-medium transition-all duration-200 border-b-2 relative ${
                activeCategory === category.id
                  ? "bg-gray-800/50 border-red-500 text-white shadow-md shadow-red-500/20"
                  : "border-transparent text-gray-400 hover:text-white hover:bg-gray-800/30"
              }`}
            >
              {activeCategory === category.id && (
                <div className="absolute inset-0 bg-gradient-to-b from-red-500/10 to-transparent pointer-events-none"></div>
              )}
              <span className="mr-2 relative z-10">{category.icon}</span>
              <span className="relative z-10">{category.name}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Parts List */}
      <div className="flex-1 overflow-y-auto p-4 space-y-2 bg-gradient-to-b from-transparent via-gray-900/20 to-transparent">
        {currentCategory && (
          <>
            <div className="mb-4 pb-3 border-b border-gray-700/30">
              <h3 className="text-lg font-bold text-white mb-0.5">
                {currentCategory.name}
              </h3>
              <p className="text-gray-400 text-xs">
                Select a {currentCategory.name.toLowerCase()} option
              </p>
            </div>

            <div className="grid gap-2">
              {currentCategory.parts.map((part) => {
                const isSelected =
                  configuration[currentCategory.id] === part.visualKey

                return (
                  <button
                    key={part.id}
                    onClick={() =>
                      onPartSelect(currentCategory.id, part.visualKey)
                    }
                    className={`group relative p-3 rounded-lg border transition-all duration-200 text-left ${
                      isSelected
                        ? "bg-red-500/20 border-red-500 shadow-lg shadow-red-500/30 backdrop-blur-sm"
                        : "bg-gray-800/30 border-gray-700/50 hover:border-red-500/50 hover:bg-gray-800/50"
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-1.5 mb-0.5">
                          <h4
                            className={`text-sm font-semibold ${
                              isSelected ? "text-red-300" : "text-white"
                            }`}
                          >
                            {part.name}
                          </h4>
                          {isSelected && (
                            <span className="px-1.5 py-0.5 text-[10px] font-medium bg-red-500 text-white rounded-full">
                              Selected
                            </span>
                          )}
                        </div>
                        {part.description && (
                          <p className="text-xs text-gray-400 mb-1.5">
                            {part.description}
                          </p>
                        )}
                        <div className="flex items-center justify-between">
                          <span
                            className={`text-sm font-bold ${
                              part.cost === 0
                                ? "text-green-400"
                                : isSelected
                                ? "text-red-300"
                                : "text-gray-300"
                            }`}
                          >
                            {part.cost === 0
                              ? "Included"
                              : `€${part.cost.toLocaleString()}`}
                          </span>
                        </div>
                      </div>

                      {/* Selection indicator */}
                      <div
                        className={`ml-3 w-5 h-5 rounded-full border flex items-center justify-center transition-all ${
                          isSelected
                            ? "bg-red-500 border-red-500"
                            : "border-gray-600 group-hover:border-gray-500"
                        }`}
                      >
                        {isSelected && (
                          <svg
                            className="w-3 h-3 text-white"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={3}
                              d="M5 13l4 4L19 7"
                            />
                          </svg>
                        )}
                      </div>
                    </div>
                  </button>
                )
              })}
            </div>
          </>
        )}
      </div>

      {/* Category Info Footer - Enhanced */}
      <div className="p-3 bg-gray-900/80 backdrop-blur-sm border-t border-gray-700/50 shadow-md">
        <div className="flex items-center justify-between text-xs">
          <span className="text-gray-400 font-medium">
            {currentCategory?.parts.length} options available
          </span>
          <span className="text-red-400 text-[10px] font-semibold uppercase tracking-wider">
            Tap to customize
          </span>
        </div>
      </div>
    </div>
  )
}
