'use client'

import React from 'react'
import { AlertCircle, Loader2, CheckCircle2 } from 'lucide-react'

/**
 * ServiceStatusNotification Component
 * 
 * Displays service status notifications for vehicle-service
 */
export default function ServiceStatusNotification({ status, message }) {
  if (status === 'connected') {
    return null // Don't show anything when connected
  }

  const getStatusConfig = () => {
    switch (status) {
      case 'loading':
        return {
          icon: <Loader2 className="w-5 h-5 animate-spin text-blue-400" />,
          bgColor: 'bg-blue-500/10 border-blue-500/50',
          textColor: 'text-blue-300',
          title: 'Vehicle Service Starting',
          message: message || 'Please wait while we connect to the vehicle service...'
        }
      case 'error':
        return {
          icon: <AlertCircle className="w-5 h-5 text-red-400" />,
          bgColor: 'bg-red-500/10 border-red-500/50',
          textColor: 'text-red-300',
          title: 'Vehicle Service Unavailable',
          message: message || 'Vehicle service is not started or crashed. Please check the service status.'
        }
      default:
        return null
    }
  }

  const config = getStatusConfig()
  if (!config) return null

  return (
    <div className={`fixed top-20 left-1/2 transform -translate-x-1/2 z-50 animate-slide-down`}>
      <div className={`${config.bgColor} border-2 ${config.textColor} px-6 py-4 rounded-xl shadow-2xl backdrop-blur-md flex items-center gap-4 min-w-[400px] max-w-[600px]`}>
        <div className="flex-shrink-0">
          {config.icon}
        </div>
        <div className="flex-1">
          <h3 className="font-bold text-sm mb-1">{config.title}</h3>
          <p className="text-xs opacity-90">{config.message}</p>
        </div>
      </div>
    </div>
  )
}
