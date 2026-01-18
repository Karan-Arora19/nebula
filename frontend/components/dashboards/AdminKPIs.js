'use client';

import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { Users, TrendingUp, AlertCircle, CheckCircle, ArrowUpRight, ArrowDownRight } from 'lucide-react';

export default function AdminKPIs({ data = {} }) {
  const {
    totalUsers = 156,
    activeVehicles = 42,
    ordersThisMonth = 89,
    systemHealth = 98.5,
    userGrowth = [
      { month: 'Jan', users: 400 },
      { month: 'Feb', users: 520 },
      { month: 'Mar', users: 615 },
      { month: 'Apr', users: 780 },
    ],
    salesData = [
      { name: 'Standard', value: 35 },
      { name: 'Premium', value: 45 },
      { name: 'Luxury', value: 20 },
    ],
  } = data;

  const COLORS = ['#dc2626', '#6b7280', '#9ca3af'];

  return (
    <div className="space-y-8">
      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Total Users Card */}
        <div className="group relative rounded-2xl overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-br from-red-600 to-red-700 opacity-90"></div>
          <div className="absolute inset-0 bg-grid-white/10 opacity-0 group-hover:opacity-100 transition"></div>
          <div className="relative p-6 text-white">
            <div className="flex items-start justify-between mb-4">
              <div className="p-3 bg-white/20 rounded-lg backdrop-blur-sm">
                <Users className="text-white" size={24} />
              </div>
              <div className="flex items-center gap-1 text-green-300 text-sm font-semibold">
                <ArrowUpRight size={16} />
                +12%
              </div>
            </div>
            <p className="text-white/80 text-sm font-medium">Total Users</p>
            <p className="text-4xl font-bold mt-2">{totalUsers}</p>
            <p className="text-white/70 text-xs mt-3">From last month</p>
          </div>
        </div>

        {/* Active Vehicles Card */}
        <div className="group relative rounded-2xl overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-br from-emerald-600 to-emerald-800 opacity-90"></div>
          <div className="relative p-6 text-white">
            <div className="flex items-start justify-between mb-4">
              <div className="p-3 bg-white/20 rounded-lg backdrop-blur-sm">
                <TrendingUp className="text-emerald-100" size={24} />
              </div>
              <div className="flex items-center gap-1 text-green-300 text-sm font-semibold">
                <ArrowUpRight size={16} />
                +8%
              </div>
            </div>
            <p className="text-emerald-100/70 text-sm font-medium">Active Vehicles</p>
            <p className="text-4xl font-bold mt-2">{activeVehicles}</p>
            <p className="text-emerald-200/60 text-xs mt-3">Operating normally</p>
          </div>
        </div>

        {/* Orders Card */}
        <div className="group relative rounded-2xl overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-br from-orange-600 to-orange-800 opacity-90"></div>
          <div className="relative p-6 text-white">
            <div className="flex items-start justify-between mb-4">
              <div className="p-3 bg-white/20 rounded-lg backdrop-blur-sm">
                <AlertCircle className="text-orange-100" size={24} />
              </div>
              <div className="flex items-center gap-1 text-green-300 text-sm font-semibold">
                <ArrowUpRight size={16} />
                +24%
              </div>
            </div>
            <p className="text-orange-100/70 text-sm font-medium">Orders This Month</p>
            <p className="text-4xl font-bold mt-2">{ordersThisMonth}</p>
            <p className="text-orange-200/60 text-xs mt-3">Total orders</p>
          </div>
        </div>

        {/* System Health Card */}
        <div className="group relative rounded-2xl overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-br from-violet-600 to-violet-800 opacity-90"></div>
          <div className="relative p-6 text-white">
            <div className="flex items-start justify-between mb-4">
              <div className="p-3 bg-white/20 rounded-lg backdrop-blur-sm">
                <CheckCircle className="text-violet-100" size={24} />
              </div>
              <div className="flex items-center gap-1 text-green-300 text-sm font-semibold">
                <ArrowUpRight size={16} />
                +2%
              </div>
            </div>
            <p className="text-violet-100/70 text-sm font-medium">System Health</p>
            <p className="text-4xl font-bold mt-2">{systemHealth.toFixed(1)}%</p>
            <p className="text-violet-200/60 text-xs mt-3">All systems optimal</p>
          </div>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* User Growth Chart */}
        <div className="rounded-2xl bg-white border border-gray-200 p-8 shadow-sm">
          <div className="mb-6">
            <h3 className="text-xl font-bold text-gray-900 mb-1">User Growth</h3>
            <p className="text-gray-600 text-sm">Last 4 months trend</p>
          </div>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={userGrowth}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.1)" />
              <XAxis dataKey="month" stroke="rgba(0,0,0,0.5)" />
              <YAxis stroke="rgba(0,0,0,0.5)" />
              <Tooltip 
                contentStyle={{ 
                  backgroundColor: 'white', 
                  border: '1px solid rgba(0,0,0,0.1)', 
                  borderRadius: '12px',
                  color: '#000'
                }} 
              />
              <Line type="monotone" dataKey="users" stroke="#dc2626" strokeWidth={3} dot={{ fill: '#dc2626', r: 6 }} activeDot={{ r: 8 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Sales Distribution */}
        <div className="rounded-2xl bg-white border border-gray-200 p-8 shadow-sm">
          <div className="mb-6">
            <h3 className="text-xl font-bold text-gray-900 mb-1">Sales Distribution</h3>
            <p className="text-gray-600 text-sm">Product category breakdown</p>
          </div>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={salesData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value }) => `${name} ${value}%`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {salesData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ backgroundColor: 'white', border: '1px solid rgba(0,0,0,0.1)', borderRadius: '12px' }} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
