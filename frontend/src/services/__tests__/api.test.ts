import { api } from '../api';
import { API_BASE_URL } from '../../config/api';

global.fetch = jest.fn();

describe('API Service', () => {
  beforeEach(() => {
    (fetch as jest.Mock).mockClear();
    localStorage.clear();
  });

  describe('Authentication', () => {
    it('should login successfully', async () => {
      const mockResponse = {
        token: 'test-token',
        user: { id: 1, name: 'John Doe', email: 'john@example.com' },
      };

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      });

      const result = await api.auth.login('john@example.com', 'password123');

      expect(fetch).toHaveBeenCalledWith(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: 'john@example.com', password: 'password123' }),
      });
      expect(result).toEqual(mockResponse);
    });

    it('should throw error on failed login', async () => {
      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 401,
      });

      await expect(api.auth.login('wrong@example.com', 'wrong')).rejects.toThrow('Login failed');
    });

    it('should register user successfully', async () => {
      const mockUser = {
        id: 1,
        name: 'Jane Doe',
        email: 'jane@example.com',
      };

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockUser,
      });

      const userData = {
        name: 'Jane Doe',
        email: 'jane@example.com',
        password: 'password123',
      };

      const result = await api.auth.register(userData);

      expect(fetch).toHaveBeenCalledWith(`${API_BASE_URL}/users`, expect.any(Object));
      expect(result).toEqual(mockUser);
    });
  });

  describe('Users', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token');
    });

    it('should get user by id', async () => {
      const mockUser = {
        id: 1,
        name: 'John Doe',
        email: 'john@example.com',
      };

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockUser,
      });

      const result = await api.users.getById(1);

      expect(fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/users/1`,
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer test-token',
          }),
        })
      );
      expect(result).toEqual(mockUser);
    });

    it('should update user', async () => {
      const updatedUser = {
        id: 1,
        name: 'John Updated',
        email: 'john@example.com',
      };

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => updatedUser,
      });

      const result = await api.users.update(1, { name: 'John Updated' });

      expect(fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/users/1`,
        expect.objectContaining({
          method: 'PUT',
        })
      );
      expect(result.name).toBe('John Updated');
    });
  });

  describe('Groups', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token');
    });

    it('should get all groups', async () => {
      const mockGroups = [
        { id: 1, name: 'Europe Trip', destination: 'Paris' },
        { id: 2, name: 'Asia Adventure', destination: 'Tokyo' },
      ];

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockGroups,
      });

      const result = await api.groups.getAll();

      expect(fetch).toHaveBeenCalledWith(`${API_BASE_URL}/groups`, expect.any(Object));
      expect(result).toHaveLength(2);
      expect(result[0].name).toBe('Europe Trip');
    });

    it('should create group', async () => {
      const newGroup = {
        name: 'Beach Vacation',
        destination: 'Maldives',
        description: 'Relaxing beach trip',
        startDate: '2024-08-01',
        endDate: '2024-08-10',
        creatorId: 1,
      };

      const mockResponse = { id: 3, ...newGroup };

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      });

      const result = await api.groups.create(newGroup);

      expect(fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/groups`,
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify(newGroup),
        })
      );
      expect(result.id).toBe(3);
      expect(result.name).toBe('Beach Vacation');
    });

    it('should delete group', async () => {
      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
      });

      await api.groups.delete(1);

      expect(fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/groups/1`,
        expect.objectContaining({
          method: 'DELETE',
        })
      );
    });
  });

  describe('Messages', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token');
    });

    it('should get messages by group', async () => {
      const mockMessages = [
        { id: 1, content: 'Hello!', senderId: 1, groupId: 1 },
        { id: 2, content: 'Hi there!', senderId: 2, groupId: 1 },
      ];

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockMessages,
      });

      const result = await api.messages.getByGroup(1);

      expect(fetch).toHaveBeenCalledWith(`${API_BASE_URL}/groups/1/messages`, expect.any(Object));
      expect(result).toHaveLength(2);
    });

    it('should send message', async () => {
      const mockMessage = {
        id: 3,
        content: 'New message',
        senderId: 1,
        groupId: 1,
      };

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockMessage,
      });

      const result = await api.messages.send(1, 'New message');

      expect(fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/messages`,
        expect.objectContaining({
          method: 'POST',
        })
      );
      expect(result.content).toBe('New message');
    });
  });

  describe('Itineraries', () => {
    beforeEach(() => {
      localStorage.setItem('token', 'test-token');
    });

    it('should get itineraries by group', async () => {
      const mockItineraries = [
        {
          id: 1,
          groupId: 1,
          day: 1,
          title: 'Visit Eiffel Tower',
          location: 'Paris',
        },
      ];

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockItineraries,
      });

      const result = await api.itineraries.getByGroup(1);

      expect(fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/groups/1/itineraries`,
        expect.any(Object)
      );
      expect(result).toHaveLength(1);
      expect(result[0].title).toBe('Visit Eiffel Tower');
    });

    it('should create itinerary', async () => {
      const newItinerary = {
        groupId: 1,
        day: 2,
        title: 'Louvre Museum',
        description: 'Art museum visit',
        location: 'Paris',
        startTime: '10:00',
        endTime: '14:00',
      };

      const mockResponse = { id: 2, ...newItinerary };

      (fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      });

      const result = await api.itineraries.create(newItinerary);

      expect(fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/itineraries`,
        expect.objectContaining({
          method: 'POST',
        })
      );
      expect(result.id).toBe(2);
      expect(result.title).toBe('Louvre Museum');
    });
  });
});
