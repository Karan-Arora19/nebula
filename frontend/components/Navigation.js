'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { Car, ShoppingCart, MapPin, User, Settings } from 'lucide-react'

export default function Navigation() {
  const pathname = usePathname()

  const navigation = [
    { href: '/', label: 'Home', icon: Car },
    { href: '/cars', label: 'Cars', icon: Car },
    { href: '/car-configurator', label: 'Car Configurator', icon: Settings },
    { href: '/world-drive', label: 'World Drive', icon: MapPin },
    { href: '/merchandise', label: 'Merchandise', icon: ShoppingCart },
    { href: '/my-car', label: 'My Car', icon: User }
  ]

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
              const isActive = pathname === item.href
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
          </div>
        </div>
      </div>
    </nav>
  )
}