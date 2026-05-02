# Yoga Studio Management

Full-stack application for managing a yoga studio: instructors, students, classes, and bookings.

**Live Demo:** [[yogastudio.up.railway.app](https://yogastudio.up.railway.app)]([https://yogastudio.up.railway.app](https://yogastudio.up.railway.app))

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 21, Spring Boot 3.4, Spring Data JPA |
| **Frontend** | React 19, Vite, Plain CSS |
| **Database** | H2 (in-memory, pre-populated) |
| **Infrastructure** | Docker & Docker Compose, Railway |

## Features

- Full CRUD for instructors, students, and classes
- Booking system with capacity validation
- Automatic waitlist when a class is full
- Input validation with localized error messages
- Centralized error handling using RFC 7807 Problem Detail
- Pre-populated seed data (4 instructors, 6 students, 9 classes, 13 bookings)

## Project Structure

```
yoga-studio/
├── src/                        # Spring Boot backend
│   └── main/java/com/yogastudio/
│       ├── entity/             # Student, Instructor, YogaClass, Booking
│       ├── dto/                # Java records (request/response)
│       ├── repository/         # Spring Data JPA repositories
│       ├── service/            # Business logic
│       ├── controller/         # REST API endpoints
│       └── exception/          # Global error handling
├── frontend/                   # React frontend
│   └── src/
│       ├── pages/              # Home, Instructors, Students, Classes, Bookings
│       ├── components/         # Navbar
│       └── api.js              # API client
├── Dockerfile                  # Backend container
├── frontend/Dockerfile         # Frontend container (nginx)
└── docker-compose.yml          # Service orchestration
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+

### Run the backend

```bash
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`

### Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:3000`

### Run with Docker

```bash
docker compose up --build
```

The full stack will be available at `http://localhost:8080`

## API Endpoints

### Students

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/students` | List all students |
| POST | `/api/students` | Create a student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |

### Instructors

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/instructors` | List all instructors |
| POST | `/api/instructors` | Create an instructor |
| PUT | `/api/instructors/{id}` | Update an instructor |
| DELETE | `/api/instructors/{id}` | Delete an instructor |

### Classes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/classes` | List all classes |
| GET | `/api/classes/day/{DAY}` | Filter classes by day (e.g. MONDAY) |
| POST | `/api/classes` | Create a class |
| PUT | `/api/classes/{id}` | Update a class |
| DELETE | `/api/classes/{id}` | Delete a class |

### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bookings` | List all bookings |
| GET | `/api/bookings/student/{id}` | Bookings for a specific student |
| GET | `/api/bookings/class/{id}` | Bookings for a specific class |
| POST | `/api/bookings` | Create a booking |
| PATCH | `/api/bookings/{id}/cancel` | Cancel a booking |

## API Examples

### Create an instructor

```bash
curl -X POST http://localhost:8080/api/instructors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ana",
    "lastName": "Popescu",
    "email": "ana@yoga.ro",
    "specialization": "Hatha Yoga"
  }'
```

### Create a class

```bash
curl -X POST http://localhost:8080/api/classes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Morning Hatha",
    "dayOfWeek": "MONDAY",
    "startTime": "10:00",
    "durationMinutes": 60,
    "maxCapacity": 12,
    "instructorId": 1
  }'
```

### Book a student into a class

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "yogaClassId": 1
  }'
```

## H2 Console (local development)

Available at `http://localhost:8080/h2-console`

| Setting | Value |
|---------|-------|
| JDBC URL | `jdbc:h2:mem:yogastudio` |
| Username | `sa` |
| Password | *(empty)* |

## Roadmap

- [ ] User authentication & authorization (Spring Security + JWT)
- [ ] Migrate from H2 to PostgreSQL in production
- [ ] OpenAPI / Swagger documentation
- [ ] Unit and integration tests (JUnit 5 + Testcontainers)
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Email notifications for bookings
- [ ] Admin dashboard with analytics

## License

This project is built for educational and portfolio purposes.

## Author

**Iuliana Paun** — built to demonstrate Java / Spring Boot and full-stack development skills.
