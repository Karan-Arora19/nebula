'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'

export default function CheckoutPage() {
  const [step, setStep] = useState('shipping') // shipping, payment, confirmation
  const [cart, setCart] = useState(() => {
    if (typeof window !== 'undefined') {
      const saved = localStorage.getItem('cart')
      return saved ? JSON.parse(saved) : []
    }
    return []
  })
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    address: '',
    city: '',
    zipCode: '',
    country: ''
  })
  const router = useRouter()

  const totalPrice = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0)

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleShippingSubmit = (e) => {
    e.preventDefault()
    setStep('payment')
  }

  const handlePaymentSuccess = () => {
    setStep('confirmation')
    // Clear cart
    localStorage.removeItem('cart')
    window.dispatchEvent(new Event('cart-updated'))
    
    // Redirect to home after 3 seconds
    setTimeout(() => {
      router.push('/')
    }, 3000)
  }

  if (step === 'confirmation') {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-lg shadow-lg p-8 max-w-md text-center">
          <div className="text-6xl mb-4">✓</div>
          <h1 className="text-3xl font-bold text-green-600 mb-4">Order Confirmed!</h1>
          <p className="text-gray-600 mb-2">Thank you for your purchase.</p>
          <p className="text-gray-600 mb-6">Order confirmation has been sent to your email.</p>
          <p className="text-sm text-gray-500">Redirecting to home...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-slate-50 py-12 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-4xl font-bold mb-12 text-center">Checkout</h1>

        <div className="grid md:grid-cols-3 gap-8">
          {/* Checkout Form */}
          <div className="md:col-span-2">
            <div className="bg-white rounded-lg shadow-md p-8">
              {step === 'shipping' && (
                <form onSubmit={handleShippingSubmit} className="space-y-4">
                  <h2 className="text-2xl font-bold mb-6">Shipping Information</h2>
                  
                  <div className="grid md:grid-cols-2 gap-4">
                    <input
                      type="text"
                      name="firstName"
                      placeholder="First Name"
                      required
                      value={formData.firstName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                    />
                    <input
                      type="text"
                      name="lastName"
                      placeholder="Last Name"
                      required
                      value={formData.lastName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                    />
                  </div>

                  <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    required
                    value={formData.email}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  />

                  <input
                    type="text"
                    name="address"
                    placeholder="Address"
                    required
                    value={formData.address}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  />

                  <div className="grid md:grid-cols-2 gap-4">
                    <input
                      type="text"
                      name="city"
                      placeholder="City"
                      required
                      value={formData.city}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                    />
                    <input
                      type="text"
                      name="zipCode"
                      placeholder="Zip Code"
                      required
                      value={formData.zipCode}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                    />
                  </div>

                  <input
                    type="text"
                    name="country"
                    placeholder="Country"
                    required
                    value={formData.country}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                  />

                  <button
                    type="submit"
                    className="w-full bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-semibold transition mt-6"
                  >
                    Continue to Payment
                  </button>
                </form>
              )}

              {step === 'payment' && (
                <div className="space-y-6">
                  <h2 className="text-2xl font-bold">Payment</h2>
                  
                  <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                    <p className="text-blue-800 text-sm">
                      Demo mode: Click "Complete Payment" to simulate successful payment
                    </p>
                  </div>

                  <div className="space-y-4">
                    <input
                      type="text"
                      placeholder="Card Number"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                    />
                    <div className="grid grid-cols-2 gap-4">
                      <input
                        type="text"
                        placeholder="MM/YY"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                      />
                      <input
                        type="text"
                        placeholder="CVC"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                      />
                    </div>
                  </div>

                  <div className="flex gap-3">
                    <button
                      onClick={() => setStep('shipping')}
                      className="flex-1 border border-gray-300 hover:bg-gray-50 text-gray-700 py-3 rounded-lg font-semibold transition"
                    >
                      Back
                    </button>
                    <button
                      onClick={handlePaymentSuccess}
                      className="flex-1 bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-semibold transition"
                    >
                      Complete Payment
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Order Summary */}
          <div className="md:col-span-1">
            <div className="bg-white rounded-lg shadow-md p-8 sticky top-24">
              <h3 className="text-xl font-bold mb-6">Order Summary</h3>
              
              <div className="space-y-3 mb-6 max-h-48 overflow-y-auto">
                {cart.map(item => (
                  <div key={item.productId} className="flex justify-between text-sm">
                    <span>{item.name} x{item.quantity}</span>
                    <span>€{(item.price * item.quantity).toFixed(2)}</span>
                  </div>
                ))}
              </div>

              <div className="border-t pt-4">
                <div className="flex justify-between font-bold text-lg">
                  <span>Total:</span>
                  <span>€{totalPrice.toFixed(2)}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
