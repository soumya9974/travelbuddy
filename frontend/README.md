# Travel Buddy - Collaborative Travel Planning Platform

A full-stack web application for organizing group travel with real-time chat, itinerary planning, and collaborative features.

## Features

- **User Authentication**: Secure JWT-based login and registration
- **Travel Groups**: Create and manage travel groups with destinations and dates
- **Real-time Chat**: WebSocket-powered instant messaging within groups
- **Itinerary Planning**: Collaborative day-by-day trip planning
- **Membership Management**: Role-based access (Admin/Member)
- **Responsive Design**: Works seamlessly on desktop and mobile devices

## Technology Stack

### Frontend
- React 18 + TypeScript
- Vite (Build tool)
- Tailwind CSS (Styling)
- STOMP/SockJS (WebSocket)

### Backend
- Spring Boot 3.5.4
- Java 21
- Spring Data JPA
- Spring Security + JWT
- Spring WebSocket

### Database
- PostgreSQL (via Supabase)
- Row Level Security (RLS)

## Quick Start

### Prerequisites
- Node.js 18+
- Java 21
- Maven 3.8+

### 1. Install Dependencies
```bash
npm install
cd backend/travelBuddy
./mvnw clean install
cd ../..
```

### 2. Configure Database
Edit `backend/travelBuddy/src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_SUPABASE_PASSWORD
```

### 3. Start Servers

**Terminal 1 - Backend:**
```bash
cd backend/travelBuddy
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
npm run dev
```

### 4. Open Application
Visit: http://localhost:5173

## Documentation

- **[Quick Start Guide](QUICK_START.md)** - Get up and running in 5 minutes
- **[Setup & Usage Guide](SETUP_AND_USAGE.md)** - Detailed setup and usage instructions
- **[Architecture Overview](ARCHITECTURE.md)** - System architecture and technical details

## Project Structure

```
travel-buddy/
├── src/                          # React frontend
│   ├── components/               # Reusable UI components
│   ├── pages/                    # Page components
│   ├── services/                 # API services
│   ├── context/                  # React Context (Auth)
│   ├── hooks/                    # Custom hooks
│   └── types/                    # TypeScript types
├── backend/travelBuddy/          # Spring Boot backend
│   └── src/main/java/com/travelbuddy/
│       ├── controller/           # REST controllers
│       ├── service/              # Business logic
│       ├── repository/           # Data access
│       ├── model/                # JPA entities
│       ├── security/             # JWT & security config
│       └── config/               # WebSocket config
├── QUICK_START.md                # Quick setup guide
├── SETUP_AND_USAGE.md            # Detailed documentation
└── ARCHITECTURE.md               # Architecture details
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login

### Users
- `GET /api/users` - Get all users
- `POST /api/users` - Create user (register)
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Travel Groups
- `GET /api/groups` - Get user's groups
- `POST /api/groups` - Create group
- `GET /api/groups/{id}` - Get group details
- `PUT /api/groups/{id}` - Update group
- `DELETE /api/groups/{id}` - Delete group

### Messages
- `GET /api/groups/{id}/messages` - Get group messages
- `POST /api/messages` - Send message
- WebSocket: `/ws` (STOMP endpoint)

### Itineraries
- `GET /api/groups/{id}/itineraries` - Get group itinerary
- `POST /api/itineraries` - Create itinerary item
- `PUT /api/itineraries/{id}` - Update itinerary
- `DELETE /api/itineraries/{id}` - Delete itinerary

### Memberships
- `GET /api/groups/{id}/memberships` - Get group members
- `POST /api/memberships` - Add member
- `DELETE /api/memberships/{id}` - Remove member

## Testing

### Run Backend Tests
```bash
cd backend/travelBuddy
./mvnw test
```

### Run Frontend Tests
```bash
npm test
```

Test coverage includes:
- User service operations
- Travel group management
- API integration tests
- Component unit tests

## How It Works

### User Flow
1. **Register/Login**: Create account or log in with credentials
2. **Create Group**: Set up a new travel group with destination and dates
3. **Invite Members**: Add friends to the group
4. **Plan Together**: Collaborate on itinerary planning
5. **Chat**: Communicate in real-time with group members

### Authentication Flow
1. User logs in with email/password
2. Backend validates credentials
3. JWT token generated and returned
4. Token stored in localStorage
5. Token included in all subsequent API requests

### Real-time Chat
1. WebSocket connection established on login
2. User joins group chat rooms
3. Messages sent via STOMP protocol
4. Server broadcasts to all group members
5. Messages instantly appear in all clients

## Database Schema

The application uses 5 main tables:

- **users**: User accounts and profiles
- **travel_groups**: Travel group information
- **memberships**: User-group relationships with roles
- **messages**: Chat messages
- **itineraries**: Trip activity planning

All tables have Row Level Security (RLS) enabled for data protection.

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Hashing**: Bcrypt password encryption
- **CORS Protection**: Configured allowed origins
- **Row Level Security**: Database-level access control
- **Role-based Access**: Admin and Member roles in groups

## Development

### Run in Development Mode
```bash
# Backend (auto-reload with devtools)
cd backend/travelBuddy
./mvnw spring-boot:run

# Frontend (hot reload with Vite)
npm run dev
```

### API Documentation
Visit: http://localhost:8080/swagger-ui.html

### Database Management
Visit: https://supabase.com/dashboard

## Building for Production

### Build Frontend
```bash
npm run build
```
Output: `dist/` folder

### Build Backend
```bash
cd backend/travelBuddy
./mvnw clean package
```
Output: `target/app-0.0.1-SNAPSHOT.jar`

### Run Production Build
```bash
java -jar backend/travelBuddy/target/app-0.0.1-SNAPSHOT.jar
```

## Environment Variables

### Frontend (.env)
```env
VITE_SUPABASE_URL=https://rmqlfsrlnljqbekeimou.supabase.co
VITE_SUPABASE_ANON_KEY=your-anon-key
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_BASE_URL=ws://localhost:8080/ws
```

### Backend (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://db.rmqlfsrlnljqbekeimou.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your-password
jwt.secret=your-secret-key
```

## Troubleshooting

### Common Issues

**Backend won't start**
- Verify Java 21 is installed: `java -version`
- Check database credentials
- Ensure port 8080 is available

**Frontend won't start**
- Verify Node 18+ is installed: `node -version`
- Run `npm install` again
- Ensure port 5173 is available

**CORS errors**
- Ensure backend is running
- Check CORS configuration in SecurityConfig.java
- Verify frontend URL matches allowed origin

**WebSocket connection fails**
- Check backend is running and accessible
- Verify WebSocket endpoint: ws://localhost:8080/ws
- Check browser console for errors

## Contributing

This is a student project. Feel free to use it for learning purposes.

## License

Educational use only.

## Key URLs

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **API Docs**: http://localhost:8080/swagger-ui.html
- **WebSocket**: ws://localhost:8080/ws
- **Database Dashboard**: https://supabase.com/dashboard

## Support

For detailed information, see:
- [QUICK_START.md](QUICK_START.md) - Quick setup
- [SETUP_AND_USAGE.md](SETUP_AND_USAGE.md) - Detailed guide
- [ARCHITECTURE.md](ARCHITECTURE.md) - Technical details

---

Built with Spring Boot, React, and Supabase
