import { useEffect, useState } from 'react'
import { apiClient } from '@/lib/api'
import { useAuth } from './useAuth'

export function useUserVehicleInfo() {
  const { user } = useAuth()
  const [vehicleInfo, setVehicleInfo] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!user?.id) {
      setVehicleInfo(null)
      setLoading(false)
      return
    }
    setLoading(true)
    apiClient
      .request('/user-vehicle/info', {
        headers: { 'X-User-Id': user.id },
      })
      .then(setVehicleInfo)
      .catch(setError)
      .finally(() => setLoading(false))
  }, [user?.id])

  return { vehicleInfo, loading, error }
}
