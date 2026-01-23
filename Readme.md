
# 🌍 Travel Buddy – Group Travel Planning Application

## 📌 Overview

**Travel Buddy** is a web-based group travel planning application that enables users to create travel groups, communicate in real time, and collaborate efficiently.
The application focuses on **real-time interaction**, **role-based access control**, and **usability**, making it suitable for organizing group trips in a structured and secure way.
---

## ✨ Features

### User Features

* Secure user registration and login (JWT-based)
* Create and join travel groups
* Real-time group chat
* Typing indicators and online presence
* Responsive UI for desktop and mobile devices

### Admin Features

* Delete individual chat messages
* Delete all messages in a group
* Moderate group communication

---

## 🛠️ Tech Stack

### Frontend

* **React** + **TypeScript**
* Tailwind CSS
* STOMP over SockJS (WebSocket client)

### Backend

* **Spring Boot**
* RESTful APIs
* WebSocket (STOMP protocol)
* JWT authentication & authorization

### Database

* Relational SQL database
* Persistent storage for users, groups, memberships, and messages

---

## 🧱 Architecture

The system follows a **layered architecture**:

* **Client Layer:** Browser-based React frontend
* **Application Layer:** Spring Boot REST API
* **Real-Time Layer:** WebSocket server for chat & presence
* **Persistence Layer:** SQL database

This structure improves maintainability, scalability, and separation of concerns.

---

## ⚙️ Installation & Setup

### Prerequisites

* Node.js (v18+ recommended)
* Java 17+
* SQL database (e.g., MySQL / PostgreSQL)

---

### Backend Setup

1. Configure database credentials in `application.properties`
2. Start the backend:

   ```bash
   ./mvnw spring-boot:run
   ```
3. Backend runs on:

   ```
   http://localhost:8080
   ```

---

### Frontend Setup

1. Install dependencies:

   ```bash
   npm install
   ```
2. Start the frontend:

   ```bash
   npm run dev
   ```
3. Access the app at:

   ```
   http://localhost:5173
   ```

---

## 🧪 Testing

The application was tested manually using predefined test cases covering:

* User authentication and authorization
* Group creation and membership handling
* Real-time message delivery
* Typing indicator behavior
* Admin-only delete actions
* WebSocket reconnection after refresh

All core features were verified to work as intended under normal usage conditions.

---

## 🔐 Security Considerations

* JWT-based authentication for all protected endpoints
* Role-based authorization (Admin / Member)
* Validation of group membership for all chat and delete actions
* WebSocket access restricted to authenticated users

---

### Possible Future Enhancements

* Automated unit and integration tests
* File sharing in group chats
* Collaborative itinerary planning
* Sending Emails


