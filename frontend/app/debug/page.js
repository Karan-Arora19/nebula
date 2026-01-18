'use client'

import { useEffect, useState } from 'react'

export default function DebugPage() {
  const [apiTest, setApiTest] = useState({ status: 'testing', data: null, error: null })
  const [envVars, setEnvVars] = useState({})

  useEffect(() => {
    // Capture environment variables
    setEnvVars({
      NEXT_PUBLIC_GATEWAY_URL: process.env.NEXT_PUBLIC_GATEWAY_URL,
      NODE_ENV: process.env.NODE_ENV,
      // Add any other relevant env vars
    })

    // Test API connection
    const testApi = async () => {
      try {
        const baseUrl = process.env.NEXT_PUBLIC_GATEWAY_URL || 'http://localhost:8080'
        console.log('Testing API with URL:', baseUrl)

        const response = await fetch(`${baseUrl}/api/v1/vehicles?size=5`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        })

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`)
        }

        const data = await response.json()
        setApiTest({ status: 'success', data, error: null })
      } catch (error) {
        console.error('API test failed:', error)
        setApiTest({ status: 'error', data: null, error: error.message })
      }
    }

    testApi()
  }, [])

  return (
    <div className="min-h-screen bg-gray-900 text-white p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-8">Nebula Debug Page</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Environment Variables */}
          <div className="bg-gray-800 rounded-lg p-6">
            <h2 className="text-xl font-semibold mb-4">Environment Variables</h2>
            <div className="space-y-2">
              {Object.entries(envVars).map(([key, value]) => (
                <div key={key} className="flex flex-col">
                  <span className="text-gray-400 text-sm">{key}:</span>
                  <span className="text-green-400 font-mono">
                    {value || '<not set>'}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* API Test Results */}
          <div className="bg-gray-800 rounded-lg p-6">
            <h2 className="text-xl font-semibold mb-4">API Connection Test</h2>

            <div className="mb-4">
              <span className="text-gray-400 text-sm">Status:</span>
              <span className={`ml-2 px-2 py-1 rounded text-sm ${
                apiTest.status === 'success' ? 'bg-green-600' : 
                apiTest.status === 'error' ? 'bg-red-600' : 'bg-yellow-600'
              }`}>
                {apiTest.status}
              </span>
            </div>

            {apiTest.error && (
              <div className="mb-4">
                <span className="text-gray-400 text-sm">Error:</span>
                <pre className="text-red-400 text-sm mt-1 bg-gray-900 p-2 rounded">
                  {apiTest.error}
                </pre>
              </div>
            )}

            {apiTest.data && (
              <div>
                <span className="text-gray-400 text-sm">Response:</span>
                <pre className="text-green-400 text-xs mt-1 bg-gray-900 p-2 rounded overflow-auto max-h-64">
                  {JSON.stringify(apiTest.data, null, 2)}
                </pre>
              </div>
            )}
          </div>
        </div>

        <div className="mt-8 bg-gray-800 rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">Browser Information</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
            <div>
              <span className="text-gray-400">User Agent:</span>
              <p className="text-gray-300 font-mono text-xs break-all">
                {typeof navigator !== 'undefined' ? navigator.userAgent : 'N/A'}
              </p>
            </div>
            <div>
              <span className="text-gray-400">Current URL:</span>
              <p className="text-gray-300 font-mono">
                {typeof window !== 'undefined' ? window.location.href : 'N/A'}
              </p>
            </div>
            <div>
              <span className="text-gray-400">Online Status:</span>
              <p className="text-gray-300">
                {typeof navigator !== 'undefined' ? (navigator.onLine ? 'Online' : 'Offline') : 'N/A'}
              </p>
            </div>
          </div>
        </div>

        <div className="mt-8 text-center">
          <button
            onClick={() => window.location.reload()}
            className="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg"
          >
            Refresh Test
          </button>
        </div>
      </div>
    </div>
  )
}
