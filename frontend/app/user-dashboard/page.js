'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../../hooks/useAuth';
import MyGaragePanel from '../../components/dashboards/MyGaragePanel';
import ActivityFeed from '../../components/dashboards/ActivityFeed';
import { Car, LogOut } from 'lucide-react';

export default function UserDashboard() {
  const router = useRouter();
  const { user, logout, isLoading } = useAuth();
  const [userVehicles, setUserVehicles] = useState([]);

  useEffect(() => {
    if (!isLoading && !user) {
      router.push('/login');
      return;
    }
  }, [user, isLoading, router]);

  useEffect(() => {
    if (user) {
      // Simulate data loading
      const timer = setTimeout(() => {
        setUserVehicles([
          {
            id: 1,
            name: 'Tesla Model 3',
            year: 2023,
            color: 'Midnight Black',
            mileage: 12500,
            status: 'Active',
          },
          {
            id: 2,
            name: 'BMW M440i',
            year: 2022,
            color: 'Alpine White',
            mileage: 28300,
            status: 'Maintenance',
          },
        ]);
      }, 500);

      return () => clearTimeout(timer);
    }
  }, [user]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-red-500/30 border-t-red-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="flex items-center gap-3 mb-2">
            <div className="bg-red-600 p-2 rounded-lg">
              <Car className="text-white" size={28} />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900">My Dashboard</h1>
              <p className="text-gray-600 text-sm mt-1">Welcome back, {user?.username || 'User'}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            <MyGaragePanel vehicles={userVehicles} />
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
              <h3 className="text-lg font-semibold mb-4 text-gray-900">Profile Info</h3>
              <div className="space-y-4">
                <div className="p-3 bg-gray-50 rounded-lg border border-gray-200">
                  <p className="text-xs text-gray-600 uppercase tracking-wider">Name</p>
                  <p className="font-semibold text-gray-900 mt-1">{user?.username || 'User'}</p>
                </div>
                <div className="p-3 bg-gray-50 rounded-lg border border-gray-200">
                  <p className="text-xs text-gray-600 uppercase tracking-wider">Email</p>
                  <p className="font-semibold text-gray-900 mt-1">{user?.email || 'user@example.com'}</p>
                </div>
                <div className="p-3 bg-gray-50 rounded-lg border border-gray-200">
                  <p className="text-xs text-gray-600 uppercase tracking-wider">Member Since</p>
                  <p className="font-semibold text-gray-900 mt-1">2023</p>
                </div>
              </div>
            </div>

            {/* Recent Activity */}
            <ActivityFeed />
          </div>
        </div>
      </div>
    </div>
  );
}
