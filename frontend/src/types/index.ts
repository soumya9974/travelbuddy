export interface User {
  id: number;
  username: string;
  email: string;
  name?: string;
  bio?: string;
  interests?: string;
  destinations?: string;
  verified?: boolean;
  createdAt?: string;
}

export interface TravelGroup {
  id: number;
  name: string;
  destination: string;
  description: string;
  interest?: string;
  startDate: string;
  endDate: string;
  creatorId: number;
  creator?: User;
  memberCount?: number;
  createdAt?: string;
}

export interface Membership {
  id: number;
  role: string;
  joinedAt: string;
  userId: number;
  userName: string;
  groupId: number;
  groupName: string;
}

export interface Message {
  id: number;
  senderId: number;
  senderName: string;
  groupId: number;
  content: string;
  timestamp: string;
}

export interface Itinerary {
  id: number;
  groupId: number;
  day: number;
  title: string;
  description: string;
  location: string;
  startTime?: string;
  endTime?: string;
}

export interface ChatMessage {
  id?: number;
  groupId: number;
  senderId: number;
  senderName: string;
  content: string;
  timestamp: string;
  type?: "CHAT" | "JOIN" | "LEAVE" | "TYPING" | "DELETE" | "DELETE_ALL";
}

export interface AuthResponse {
  token: string;
  user: User;
}
