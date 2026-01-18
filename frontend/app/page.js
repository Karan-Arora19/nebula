'use client'

import { Car, Settings, Package, ChevronLeft, ChevronRight, ShoppingBag, Shirt, Watch, Coffee, Mail, Phone, MapPin, Facebook, Twitter, Instagram, Youtube, Linkedin } from 'lucide-react'
import Link from 'next/link'
import { useState } from 'react'

export default function Home() {
  const [currentIndex, setCurrentIndex] = useState(0)

  const cars = [
    { name: 'Nebula Velocity', type: 'Sport', price: '€89,900', power: '450 HP', img: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600' },
    { name: 'Nebula Elite', type: 'Luxury Sedan', price: '€125,000', power: '380 HP', img: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600' },
    { name: 'Nebula Apex', type: 'Supercar', price: '€245,000', power: '720 HP', img: 'https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=600' },
    { name: 'Nebula Urban', type: 'SUV', price: '€68,500', power: '310 HP', img: 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600' },
    { name: 'Nebula Thunder', type: 'Electric', price: '€95,000', power: '500 HP', img: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600' },
    { name: 'Nebula Prestige', type: 'Luxury Coupe', price: '€175,000', power: '550 HP', img: 'https://images.unsplash.com/photo-1541443131876-44b03de101c5?w=600' }
  ]

  const featuredProducts = [
    { 
      name: 'Nebula Racing Cap', 
      price: '€35', 
      img: 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400', 
      badge: 'BESTSELLER',
      description: 'Official Nebula racing cap with embroidered logo'
    },
    { 
      name: 'Carbon Fiber Wallet', 
      price: '€78', 
      img: 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=400', 
      badge: 'NEW',
      description: 'Lightweight carbon fiber construction'
    },
    { 
      name: 'Limited Edition Watch', 
      price: '€299', 
      img: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400', 
      badge: 'LIMITED',
      description: 'Only 500 pieces worldwide'
    },
    { 
      name: 'Team Jacket', 
      price: '€129', 
      img: 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400', 
      badge: 'HOT',
      description: 'Premium quality racing team jacket'
    },
    { 
      name: 'Leather Keychain', 
      price: '€25', 
      img: 'https://images.unsplash.com/photo-1611085583191-a3b181a88401?w=400', 
      badge: null,
      description: 'Handcrafted Italian leather'
    },
    { 
      name: 'Performance T-Shirt', 
      price: '€45', 
      img: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400', 
      badge: 'SALE',
      description: 'Moisture-wicking athletic fabric'
    }
  ]

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

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
            {featuredProducts.map((product, idx) => (
              <div
                key={idx}
                className="group bg-gray-800 rounded-xl overflow-hidden hover:transform hover:scale-105 transition-all duration-300 shadow-xl"
              >
                <div className="relative h-72 overflow-hidden">
                  <div 
                    className="absolute inset-0 bg-cover bg-center transform group-hover:scale-110 transition-transform duration-500"
                    style={{ backgroundImage: `url(${product.img})` }}
                  ></div>
                  <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-transparent to-transparent"></div>
                  
                  {product.badge && (
                    <div className={`absolute top-4 right-4 ${getBadgeColor(product.badge)} text-white text-xs font-bold px-3 py-1 rounded-full`}>
                      {product.badge}
                    </div>
                  )}
                </div>

                <div className="p-6">
                  <h3 className="text-2xl font-bold mb-2">{product.name}</h3>
                  <p className="text-gray-400 text-sm mb-4">{product.description}</p>
                  <div className="flex items-center justify-between">
                    <span className="text-3xl font-bold text-red-500">{product.price}</span>
                    <button className="bg-red-600 hover:bg-red-700 p-3 rounded-lg transition transform hover:scale-110">
                      <ShoppingBag className="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>


        </div>
      </div>

      {/* Footer */}
      <footer className="bg-black border-t border-gray-800">
        <div className="max-w-7xl mx-auto px-4 py-16">
          <div className="grid md:grid-cols-2 lg:grid-cols-6 gap-8 mb-12">
            {/* Brand Section */}
            <div className="lg:col-span-2">
              <div className="flex items-center gap-2 mb-4">
                <Car className="w-10 h-10 text-red-500" />
                <span className="text-3xl font-bold">NEBULA</span>
              </div>
              <p className="text-gray-400 mb-6 max-w-md">
                Engineering excellence and defining performance since 2024. Creating the ultimate driving experience for automotive enthusiasts worldwide.
              </p>
              <div className="flex gap-4">
                <a href="#" className="bg-gray-800 hover:bg-red-600 p-3 rounded-full transition">
                  <Facebook className="w-5 h-5" />
                </a>
                <a href="#" className="bg-gray-800 hover:bg-red-600 p-3 rounded-full transition">
                  <Twitter className="w-5 h-5" />
                </a>
                <a href="#" className="bg-gray-800 hover:bg-red-600 p-3 rounded-full transition">
                  <Instagram className="w-5 h-5" />
                </a>
                <a href="#" className="bg-gray-800 hover:bg-red-600 p-3 rounded-full transition">
                  <Youtube className="w-5 h-5" />
                </a>
                <a href="#" className="bg-gray-800 hover:bg-red-600 p-3 rounded-full transition">
                  <Linkedin className="w-5 h-5" />
                </a>
              </div>
            </div>

            {/* Quick Links */}
            <div>
              <h3 className="text-lg font-bold mb-4">Quick Links</h3>
              <ul className="space-y-2">
                <li><Link href="/cars" className="text-gray-400 hover:text-red-500 transition">Our Cars</Link></li>
                <li><Link href="/car-configurator" className="text-gray-400 hover:text-red-500 transition">Car Configurator</Link></li>
                <li><Link href="/world-drive" className="text-gray-400 hover:text-red-500 transition">World Drive</Link></li>
                <li><Link href="/merchandise" className="text-gray-400 hover:text-red-500 transition">Merchandise</Link></li>
                <li><Link href="/my-car" className="text-gray-400 hover:text-red-500 transition">My Nebula Car</Link></li>
              </ul>
            </div>

            {/* Dashboards */}
            <div>
              <h3 className="text-lg font-bold mb-4">Dashboards</h3>
              <ul className="space-y-2">
                <li><Link href="/user-dashboard" className="text-gray-400 hover:text-red-500 transition">User Dashboard</Link></li>
                <li><Link href="/admin-dashboard" className="text-gray-400 hover:text-red-500 transition">Admin Dashboard</Link></li>
              </ul>
            </div>

            {/* Support */}
            <div>
              <h3 className="text-lg font-bold mb-4">Support</h3>
              <ul className="space-y-2">
                <li><a href="#" className="text-gray-400 hover:text-red-500 transition">Contact Us</a></li>
                <li><a href="#" className="text-gray-400 hover:text-red-500 transition">FAQ</a></li>
                <li><a href="#" className="text-gray-400 hover:text-red-500 transition">Warranty</a></li>
                <li><a href="#" className="text-gray-400 hover:text-red-500 transition">Service Centers</a></li>
                <li><a href="#" className="text-gray-400 hover:text-red-500 transition">Test Drive</a></li>
              </ul>
            </div>

            {/* Contact */}
            <div>
              <h3 className="text-lg font-bold mb-4">Contact</h3>
              <ul className="space-y-3">
                <li className="flex items-start gap-3 text-gray-400">
                  <MapPin className="w-5 h-5 flex-shrink-0 mt-1 text-red-500" />
                  <span>Nebula HQ<br />Stuttgart, Germany</span>
                </li>
                <li className="flex items-center gap-3 text-gray-400">
                  <Phone className="w-5 h-5 flex-shrink-0 text-red-500" />
                  <span>+49 711 123 4567</span>
                </li>
                <li className="flex items-center gap-3 text-gray-400">
                  <Mail className="w-5 h-5 flex-shrink-0 text-red-500" />
                  <span>info@nebula.com</span>
                </li>
              </ul>
            </div>
          </div>

          {/* Newsletter */}
          <div className="border-t border-gray-800 pt-8 mb-8">
            <div className="max-w-2xl mx-auto text-center">
              <h3 className="text-2xl font-bold mb-3">Stay Updated</h3>
              <p className="text-gray-400 mb-6">Subscribe to our newsletter for exclusive offers and updates</p>
              <div className="flex gap-2 max-w-md mx-auto">
                <input
                  type="email"
                  placeholder="Enter your email"
                  className="flex-1 bg-gray-800 border border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:border-red-500 transition"
                />
                <button className="bg-red-600 hover:bg-red-700 px-6 py-3 rounded-lg font-semibold transition">
                  Subscribe
                </button>
              </div>
            </div>
          </div>

          {/* Copyright */}
          <div className="border-t border-gray-800 pt-8 flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-gray-400">
            <p>&copy; 2024 Nebula. All rights reserved.</p>
            <div className="flex gap-6">
              <a href="#" className="hover:text-red-500 transition">Privacy Policy</a>
              <a href="#" className="hover:text-red-500 transition">Terms of Service</a>
              <a href="#" className="hover:text-red-500 transition">Cookie Policy</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}