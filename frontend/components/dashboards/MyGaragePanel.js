'use client';

import { Edit3, Trash2, Plus, Fuel, Gauge, Calendar } from 'lucide-react';

export default function MyGaragePanel({ vehicles = [] }) {
  const defaultVehicles = [
    {
      id: 1,
      name: 'Tesla Model 3',
      year: 2023,
      color: 'Midnight Black',
      mileage: 12500,
      status: 'Active',
      type: 'Electric',
      icon: '⚡',
    },
    {
      id: 2,
      name: 'BMW M440i',
      year: 2022,
      color: 'Alpine White',
      mileage: 28300,
      status: 'Maintenance',
      type: 'Sports',
      icon: '🏎️',
    },
  ];

  const displayVehicles = vehicles.length > 0 ? vehicles : defaultVehicles;

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold text-gray-900">My Garage</h2>
          <p className="text-gray-600 text-sm mt-1">{displayVehicles.length} vehicles registered</p>
        </div>
        <button className="flex items-center gap-2 bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-lg transition shadow-sm font-semibold">
          <Plus size={20} />
          Add Vehicle
        </button>
      </div>

      {/* Vehicles Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {displayVehicles.map((vehicle) => (
          <div
            key={vehicle.id}
            className="group rounded-2xl overflow-hidden border border-gray-200 hover:border-gray-300 transition duration-300 shadow-sm hover:shadow-md bg-white"
          >
            {/* Header with gradient */}
            <div className="bg-gradient-to-r from-gray-50 to-gray-100 p-6 border-b border-gray-200">
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h3 className="text-2xl font-bold text-gray-900">{vehicle.name}</h3>
                  <p className="text-gray-600 text-sm mt-1">{vehicle.color}</p>
                </div>
                <div className="text-4xl">{vehicle.icon}</div>
              </div>
              <div className="flex items-center justify-between pt-3 border-t border-gray-200">
                <span className="text-gray-600 text-sm">{vehicle.year} • {vehicle.type}</span>
                <span
                  className={`px-3 py-1 rounded-full text-xs font-bold ${
                    vehicle.status === 'Active'
                      ? 'bg-green-100 text-green-700 border border-green-300'
                      : 'bg-amber-100 text-amber-700 border border-amber-300'
                  }`}
                >
                  {vehicle.status}
                </span>
              </div>
            </div>

            {/* Image placeholder */}
            <div className="h-48 bg-gradient-to-br from-slate-100 to-slate-200 flex items-center justify-center group-hover:from-slate-200 group-hover:to-slate-300 transition">
              <div className="text-center">
                <div className="text-6xl mb-3">{vehicle.icon}</div>
                <p className="text-gray-500 text-sm">{vehicle.name}</p>
              </div>
            </div>

            {/* Details */}
            <div className="bg-gray-50 p-6 space-y-4">
              {/* Mileage */}
              <div className="flex items-center justify-between p-3 bg-white rounded-lg border border-gray-200">
                <div className="flex items-center gap-3">
                  <Gauge className="text-gray-500" size={20} />
                  <span className="text-gray-600 text-sm">Mileage</span>
                </div>
                <span className="font-bold text-gray-900">{vehicle.mileage.toLocaleString()} km</span>
              </div>

              {/* Fuel Type */}
              <div className="flex items-center justify-between p-3 bg-white rounded-lg border border-gray-200">
                <div className="flex items-center gap-3">
                  <Fuel className="text-emerald-600" size={20} />
                  <span className="text-gray-600 text-sm">Type</span>
                </div>
                <span className="font-bold text-gray-900">{vehicle.type}</span>
              </div>

              {/* Year */}
              <div className="flex items-center justify-between p-3 bg-white rounded-lg border border-gray-200">
                <div className="flex items-center gap-3">
                  <Calendar className="text-purple-600" size={20} />
                  <span className="text-gray-600 text-sm">Year</span>
                </div>
                <span className="font-bold text-gray-900">{vehicle.year}</span>
              </div>
            </div>

            {/* Actions */}
            <div className="bg-gray-50 px-6 py-4 border-t border-gray-200 flex gap-3">
              <button className="flex-1 flex items-center justify-center gap-2 bg-gray-100 hover:bg-gray-200 text-gray-700 hover:text-gray-800 py-2 rounded-lg transition border border-gray-300">
                <Edit3 size={16} />
                Edit
              </button>
              <button className="flex-1 flex items-center justify-center gap-2 bg-red-100 hover:bg-red-200 text-red-700 hover:text-red-800 py-2 rounded-lg transition border border-red-300">
                <Trash2 size={16} />
                Remove
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Empty state if needed */}
      {displayVehicles.length === 0 && (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">🚗</div>
          <p className="text-white/50 text-lg">No vehicles in your garage yet</p>
          <button className="mt-6 bg-red-600 hover:bg-red-700 text-white px-6 py-2 rounded-lg transition">
            Add Your First Vehicle
          </button>
        </div>
      )}
    </div>
  );
}
