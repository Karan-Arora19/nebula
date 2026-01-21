import { Car, Facebook, Twitter, Instagram, Youtube, Linkedin, MapPin, Phone, Mail } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-black border-t border-gray-800 text-white">
      <div className="max-w-7xl mx-auto px-4 py-16">
        {/* Main Footer Content */}
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
              <li><a className="text-gray-400 hover:text-red-500 transition" href="/cars">Our Cars</a></li>
              <li><a className="text-gray-400 hover:text-red-500 transition" href="/car-configurator">Car Configurator</a></li>
              <li><a className="text-gray-400 hover:text-red-500 transition" href="/world-drive">World Drive</a></li>
              <li><a className="text-gray-400 hover:text-red-500 transition" href="/merchandise">Merchandise</a></li>
              <li><a className="text-gray-400 hover:text-red-500 transition" href="/my-car">My Nebula Car</a></li>
            </ul>
          </div>

          {/* Dashboards */}
          <div>
            <h3 className="text-lg font-bold mb-4">Dashboards</h3>
            <ul className="space-y-2">
              <li><a className="text-gray-400 hover:text-red-500 transition" href="/user-dashboard">User Dashboard</a></li>
              <li><a className="text-gray-400 hover:text-red-500 transition" href="/admin-dashboard">Admin Dashboard</a></li>
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

        {/* Copyright */}
        <div className="border-t border-gray-800 pt-8 flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-gray-400">
          <p>© 2024 Nebula. All rights reserved.</p>
          <div className="flex gap-6">
            <a href="#" className="hover:text-red-500 transition">Privacy Policy</a>
            <a href="#" className="hover:text-red-500 transition">Terms of Service</a>
            <a href="#" className="hover:text-red-500 transition">Cookie Policy</a>
          </div>
        </div>
      </div>
    </footer>
  );
}
