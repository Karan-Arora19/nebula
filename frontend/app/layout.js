import { Inter } from 'next/font/google'
import './globals.css'
import Navigation from '@/components/Navigation'
import Footer from '@/components/Footer'
import FloatingCart from '@/components/FloatingCart'
import ClientLayout from './client-layout'

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'Nebula - Engineering Excellence',
  description: 'Luxury automobile manufacturer',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <head>
        <link
          rel="preconnect"
          href="https://fonts.googleapis.com"
        />
        <link
          rel="preconnect"
          href="https://fonts.gstatic.com"
          crossOrigin="anonymous"
        />
        <link
          href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap"
          rel="stylesheet"
        />
        <link
          rel="stylesheet"
          href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY="
          crossOrigin=""
        />
      </head>
      <body className={inter.className}>
        <ClientLayout>
          <Navigation />
          {children}
          <FloatingCart />
          <Footer />
        </ClientLayout>
      </body>
    </html>
  )
}