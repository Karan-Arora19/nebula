'use client'

import Link from 'next/link'
import { usePathname, useRouter } from 'next/navigation'
import { Car, ShoppingCart, MapPin, User, Settings, LogOut } from 'lucide-react'
import { useAuth } from '@/hooks/useAuth'

export default function Navigation() {
  const pathname = usePathname()
  const router = useRouter()
  const { user, logout } = useAuth()
  
  // Don't highlight nav items on dashboard pages
  const isDashboardPage = pathname.includes('dashboard')

  const navigation = [
    { href: '/', label: 'Home', icon: Car },
    { href: '/cars', label: 'Cars', icon: Car },
    { href: '/car-configurator', label: 'Car Configurator', icon: Settings },
    { href: '/world-drive', label: 'World Drive', icon: MapPin },
    { href: '/merchandise', label: 'Merchandise', icon: ShoppingCart },
    ...(user && user.role === 'USER' ? [{ href: '/my-car', label: 'My Car', icon: User }] : [])
  ]

  const handleLogout = () => {
    logout()
    router.push('/login')
  }

  return (
    <nav className="bg-black text-white sticky top-0 z-50 shadow-lg">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <Link href="/" className="flex items-center gap-2">
            <Car className="w-8 h-8 text-red-500" />
            <span className="text-2xl font-bold">NEBULA</span>
          </Link>
          <div className="flex gap-1">
            {navigation.map(item => {
              const Icon = item.icon
              const isActive = !isDashboardPage && pathname === item.href
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg transition ${
                    isActive ? 'bg-red-600' : 'hover:bg-gray-800'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  <span className="hidden md:inline">{item.label}</span>
                </Link>
              )
            })}
            {user ? (
              <button
                onClick={handleLogout}
                className="flex items-center gap-2 px-4 py-2 rounded-lg hover:bg-gray-800 transition"
              >
                <LogOut className="w-4 h-4" />
                <span className="hidden md:inline">Logout</span>
              </button>
            ) : (
              <Link
                href="/login"
                className="flex items-center gap-2 px-4 py-2 rounded-lg bg-red-600 hover:bg-red-700 transition text-white font-semibold"
              >
                <LogOut className="w-4 h-4" />
                <span className="hidden md:inline">Login</span>
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}