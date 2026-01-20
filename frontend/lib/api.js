// API configuration and utilities

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const apiClient = {
  async request(endpoint, options = {}) {
    const token = typeof window !== 'undefined' ? localStorage.getItem('authToken') : null;
    
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers,
      });

      if (!response.ok) {
        throw new Error(`API Error: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      // Silently fail for network errors - callers should have fallbacks
      console.debug('API request failed (expected in dev mode):', error.message);
      throw error;
    }
  },

  get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  },

  post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  put(endpoint, data) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  },
};

// Auth API
export const authApi = {
  async login(email, password) {
    try {
      return await apiClient.post('/users/login', { email, password });
    } catch (error) {
      // Fallback to mock login
      console.log('Using mock login due to API unavailability');
      if (email === 'admin@nebula.com' && password === 'admin123') {
        return {
          token: 'mock-token',
          user: {
            id: 1,
            email,
            username: 'admin',
            role: 'ADMIN',
          },
        };
      }
      if (email === 'user@nebula.com' && password === 'user123') {
        return {
          token: 'mock-token',
          user: {
            id: 2,
            email,
            username: 'user',
            role: 'USER',
          },
        };
      }
      throw new Error('Invalid credentials');
    }
  },

  register: (user) =>
    apiClient.post('/users/register', user),

  logout: (token) =>
    apiClient.post('/users/logout', { token }),
};

// Admin API
export const adminApi = {
  getUsers: () =>
    apiClient.get('/admin/users').catch(() => ({
      users: [],
    })),

  getUser: (id) =>
    apiClient.get(`/admin/users/${id}`).catch(() => ({
      user: null,
    })),

  updateUserRole: (userId, role) =>
    apiClient.put(`/admin/users/${userId}/role`, { role }),

  deleteUser: (userId) =>
    apiClient.delete(`/admin/users/${userId}`),
};

// Vehicle API
export const vehicleApi = {
  getVehicles: () =>
    apiClient.get('/vehicles').catch(() => ({
      vehicles: [],
    })),

  getVehicle: (id) =>
    apiClient.get(`/vehicles/${id}`).catch(() => ({
      vehicle: null,
    })),

  createVehicle: (vehicle) =>
    apiClient.post('/vehicles', vehicle),

  updateVehicle: (id, vehicle) =>
    apiClient.put(`/vehicles/${id}`, vehicle),

  deleteVehicle: (id) =>
    apiClient.delete(`/vehicles/${id}`),
};

// Merchandise API
export const merchandiseApi = {
  getProducts: () =>
    apiClient.get('/merchandise').catch(() => ({
      products: [],
    })),

  getProduct: (id) =>
    apiClient.get(`/merchandise/${id}`).catch(() => ({
      product: null,
    })),
};
