'use client'

import { ShoppingCart, X } from 'lucide-react'
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'

export default function FloatingCart() {
  const [cart, setCart] = useState([])
  const [isOpen, setIsOpen] = useState(false)
  const router = useRouter()

  useEffect(() => {
    // Load cart from localStorage
    const savedCart = localStorage.getItem('cart')
    if (savedCart) {
      setCart(JSON.parse(savedCart))
    }

    // Listen for storage changes
    const handleStorageChange = (e) => {
      if (e.key === 'cart' && e.newValue) {
        setCart(JSON.parse(e.newValue))
      }
    }

    // Listen for custom cart update event
    const handleCartUpdate = () => {
      const savedCart = localStorage.getItem('cart')
      setCart(savedCart ? JSON.parse(savedCart) : [])
    }

    window.addEventListener('storage', handleStorageChange)
    window.addEventListener('cart-updated', handleCartUpdate)

    return () => {
      window.removeEventListener('storage', handleStorageChange)
      window.removeEventListener('cart-updated', handleCartUpdate)
    }
  }, [])

  const totalPrice = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0)

  const handleCheckout = () => {
    router.push('/checkout')
    setIsOpen(false)
  }

  const handleRemoveItem = (productId) => {
    const updatedCart = cart.filter(item => item.productId !== productId)
    setCart(updatedCart)
    localStorage.setItem('cart', JSON.stringify(updatedCart))
    window.dispatchEvent(new Event('cart-updated'))
  }

  return (
    <div className="fixed bottom-8 right-8 z-40">
      {/* Cart Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="bg-red-600 hover:bg-red-700 text-white rounded-full p-4 shadow-lg flex items-center gap-2 transition"
      >
        <ShoppingCart className="w-6 h-6" />
        {cart.length > 0 && (
          <span className="bg-yellow-500 text-black text-xs font-bold rounded-full w-6 h-6 flex items-center justify-center">
            {cart.length}
          </span>
        )}
      </button>

      {/* Cart Panel */}
      {isOpen && (
        <div className="absolute bottom-20 right-0 bg-white rounded-lg shadow-2xl w-96 max-h-96 flex flex-col">
          <div className="p-4 border-b flex justify-between items-center">
            <h3 className="font-bold text-lg">Shopping Cart</h3>
            <button
              onClick={() => setIsOpen(false)}
              className="text-gray-500 hover:text-black"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          <div className="flex-1 overflow-y-auto p-4">
            {cart.length === 0 ? (
              <p className="text-gray-500 text-center">Your cart is empty</p>
            ) : (
              <div className="space-y-3">
                {cart.map(item => (
                  <div key={item.productId} className="flex justify-between items-start border-b pb-3">
                    <div className="flex-1">
                      <p className="font-semibold text-sm">{item.name}</p>
                      <p className="text-gray-600 text-xs">
                        €{item.price} x {item.quantity}
                      </p>
                    </div>
                    <button
                      onClick={() => handleRemoveItem(item.productId)}
                      className="text-red-500 hover:text-red-700 ml-2"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {cart.length > 0 && (
            <div className="border-t p-4 space-y-3">
              <div className="flex justify-between font-bold text-lg">
                <span>Total:</span>
                <span>€{totalPrice.toFixed(2)}</span>
              </div>
              <button
                onClick={handleCheckout}
                className="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded-lg font-semibold transition"
              >
                Proceed to Checkout
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
