'use client'

import dynamic from 'next/dynamic'

const MapViewClient = dynamic(() => import('./MapViewClient'), {
  ssr: false,
  loading: () => (
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

      <div className="flex-1 relative">
        {/* Outer glow */}
        <div className="absolute inset-0 bg-gradient-to-br from-red-600/20 via-rose-500/10 to-pink-500/20 rounded-3xl blur-xl" />
        
        {/* Main frame container */}
        <div className="relative h-full bg-gradient-to-br from-gray-800 via-gray-700 to-gray-800 rounded-3xl p-1.5 shadow-2xl">
          {/* Inner frame with bevel effect */}
          <div className="h-full bg-gradient-to-br from-gray-600 to-gray-700 rounded-[22px] p-[3px]">
            {/* White map container */}
            <div className="h-full bg-white rounded-[19px] relative overflow-hidden shadow-inner flex items-center justify-center">
              <div className="text-center">
                <div className="w-12 h-12 border-4 border-gray-500 border-t-gray-300 rounded-full animate-spin mx-auto mb-4" />
                <p className="text-gray-600 font-medium">Loading map...</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  ),
})

export default function MapView(props) {
  return <MapViewClient {...props} />
}
