'use client'

import { useCallback, useEffect, useState } from 'react'
import { ShoppingCart, Heart, Star, Filter } from 'lucide-react'

export default function MerchandisePage() {
  const [cart, setCart] = useState([])
  const [favorites, setFavorites] = useState([])
  const [selectedCategory, setSelectedCategory] = useState('All')
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [debugInfo, setDebugInfo] = useState(null)

  const fetchProducts = useCallback(async (signal) => {
    try {
      setLoading(true)
      setError(null)
      setDebugInfo(null)
      // Use gateway for REST API requests when NEXT_PUBLIC_GATEWAY_URL is set.
      const gateway = process.env.NEXT_PUBLIC_GATEWAY_URL || 'http://localhost:8080'
      const url = `${gateway}/api/v1/merchandise/products`
      setDebugInfo({ attemptingUrl: url })
      const res = await fetch(url, {
        signal,
      })
      if (!res.ok) {
        const text = await res.text().catch(() => '')
        throw new Error(`Failed to load products (${res.status}) ${text}`)
      }
      const data = await res.json()
      setProducts(Array.isArray(data) ? data : [])
      setDebugInfo({ attemptingUrl: url, success: true, count: Array.isArray(data) ? data.length : 0 })
    } catch (err) {
      if (err && err.name === 'AbortError') return
      // Provide richer debug info for client-side troubleshooting
      const message = err && err.message ? err.message : String(err)
      setError(message)
      setDebugInfo(prev => ({ ...prev, error: message }))
      setProducts([])
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const controller = new AbortController()
    fetchProducts(controller.signal)
    return () => controller.abort()
  }, [fetchProducts])

  const categories = ['All', 'Apparel', 'Accessories', 'Models', 'Lifestyle']

  const filteredProducts = selectedCategory === 'All'
    ? products
    : products.filter(p => p.category === selectedCategory)

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

  const toggleFavorite = (productId) => {
    if (favorites.includes(productId)) {
      setFavorites(favorites.filter(id => id !== productId))
    } else {
      setFavorites([...favorites, productId])
    }
  }

  const getBadgeColor = (badge) => {
    switch (badge) {
      case 'Bestseller': return 'bg-yellow-500'
      case 'New': return 'bg-green-500'
      case 'Limited': return 'bg-purple-500'
      case 'Premium': return 'bg-blue-500'
      default: return 'bg-gray-500'
    }
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-12 gap-4">
          <div>
            <h1 className="text-5xl font-bold mb-2">Nebula Merchandise</h1>
            <p className="text-gray-400">Premium products for true automotive enthusiasts</p>
          </div>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2 bg-red-600 px-6 py-3 rounded-lg shadow-lg">
              <ShoppingCart className="w-6 h-6" />
              <span className="font-bold text-lg">{cart.length}</span>
            </div>
            <div className="flex items-center gap-2 bg-gray-800 px-6 py-3 rounded-lg">
              <Heart className="w-6 h-6" />
              <span className="font-bold text-lg">{favorites.length}</span>
            </div>
          </div>
        </div>

        {/* Category Filter */}
        <div className="flex items-center gap-4 mb-8 overflow-x-auto pb-2">
          <Filter className="w-5 h-5 text-gray-400 flex-shrink-0" />
          {categories.map(category => (
            <button
              key={category}
              onClick={() => setSelectedCategory(category)}
              className={`px-6 py-2 rounded-full font-semibold transition whitespace-nowrap ${selectedCategory === category
                ? 'bg-red-600 text-white'
                : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
                }`}
            >
              {category}
            </button>
          ))}
        </div>

        {/* Loading / Error */}
        {loading && (
          <div className="text-center py-20 text-gray-300">Loading merchandise...</div>
        )}
        {error && !loading && (
          <div className="text-center py-20 text-red-400">
            <div>{error}</div>
            {process.env.NODE_ENV === 'development' && debugInfo && (
              <div className="text-sm text-gray-400 mt-2">
                <div>URL: {debugInfo.attemptingUrl}</div>
                {debugInfo.error && <div>Error: {debugInfo.error}</div>}
                {debugInfo.success && <div>Loaded {debugInfo.count} products</div>}
              </div>
            )}
            <div className="mt-4">
              <button
                onClick={() => fetchProducts()}
                className="px-4 py-2 bg-gray-700 rounded"
              >
                Retry
              </button>
            </div>
          </div>
        )}

        {/* Products Grid */}
        {!loading && !error && (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredProducts.map(product => {
              const rating = product.rating ? Number(product.rating) : 0
              const reviews = product.reviews ?? 0
              const priceNumber = product.price !== undefined && product.price !== null ? Number(product.price) : 0
              const priceDisplay = Number.isNaN(priceNumber) ? '0.00' : priceNumber.toFixed(2)

              return (
                <div
                  key={product.id}
                  className="bg-gray-800 rounded-xl overflow-hidden hover:transform hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-2xl group"
                >
                  {/* Product Image */}
                  <div className="relative h-64 overflow-hidden">
                    <div
                      className="absolute inset-0 bg-cover bg-center transform group-hover:scale-110 transition-transform duration-500"
                      style={{ backgroundImage: `url(${product.imageUrl || product.img || ''})` }}
                    ></div>
                    <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-transparent to-transparent opacity-60"></div>

                    {/* Badge */}
                    {product.badge && (
                      <div className={`absolute top-4 left-4 ${getBadgeColor(product.badge)} text-white text-xs font-bold px-3 py-1 rounded-full`}>
                        {product.badge}
                      </div>
                    )}

                    {/* Favorite Button */}
                    <button
                      onClick={() => toggleFavorite(product.id)}
                      className={`absolute top-4 right-4 p-2 rounded-full backdrop-blur-sm transition-all ${favorites.includes(product.id)
                        ? 'bg-red-600 text-white'
                        : 'bg-white/20 text-white hover:bg-white/30'
                        }`}
                    >
                      <Heart className={`w-5 h-5 ${favorites.includes(product.id) ? 'fill-current' : ''}`} />
                    </button>

                    {/* Quick View on Hover */}
                    <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                      <button className="bg-white text-gray-900 px-6 py-2 rounded-full font-semibold hover:bg-gray-200 transition">
                        Quick View
                      </button>
                    </div>
                  </div>

                  {/* Product Info */}
                  <div className="p-5">
                    <div className="flex items-center gap-1 mb-2">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-4 h-4 ${i < Math.floor(rating)
                            ? 'text-yellow-400 fill-current'
                            : 'text-gray-600'
                            }`}
                        />
                      ))}
                      <span className="text-sm text-gray-400 ml-2">({reviews})</span>
                    </div>

                    <h3 className="text-xl font-bold mb-1 line-clamp-1">{product.name}</h3>
                    <p className="text-sm text-gray-400 mb-3">{product.category || 'Accessories'}</p>

                    <div className="flex items-center justify-between">
                      <span className="text-2xl font-bold text-red-500">€{priceDisplay}</span>
                      <button
                        onClick={() => addToCart(product)}
                        className="bg-red-600 hover:bg-red-700 p-3 rounded-lg transition transform hover:scale-110 active:scale-95"
                      >
                        <ShoppingCart className="w-5 h-5" />
                      </button>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        )}

        {/* Empty State */}
        {filteredProducts.length === 0 && (
          <div className="text-center py-20">
            <p className="text-2xl text-gray-400">No products found in this category</p>
          </div>
        )}

        {/* Cart Summary (if items exist) */}
        {cart.length > 0 && (
          <div className="fixed bottom-8 right-8 bg-gray-800 rounded-xl shadow-2xl p-6 max-w-sm border-2 border-red-600">
            <h3 className="text-xl font-bold mb-4">Cart Summary</h3>
            <div className="space-y-2 mb-4 max-h-40 overflow-y-auto">
              {cart.map((item, idx) => (
                <div key={idx} className="flex justify-between text-sm">
                  <span className="text-gray-400">{item.name}</span>
                  <span className="font-semibold">€{item.price}</span>
                </div>
              ))}
            </div>
            <div className="border-t border-gray-700 pt-4 mb-4">
              <div className="flex justify-between text-lg font-bold">
                <span>Total:</span>
                <span className="text-red-500">€{cart.reduce((sum, item) => sum + item.price, 0)}</span>
              </div>
            </div>
            <button className="w-full bg-red-600 hover:bg-red-700 py-3 rounded-lg font-semibold transition">
              Checkout Now
            </button>
          </div>
        )}
      </div>
    </div>
  )
}