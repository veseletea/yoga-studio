# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Full-stack yoga studio management app: a Spring Boot (Java 21) REST backend and a React 19 (Vite) SPA frontend. Entities: Instructor, Student, YogaClass, Booking. Data is in-memory H2 — recreated on every restart (`ddl-auto=create-drop`) and seeded from `src/main/resources/data.sql`, so any schema/data change you make at runtime is lost on restart and seed edits go in `data.sql`.

## Commands

### Backend (from repo root)
- `mvn spring-boot:run` — run backend at `http://localhost:8080`
- `mvn test` — run all tests
- `mvn test -Dtest=YogaStudioApplicationTests#contextLoads` — run a single test
- `mvn clean package` — full build; this **also builds the frontend** (see Build pipeline) and bundles it into the jar

### Frontend (from `frontend/`)
- `npm install` then `npm run dev` — dev server at `http://localhost:3000`, proxies `/api` to `:8080` (see `vite.config.js`)
- `npm run lint` — ESLint (flat config in `eslint.config.js`)
- `npm run build` — production build into `frontend/dist/`

### Docker
- `docker compose up --build` — backend on `:8080`, frontend (nginx) on `:3000`

## Architecture

### Two run modes — know which one you're in
1. **Dev (two servers):** Vite on `:3000` proxies `/api/*` to Spring on `:8080`. CORS is also configured in `WebConfig.java` for `localhost:3000`.
2. **Packaged / prod (one server):** `mvn package` builds the React app and copies `frontend/dist` into the jar's `static/`. Spring serves the SPA and the API from `:8080`. `SpaController` forwards client-side routes (`/instructors`, `/students`, `/classes`, `/bookings`) to `index.html` — **if you add a new top-level frontend route, add it to `SpaController.forward()`** or a deep link / refresh on that path 404s.

### Backend layering (`src/main/java/com/yogastudio/`)
Strict `controller → service → repository → entity` flow:
- **controller/** — REST endpoints under `/api/*`, validate input (`@Valid`), delegate to services, return DTOs.
- **service/** — business logic, `@Transactional`. Throws domain exceptions (`ResourceNotFoundException`, `DuplicateResourceException`) rather than returning error codes.
- **repository/** — Spring Data JPA interfaces.
- **dto/** — Java `record`s for request/response. Responses have a static `from(entity)` factory (e.g. `BookingResponse.from(...)`); entities are never exposed directly.
- **exception/** — `GlobalExceptionHandler` (`@RestControllerAdvice`) maps every exception to an RFC 7807 `ProblemDetail`. The frontend's `api.js` reads `error.detail` from this. Add new error mappings here, not in controllers.

### Booking domain rules (`BookingService`)
- Duplicate enrollment (same student + class) → `DuplicateResourceException` (409).
- Capacity: a new booking is `CONFIRMED` if confirmed bookings < `maxCapacity`, otherwise auto-`WAITLISTED`. Capacity is computed by counting `CONFIRMED` bookings live, not a stored counter.
- Cancel sets status `CANCELLED` (soft) — bookings are not deleted.

### Frontend (`frontend/src/`)
- `api.js` — single fetch wrapper + per-entity API objects (`studentApi`, `instructorApi`, `yogaClassApi`, `bookingApi`). All calls go through `request()`, which unwraps `ProblemDetail` errors and treats 204 as null. **Add new endpoints here**, keep components free of raw `fetch`.
- `pages/` — one page per entity plus Home/Schedule; `components/Navbar.jsx`; React Router (`react-router-dom` v7) wired in `App.jsx`. Plain CSS in `index.css`, no UI framework.
- Note `studentApi.findByEmail` hits `/api/students/search?email=` (not documented in README) — check the controller for the full endpoint set rather than trusting the README's table.

## Build pipeline gotcha
`pom.xml` uses `frontend-maven-plugin` to download Node `v22.14.0`, run `npm ci`, and `npm run build` during the Maven build, then `maven-resources-plugin` copies `frontend/dist` → `target/classes/static`. So `mvn package` needs no local Node, but a frontend build/lint failure fails the Maven build.

## Notes
- H2 console at `/h2-console` (JDBC `jdbc:h2:mem:yogastudio`, user `sa`, no password).
- Swagger UI at `/swagger-ui/index.html` (springdoc).
- Deployment: Railway via root `Dockerfile` / `Procfile`. If the deployed frontend origin changes, update the allowed origins in `WebConfig.java`.
- Only a smoke `contextLoads` test exists — there is no real test suite yet.
