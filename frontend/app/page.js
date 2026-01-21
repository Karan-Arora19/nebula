'use client'

import { Car, Settings, Package, ChevronLeft, ChevronRight, ShoppingCart, Shirt, Watch, Coffee, Mail, Phone, MapPin, Facebook, Twitter, Instagram, Youtube, Linkedin } from 'lucide-react'
import Link from 'next/link'
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'

export default function Home() {
  const [currentIndex, setCurrentIndex] = useState(0)
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [cart, setCart] = useState([])
  const router = useRouter()

  const cars = [
    { name: 'Nebula Velocity', type: 'Sport', price: '€89,900', power: '450 HP', img: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600' },
    { name: 'Nebula Elite', type: 'Luxury Sedan', price: '€125,000', power: '380 HP', img: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600' },
    { name: 'Nebula Apex', type: 'Supercar', price: '€245,000', power: '720 HP', img: 'https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=600' },
    { name: 'Nebula Urban', type: 'SUV', price: '€68,500', power: '310 HP', img: 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600' },
    { name: 'Nebula Thunder', type: 'Electric', price: '€95,000', power: '500 HP', img: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600' },
    { name: 'Nebula Prestige', type: 'Luxury Coupe', price: '€175,000', power: '550 HP', img: 'https://images.unsplash.com/photo-1541443131876-44b03de101c5?w=600' }
  ]

  // Fetch products from merchandise API
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true)
        const merchandiseUrl = process.env.NEXT_PUBLIC_MERCHANDISE_URL || 'http://localhost:8084'
        const response = await fetch(`${merchandiseUrl}/api/v1/merchandise/products`)
        
        if (!response.ok) {
          throw new Error(`Failed to fetch products: ${response.status}`)
        }
        
        const data = await response.json()
        setProducts(data)
        setError(null)
      } catch (err) {
        console.error('Error fetching products:', err)
        setError(err.message)
        setProducts([])
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [])

  // Load cart from localStorage
  useEffect(() => {
    const savedCart = localStorage.getItem('cart')
    if (savedCart) {
      setCart(JSON.parse(savedCart))
    }

    // Listen for cart updates from other tabs/components
    const handleCartUpdate = () => {
      const updatedCart = localStorage.getItem('cart')
      if (updatedCart) {
        setCart(JSON.parse(updatedCart))
      }
    }

    window.addEventListener('cart-updated', handleCartUpdate)
    return () => window.removeEventListener('cart-updated', handleCartUpdate)
  }, [])

  const addToCart = (product) => {
    const cartItem = {
      productId: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
      image: product.imageUrl
    }
    
    const existingCart = localStorage.getItem('cart')
    const updatedCart = existingCart ? JSON.parse(existingCart) : []
    
    const existingItem = updatedCart.find(item => item.productId === product.id)
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      updatedCart.push(cartItem)
    }
    
    localStorage.setItem('cart', JSON.stringify(updatedCart))
    window.dispatchEvent(new Event('cart-updated'))
    setCart(updatedCart)
  }

  const nextSlide = () => {
    setCurrentIndex((prev) => (prev + 1) % cars.length)
  }

  const prevSlide = () => {
    setCurrentIndex((prev) => (prev - 1 + cars.length) % cars.length)
  }

  const getVisibleCars = () => {
    const visible = []
    for (let i = -1; i <= 1; i++) {
      const index = (currentIndex + i + cars.length) % cars.length
      visible.push({ ...cars[index], position: i })
    }
    return visible
  }

  const getBadgeColor = (badge) => {
    switch(badge) {
      case 'BESTSELLER': return 'bg-yellow-500'
      case 'NEW': return 'bg-green-500'
      case 'LIMITED': return 'bg-purple-500'
      case 'HOT': return 'bg-red-500'
      case 'SALE': return 'bg-blue-500'
      default: return 'bg-gray-500'
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
      {/* Hero Section */}
      <div className="relative h-screen flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=1920')] bg-cover bg-center opacity-30"></div>
        <div className="relative z-10 text-center px-4">
          <h1 className="text-7xl font-bold mb-6 tracking-tight">NEBULA</h1>
          <p className="text-2xl mb-8 text-gray-300">Engineering Excellence, Defining Performance</p>
        </div>
      </div>
      
      {/* Features Section */}
      <div className="py-20 px-4">
        <div className="max-w-6xl mx-auto grid md:grid-cols-3 gap-8">
          <div className="bg-gray-800 p-8 rounded-lg text-center hover:bg-gray-750 transition">
            <Car className="w-16 h-16 mx-auto mb-4 text-red-500" />
            <h3 className="text-xl font-bold mb-2">Innovation</h3>
            <p className="text-gray-400">Cutting-edge technology in every vehicle</p>
          </div>
          <div className="bg-gray-800 p-8 rounded-lg text-center hover:bg-gray-750 transition">
            <Settings className="w-16 h-16 mx-auto mb-4 text-red-500" />
            <h3 className="text-xl font-bold mb-2">Performance</h3>
            <p className="text-gray-400">Unmatched power and precision</p>
          </div>
          <div className="bg-gray-800 p-8 rounded-lg text-center hover:bg-gray-750 transition">
            <Package className="w-16 h-16 mx-auto mb-4 text-red-500" />
            <h3 className="text-xl font-bold mb-2">Luxury</h3>
            <p className="text-gray-400">Premium craftsmanship and comfort</p>
          </div>
        </div>
      </div>

      {/* Car Collection Slider */}
      <div className="py-20 px-4 bg-black">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-5xl font-bold text-center mb-4">Our Collection</h2>
          <p className="text-center text-gray-400 mb-16">Discover the perfect vehicle for your journey</p>
          
          <div className="relative h-[500px] flex items-center justify-center">
            {/* Navigation Buttons */}
            <button
              onClick={prevSlide}
              className="absolute left-0 z-20 bg-red-600 hover:bg-red-700 p-4 rounded-full transition transform hover:scale-110"
            >
              <ChevronLeft className="w-6 h-6" />
            </button>
            
            <button
              onClick={nextSlide}
              className="absolute right-0 z-20 bg-red-600 hover:bg-red-700 p-4 rounded-full transition transform hover:scale-110"
            >
              <ChevronRight className="w-6 h-6" />
            </button>

            {/* Car Cards */}
            <div className="relative w-full h-full flex items-center justify-center">
              {getVisibleCars().map((car, idx) => {
                const isCenter = car.position === 0
                const isLeft = car.position === -1
                const isRight = car.position === 1

                return (
                  <div
                    key={idx}
                    className="absolute transition-all duration-500 ease-out"
                    style={{
                      transform: isCenter
                        ? 'translateX(0) scale(1.2) translateZ(0)'
                        : isLeft
                        ? 'translateX(-120%) scale(0.85) translateZ(0)'
                        : 'translateX(120%) scale(0.85) translateZ(0)',
                      zIndex: isCenter ? 10 : 5,
                      opacity: isCenter ? 1 : 0.6,
                    }}
                  >
                    <div className={`bg-gray-800 rounded-2xl overflow-hidden shadow-2xl transition-all duration-500 ${isCenter ? 'w-96' : 'w-80'}`}>
                      <div
                        className="h-64 bg-cover bg-center relative"
                        style={{ backgroundImage: `url(${car.img})` }}
                      >
                        <div className="absolute inset-0 bg-gradient-to-t from-gray-900 to-transparent"></div>
                      </div>
                      <div className="p-6">
                        <h3 className="text-3xl font-bold mb-2">{car.name}</h3>
                        <p className="text-gray-400 mb-4">{car.type}</p>
                        <div className="flex justify-between items-center mb-4">
                          <span className="text-red-500 font-bold text-2xl">{car.price}</span>
                          <span className="text-gray-400 text-lg">{car.power}</span>
                        </div>
                        {isCenter && (
                          <Link
                            href="/car-configurator"
                            className="block w-full bg-red-600 hover:bg-red-700 py-3 rounded-lg text-center font-semibold transition transform hover:scale-105"
                          >
                            Configure Now
                          </Link>
                        )}
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>
          </div>

          {/* Dots Indicator */}
          <div className="flex justify-center gap-2 mt-12">
            {cars.map((_, idx) => (
              <button
                key={idx}
                onClick={() => setCurrentIndex(idx)}
                className={`w-3 h-3 rounded-full transition-all ${
                  idx === currentIndex ? 'bg-red-600 w-8' : 'bg-gray-600 hover:bg-gray-500'
                }`}
              />
            ))}
          </div>


        </div>
      </div>

      {/* Merchandise Section */}
      <div className="py-20 px-4 bg-gradient-to-b from-black to-gray-900">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-5xl font-bold mb-4">Nebula Merchandise</h2>
            <p className="text-gray-400 text-xl">Express your passion with our exclusive collection</p>
          </div>

          {loading && (
            <div className="text-center py-12">
              <p className="text-gray-400 text-lg">Loading products...</p>
            </div>
          )}

          {error && (
            <div className="text-center py-12">
              <p className="text-red-500 text-lg">Failed to load products: {error}</p>
            </div>
          )}

          {!loading && !error && products.length > 0 && (
            <>
              <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
                {products.slice(0, 4).map((product) => (
                  <div
                    key={product.id}
                    className="group bg-gray-800 rounded-xl overflow-hidden hover:transform hover:scale-105 transition-all duration-300 shadow-xl"
                  >
                    <div className="relative h-48 overflow-hidden">
                      <div 
                        className="absolute inset-0 bg-cover bg-center transform group-hover:scale-110 transition-transform duration-500"
                        style={{ backgroundImage: `url(${product.imageUrl || product.image_url || product.img || ''})` }}
                      ></div>
                      <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-transparent to-transparent"></div>
                      
                      {product.badge && (
                        <div className={`absolute top-2 right-2 ${getBadgeColor(product.badge)} text-white text-xs font-bold px-2 py-1 rounded-full`}>
                          {product.badge}
                        </div>
                      )}
                    </div>

                    <div className="p-4">
                      <h3 className="text-sm font-bold mb-1 line-clamp-2">{product.name}</h3>
                      <div className="flex items-center justify-between">
                        <span className="text-lg font-bold text-red-500">€{product.price.toFixed(2)}</span>
                        <button
                          onClick={() => addToCart(product)}
                          className="bg-red-600 hover:bg-red-700 p-2 rounded-lg transition transform hover:scale-110 active:scale-95"
                        >
                          <ShoppingCart className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              <div className="text-center">
                <Link
                  href="/merchandise"
                  className="inline-block bg-red-600 hover:bg-red-700 text-white font-bold py-3 px-12 rounded-lg transition transform hover:scale-105"
                >
                  Show More
                </Link>
              </div>
            </>
          )}

          {!loading && !error && products.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-400 text-lg">No products available</p>
            </div>
          )}

        </div>
      </div>

      {/* Cart Summary (if items exist) */}
      {cart.length > 0 && (
        <div className="fixed bottom-8 right-32 bg-gray-800 rounded-xl shadow-2xl p-6 max-w-sm border-2 border-red-600 z-40">
          <h3 className="text-xl font-bold mb-4">Cart Summary</h3>
          <div className="space-y-2 mb-4 max-h-40 overflow-y-auto">
            {cart.map((item, idx) => (
              <div key={idx} className="flex justify-between text-sm">
                <span className="text-gray-400">{item.name}</span>
                <span className="font-semibold">€{Number(item.price).toFixed(2)}</span>
              </div>
            ))}
          </div>
          <div className="border-t border-gray-700 pt-4 mb-4">
            <div className="flex justify-between text-lg font-bold">
              <span>Total:</span>
              <span className="text-red-500">€{cart.reduce((sum, item) => sum + (Number(item.price) || 0), 0).toFixed(2)}</span>
            </div>
          </div>
          <button
            className="w-full bg-red-600 hover:bg-red-700 py-3 rounded-lg font-semibold transition"
            onClick={() => router.push('/checkout')}
          >
            Checkout Now
          </button>
        </div>
      )}
    </div>
  )
}