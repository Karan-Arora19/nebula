'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../../hooks/useAuth';
import AdminKPIs from '../../components/dashboards/AdminKPIs';
import ActivityFeed from '../../components/dashboards/ActivityFeed';
import { BarChart3, LogOut, Users, TrendingUp } from 'lucide-react';

export default function AdminDashboard() {
  const router = useRouter();
  const { user, logout, isLoading } = useAuth();
  const [dashboardData, setDashboardData] = useState(null);

  useEffect(() => {
    if (!isLoading && !user) {
      router.push('/login');
      return;
    }

    if (!isLoading && user && user.role !== 'ADMIN') {
      logout();
      router.push('/login');
      return;
    }
  }, [user, isLoading, router, logout]);

  useEffect(() => {
    if (user) {
      // Simulate data loading
      const timer = setTimeout(() => {
        setDashboardData({
          totalUsers: 156,
          activeVehicles: 42,
          ordersThisMonth: 89,
          systemHealth: 98.5,
          userGrowth: [
            { month: 'Jan', users: 400 },
            { month: 'Feb', users: 520 },
            { month: 'Mar', users: 615 },
            { month: 'Apr', users: 780 },
          ],
          salesData: [
            { name: 'Standard', value: 35 },
            { name: 'Premium', value: 45 },
            { name: 'Luxury', value: 20 },
          ],
        });
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

  if (!user || user.role !== 'ADMIN') {
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
          <div className="flex items-center gap-3">
            <div className="bg-red-600 p-2 rounded-lg">
              <BarChart3 className="text-white" size={28} />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
              <p className="text-gray-600 text-sm mt-1">Welcome back, {user?.username || 'Administrator'}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main KPIs */}
          <div className="lg:col-span-2">
            {dashboardData && <AdminKPIs data={dashboardData} />}
          </div>

          {/* Sidebar */}
          <div className="space-y-6">

            {/* Quick Stats & Register User */}
            <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm space-y-4">
              <h3 className="text-lg font-semibold mb-4 text-gray-900">Quick Stats</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-3 bg-red-50 rounded-lg border border-red-200">
                  <div className="flex items-center gap-2">
                    <Users className="text-red-600" size={20} />
                    <span className="text-gray-700 text-sm">New Users (7d)</span>
                  </div>
                  <span className="font-bold text-lg text-red-600">+23</span>
                </div>
                <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg border border-green-200">
                  <div className="flex items-center gap-2">
                    <TrendingUp className="text-green-600" size={20} />
                    <span className="text-gray-700 text-sm">Revenue (7d)</span>
                  </div>
                  <span className="font-bold text-lg text-green-600">+$4.2K</span>
                </div>
              </div>
              <button
                className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg font-semibold transition mt-4"
                onClick={() => router.push('/register')}
              >
                Register New User
              </button>
            </div>

            {/* Recent Activity */}
            <ActivityFeed />
          </div>
        </div>
      </div>
    </div>
  );
}
