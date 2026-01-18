'use client';

import { Activity, ChevronRight } from 'lucide-react';

export default function ActivityFeed({ activities = [] }) {
  const defaultActivities = [
    {
      id: 1,
      type: 'configuration',
      title: 'Created new car configuration',
      description: 'Tesla Model 3 with custom wheels',
      timestamp: '2 hours ago',
      icon: '⚙️',
      color: 'from-red-600 to-red-700',
    },
    {
      id: 2,
      type: 'purchase',
      title: 'Completed merchandise order',
      description: 'Premium carbon fiber spoiler - $899',
      timestamp: '1 day ago',
      icon: '🛒',
      color: 'from-emerald-600 to-emerald-700',
    },
    {
      id: 3,
      type: 'world-drive',
      title: 'Shared world drive journey',
      description: 'Cross-country trip - 2,450 km',
      timestamp: '3 days ago',
      icon: '🌍',
      color: 'from-purple-600 to-purple-700',
    },
    {
      id: 4,
      type: 'achievement',
      title: 'Earned "Speed Demon" badge',
      description: 'Completed 50 world drive sessions',
      timestamp: '5 days ago',
      icon: '🏆',
      color: 'from-amber-600 to-amber-700',
    },
  ];

  const displayActivities = activities.length > 0 ? activities : defaultActivities;

  return (
    <div className="rounded-2xl bg-white border border-gray-200 p-8 shadow-sm">
      <div className="flex items-center gap-3 mb-6">
        <div className="p-3 bg-red-100 rounded-lg">
          <Activity className="text-red-600" size={24} />
        </div>
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Recent Activity</h2>
          <p className="text-gray-600 text-sm mt-1">Your latest actions and updates</p>
        </div>
      </div>

      <div className="space-y-3 max-h-96 overflow-y-auto">
        {displayActivities.map((activity, index) => (
          <div
            key={activity.id}
            className="group relative rounded-xl overflow-hidden transition duration-300 hover:shadow-md"
          >
            <div className={`absolute inset-0 bg-gradient-to-r ${activity.color} opacity-0 group-hover:opacity-5 transition`}></div>
            <div className={`relative border border-gray-200 group-hover:border-gray-300 rounded-xl p-4 transition bg-white`}>
              <div className="flex items-start gap-4">
                {/* Icon */}
                <div className="text-3xl flex-shrink-0 mt-1">{activity.icon}</div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold text-gray-900 group-hover:text-red-600 transition">{activity.title}</h3>
                  <p className="text-sm text-gray-600 mt-1">{activity.description}</p>
                  <p className="text-xs text-gray-400 mt-2">{activity.timestamp}</p>
                </div>

                {/* Status indicator for latest */}
                {index === 0 && (
                  <div className="flex-shrink-0">
                    <div className="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
                  </div>
                )}

                {/* Hover arrow */}
                <ChevronRight className="text-gray-400 group-hover:text-gray-600 transition flex-shrink-0" size={20} />
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* View All Link */}
      <button className="w-full mt-6 text-center bg-gray-50 hover:bg-gray-100 text-gray-700 hover:text-gray-900 font-semibold py-3 rounded-lg transition border border-gray-200">
        View All Activity
      </button>
    </div>
  );
}
