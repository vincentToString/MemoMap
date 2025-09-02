# Travel Journal Application

A full-stack travel journaling application that allows users to create, manage, and enrich their travel memories with location data, weather information, tags, and ratings.

## Architecture

This application consists of two main components:

- **Backend**: Spring Boot REST API (`/journal`)
- **Frontend**: Next.js React application (`/frontend`)

## Backend (Spring Boot)

### Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Java Version**: 21
- **Database**: PostgreSQL
- **Authentication**: OAuth2 with Google + JWT
- **Caching**: Redis
- **Build Tool**: Maven

### Key Features

#### Authentication & Security
- **OAuth2 Google Authentication**: Users authenticate via Google OAuth2
- **JWT Tokens**: Access and refresh token implementation
- **Security Configuration**: Stateless session management with JWT validation
- **User Management**: Automatic user creation on first OAuth2 login

#### Travel Memo Management
- **CRUD Operations**: Create, read, update, delete travel memos
- **Rich Content**: Support for titles, content, images, ratings, and mood icons
- **Location Data**: Integration with location entities and geographic services
- **Weather Integration**: Historical weather data for travel dates
- **Tagging System**: Flexible tagging with automatic tag creation
- **Caching**: Redis-based caching for improved performance

#### Data Enrichment
- **Location Services**: Automatic location resolution and creation
- **Weather Services**: Historical weather data integration
- **Geo Services**: Geographic data processing

### API Endpoints

#### Authentication (`/api/auth`)
- `POST /refresh` - Refresh JWT access token
- `POST /logout` - User logout and token cleanup

#### Travel Memos (`/api/memos`)
- `GET /greeting` - Health check endpoint
- `POST /` - Create new travel memo
- `GET /{email}` - Get all memos for user by email
- `PUT /{id}` - Update existing memo
- `DELETE /{id}` - Delete memo

### Database Schema

#### Core Entities
- **TravelMemoEntity**: Main memo entity with title, content, image, rating, mood, and timestamps
- **UserEntity**: User information from OAuth2
- **LocationEntity**: Geographic location data
- **TagEntity**: Categorization tags

#### Relationships
- Many-to-many relationship between memos and locations
- Many-to-many relationship between memos and tags
- Many-to-one relationship between memos and users

### Configuration Requirements

Create a `.env` file in the `/journal` directory with:

```properties
# Database
DB_URL=your_postgresql_url
DB_USER=your_db_username
DB_PASS=your_db_password

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# JWT
JWT_SECRET=your_jwt_secret_key

# Redis Cache
SPRING_DATA_REDIS_HOST=your_redis_host
SPRING_DATA_REDIS_PORT=your_redis_port
SPRING_DATA_REDIS_USERNAME=your_redis_username
SPRING_DATA_REDIS_PASSWORD=your_redis_password

# Mapbox (for location services)
MAPBOX_TOKEN=your_mapbox_api_token
```

## Frontend (Next.js)

### Technology Stack

- **Framework**: Next.js 15.5.2
- **React**: 19.1.0
- **Language**: TypeScript
- **Styling**: Tailwind CSS 4
- **State Management**: TanStack React Query
- **Maps**: Mapbox GL JS
- **UI Components**: Radix UI
- **Forms**: React Hook Form with Zod validation

### Key Features

- **Modern React**: Latest React 19 with Next.js 15
- **Type Safety**: Full TypeScript implementation
- **Responsive Design**: Tailwind CSS for responsive layouts
- **Form Validation**: React Hook Form with Zod schemas
- **Interactive Maps**: Mapbox integration for location visualization
- **Theme Support**: Dark/light theme switching
- **Component Library**: Radix UI components for accessibility

## Getting Started

### Prerequisites

- Java 21
- Node.js 18+
- PostgreSQL database
- Redis instance
- Google OAuth2 credentials
- Mapbox API token

### Backend Setup

1. Navigate to the journal directory:
   ```bash
   cd journal
   ```

2. Create `.env` file with required configuration (see above)

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend API will be available at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the development server:
   ```bash
   npm run dev
   ```

The frontend will be available at `http://localhost:3000`

### Available Scripts

#### Backend
- `./mvnw spring-boot:run` - Start development server
- `./mvnw test` - Run tests
- `./mvnw package` - Build JAR package

#### Frontend
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm run lint` - Run ESLint

## Development Features

- **Hot Reload**: Both backend and frontend support hot reload
- **API Documentation**: Swagger/OpenAPI documentation available
- **Testing**: Comprehensive test suite for controllers and services
- **Caching**: Redis-based caching for performance optimization
- **Security**: OAuth2 + JWT authentication with CORS configuration

## Project Structure

```
appteam_/
├── journal/                 # Spring Boot backend
│   ├── src/main/java/com/travel/journal/
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── entity/          # JPA entities
│   │   ├── repo/            # Data repositories
│   │   ├── security/        # Security configuration
│   │   ├── config/          # Application configuration
│   │   └── util/            # Utility classes
│   └── pom.xml             # Maven configuration
└── frontend/               # Next.js frontend
    ├── src/
    ├── public/
    └── package.json        # NPM configuration
```