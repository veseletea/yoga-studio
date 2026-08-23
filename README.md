![CI](https://github.com/veseletea/yoga-studio/actions/workflows/ci.yml/badge.svg)
![Release](https://img.shields.io/github/v/release/veseletea/yoga-studio)

# Yoga Studio Management

Full-stack application for managing a yoga studio: instructors, students, classes, and bookings.

**Live Demo:** [yogastudio.up.railway.app](https://yogastudio.up.railway.app) · 
**API Docs:** [Swagger UI](https://yogastudio.up.railway.app/swagger-ui/index.html)

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 21, Spring Boot 3.4, Spring Data JPA |
| **Frontend** | React 19, Vite, Plain CSS |
| **Database** | PostgreSQL (Docker Compose local, Railway in production) |
| **Infrastructure** | Docker & Docker Compose, Railway |
| **AI** | Spring AI, pgvector, OpenAI (RAG) |
| **Testing** | JUnit 5, Mockito, Testcontainers |
| **CI** | GitHub Actions |
|  **AI** | Spring AI, pgvector, OpenAI (RAG + tool calling) |
|  **Security** | Spring Security (stateless auth, BCrypt) |

## Features

- Full CRUD for instructors, students, and classes
- Booking system with capacity validation
- Automatic waitlist when a class is full
- Input validation with localized error messages
- Centralized error handling using RFC 7807 Problem Detail
- Persistent data storage with PostgreSQL (survives restarts)
- Interactive API documentation with Swagger/OpenAPI (springdoc)

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

First, start the PostgreSQL database with Docker Compose:

```bash
docker compose up -d db
```

Then run the application:

```bash
mvn spring-boot:run
```
## Running locally

## Environment variables

| Variable | Required | Description |
|---|---|---|
| `OPENAI_API_KEY` | yes | OpenAI key used for chat completions (`gpt-4o-mini`) and embeddings (`text-embedding-3-small`). |
| `ADMIN_PASSWORD` | yes | Password for the seeded admin account. Set it before the first startup — the seeder only creates the account if it doesn't already exist. |
| `SPRING_DATASOURCE_URL` | no locally | Defaults to `jdbc:postgresql://localhost:5432/yogastudio`, matching the `db` service in `docker-compose.yml`. Set explicitly in production. |
| `SPRING_DATASOURCE_USERNAME` | no locally | Same as above. |
| `SPRING_DATASOURCE_PASSWORD` | no locally | Same as above. |
Never commit these values. Locally they go in your shell environment; on Railway they are set as service variables.

```bash
docker compose up -d db
ADMIN_PASSWORD=your_password mvn spring-boot:run
```

The admin account is seeded on first startup using `app.admin.email` and `ADMIN_PASSWORD`.
Set `ADMIN_PASSWORD` before the first run — the seeder only creates the account if it doesn't already exist.

Backend runs at `http://localhost:8080`

### Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:3000`

### Run with Docker available at http://localhost:8080

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

## Database

The application uses **PostgreSQL** in all environments.

**Local development** runs Postgres via Docker Compose (see `docker-compose.yml`). Connection settings default to a local instance and can be overridden with environment variables:

| Variable | Default (local) |
|----------|-----------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/yogastudio` |
| `SPRING_DATASOURCE_USERNAME` | `yoga` |
| `SPRING_DATASOURCE_PASSWORD` | `yoga_local_pass` |

**Production** runs on Railway with a managed PostgreSQL instance, wired through the same environment variables.

## Roadmap

- [ ] User authentication & authorization (Spring Security + JWT)
- [x] Migrate from H2 to PostgreSQL (local + production)
- [x] OpenAPI / Swagger documentation
- [ ] Unit and integration tests (JUnit 5 + Testcontainers)
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Email notifications for bookings
- [ ] Admin dashboard with analytics

## License

This project is built for educational and portfolio purposes.

## Author

**Iuliana Paun** — built to demonstrate Java / Spring Boot and full-stack development skills.
