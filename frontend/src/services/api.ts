import { API_BASE_URL } from '../config/api';
import { User, TravelGroup, Message, Itinerary, Membership } from '../types';

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
  };
};

export const api = {
  auth: {
    login: async (email: string, password: string) => {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      if (!response.ok) throw new Error('Login failed');
      const data = await response.json();
      const token = data.token ?? data.accessToken;
      if (token) localStorage.setItem("token", token);
      return data;
    },

    register: async (userData: { name: string; email: string; password: string }) => {
      const response = await fetch(`${API_BASE_URL}/users`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: userData.name,
          email: userData.email,
          passwordHash: userData.password,
          bio: '',
          profilePicUrl: '',
          verificationStatus: false,
        }),
      });
      if (!response.ok) throw new Error('Registration failed');
      return response.json();
    },
  },

  users: {
    getById: async (id: number): Promise<User> => {
      const response = await fetch(`${API_BASE_URL}/users/${id}`, {
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to fetch user');
      return response.json();
    },

    update: async (id: number, userData: Partial<User>): Promise<User> => {
      const response = await fetch(`${API_BASE_URL}/users/${id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(userData),
      });
      if (!response.ok) throw new Error('Failed to update user');
      return response.json();
    },
  },

  groups: {
    getAll: async (): Promise<TravelGroup[]> => {
      const response = await fetch(`${API_BASE_URL}/groups`, {
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to fetch groups');
      return response.json();
    },

    getById: async (id: number): Promise<TravelGroup> => {
      const response = await fetch(`${API_BASE_URL}/groups/${id}`, {
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to fetch group');
      return response.json();
    },

    create: async (groupData: Omit<TravelGroup, 'id' | 'createdAt'>): Promise<TravelGroup> => {
      const response = await fetch(`${API_BASE_URL}/groups`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(groupData),
      });
      if (!response.ok) throw new Error('Failed to create group');
      return response.json();
    },

    update: async (id: number, groupData: Partial<TravelGroup>): Promise<TravelGroup> => {
      const response = await fetch(`${API_BASE_URL}/groups/${id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(groupData),
      });
      if (!response.ok) throw new Error('Failed to update group');
      return response.json();
    },

    delete: async (id: number): Promise<void> => {
      const response = await fetch(`${API_BASE_URL}/groups/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to delete group');
    },
  },

  memberships: {
    getByGroup: async (groupId: number): Promise<Membership[]> => {
      const response = await fetch(`${API_BASE_URL}/groups/${groupId}/memberships`, {
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to fetch memberships');
      return response.json();
    },

    add: async (groupId: number, userId: number, role: 'ADMIN' | 'MEMBER'): Promise<Membership> => {
      const response = await fetch(`${API_BASE_URL}/memberships`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ groupId, userId, role }),
      });
      if (!response.ok) throw new Error('Failed to add member');
      return response.json();
    },

    remove: async (membershipId: number): Promise<void> => {
      const response = await fetch(`${API_BASE_URL}/memberships/${membershipId}`, {
        method: 'DELETE',
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to remove member');
    },
  },

  messages: {
    getByGroup: async (groupId: number): Promise<Message[]> => {
      const response = await fetch(`${API_BASE_URL}/groups/${groupId}/messages`, {
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to fetch messages');
      return response.json();
    },

    send: async (groupId: number, content: string): Promise<Message> => {
      const response = await fetch(`${API_BASE_URL}/messages`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ groupId, content }),
      });
      if (!response.ok) throw new Error('Failed to send message');
      return response.json();
    },
  },

  itineraries: {
    getByGroup: async (groupId: number): Promise<Itinerary[]> => {
      const response = await fetch(`${API_BASE_URL}/groups/${groupId}/itineraries`, {
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to fetch itineraries');
      return response.json();
    },

    create: async (itineraryData: Omit<Itinerary, 'id'>): Promise<Itinerary> => {
      const response = await fetch(`${API_BASE_URL}/itineraries`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(itineraryData),
      });
      if (!response.ok) throw new Error('Failed to create itinerary');
      return response.json();
    },

    update: async (id: number, itineraryData: Partial<Itinerary>): Promise<Itinerary> => {
      const response = await fetch(`${API_BASE_URL}/itineraries/${id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(itineraryData),
      });
      if (!response.ok) throw new Error('Failed to update itinerary');
      return response.json();
    },

    delete: async (id: number): Promise<void> => {
      const response = await fetch(`${API_BASE_URL}/itineraries/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders(),
      });
      if (!response.ok) throw new Error('Failed to delete itinerary');
    },
  },
};
