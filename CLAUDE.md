# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Full-stack yoga studio management app: a Spring Boot 3.4 (Java 21) REST backend and a React 19 (Vite) SPA frontend. Entities: Instructor, Student, YogaClass, Booking. Also includes an AI assistant (Spring AI + OpenAI, RAG over pgvector).

Data lives in **PostgreSQL** (image `pgvector/pgvector:pg16`) with `ddl-auto=update`, so data persists across restarts. Connection comes from `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` env vars, defaulting to `localhost:5432/yogastudio` (user `yoga`). `src/main/resources/data.sql` exists but is **not** executed against Postgres (`spring.sql.init.mode` is commented out).

Required env vars: `OPENAI_API_KEY` (startup needs it); optional `ADMIN_PASSWORD` (see Security).

## Commands

### Backend (from repo root)
- `docker compose up -d db` — start only Postgres, then run the backend from the IDE / `mvn spring-boot:run` at `http://localhost:8080`
- `mvn test` — unit tests (surefire)
- `mvn verify` — also runs `*IT` integration tests (failsafe); these and `AbstractIntegrationTest` subclasses use **Testcontainers**, so Docker must be running
- `mvn test -Dtest=BookingServiceTest` — run a single test class
- `mvn clean package` — full build; this **also builds the frontend** (see Build pipeline) and bundles it into the jar

### Frontend (from `frontend/`)
- `npm install` then `npm run dev` — dev server at `http://localhost:3000`, proxies `/api` to `:8080` (see `vite.config.js`)
- `npm run lint` — ESLint (flat config in `eslint.config.js`)
- `npm run build` — production build into `frontend/dist/`

### Docker
- `docker compose up --build` — containers `yoga-db` (:5432), `yoga-backend` (:8080), `yoga-frontend` (nginx, :3000)
- **Port clash:** if `yoga-backend` is running, starting the backend from the IDE fails with "Port 8080 was already in use". Run `docker stop yoga-backend` first (keep `yoga-db` up). The `yoga-backend` image only contains new code after `--build`.

## Architecture

### Two run modes — know which one you're in
1. **Dev (two servers):** Vite on `:3000` proxies `/api/*` to Spring on `:8080`. CORS is also configured in `WebConfig.java`.
2. **Packaged / prod (one server):** `mvn package` builds the React app and copies `frontend/dist` into the jar's `static/`. Spring serves the SPA and the API from `:8080`. `SpaController` forwards client-side routes to `index.html` — **if you add a new top-level frontend route (in `App.jsx`), add it to both `SpaController.forward()` and the SPA-routes matcher in `SecurityConfig`**, or a deep link / refresh on that path returns 404 / 401.

### Backend layering (`src/main/java/com/yogastudio/`)
Strict `controller → service → repository → entity` flow:
- **controller/** — REST endpoints under `/api/*`, validate input (`@Valid`), delegate to services, return DTOs. `HomeController` serves `GET /api` (welcome info from `app.info.*`); `HealthController` serves a custom `GET /health`.
- **service/** — business logic, `@Transactional`. Throws domain exceptions (`ResourceNotFoundException`, `DuplicateResourceException`) rather than returning error codes.
- **repository/** — Spring Data JPA interfaces.
- **dto/** — Java `record`s for request/response. Responses have a static `from(entity)` factory (e.g. `BookingResponse.from(...)`); entities are never exposed directly.
- **exception/** — `GlobalExceptionHandler` (`@RestControllerAdvice`) maps every exception to an RFC 7807 `ProblemDetail`. The frontend's `api.js` reads `error.detail` from this. Add new error mappings here, not in controllers.
- **config/** — `SecurityConfig`, `StudentUserDetailsService`, `AdminSeeder`, `OpenApiConfig`.

### Security (`config/SecurityConfig.java`)
- Spring Security, stateless, CSRF disabled, **HTTP Basic** auth. Users are `Student` rows (login = email, BCrypt password); role from `Student.Role` (`STUDENT` default, `ADMIN`) → `ROLE_*` authority.
- Public: static SPA assets and SPA routes (GET), Swagger, `/api/assistant/**`, all `GET /api/**`, `GET /actuator/health[/**]` and `/actuator/info`. Everything else (all writes) requires authentication.
- `AdminSeeder` creates an admin (`app.admin.email`, default `admin@yogastudio.local`) on startup only if `ADMIN_PASSWORD` is set and the account doesn't exist.
- When adding a new public endpoint, add a matcher here — otherwise it falls into `.anyRequest().authenticated()` and returns 401.

### Actuator
Only `health` and `info` are exposed over HTTP (`management.endpoints.web.exposure.include=health,info`); health details are hidden (`show-details=never`). `/actuator/info` is populated from `info.app.*`, which reuses the `app.info.*` properties. Exposing more endpoints requires changing both the property and `SecurityConfig` (other `/actuator/**` paths return 401).

### AI assistant (Spring AI)
- `GET /api/assistant?question=...` (`YogaAssistantController`) — `ChatClient` with OpenAI `gpt-4o-mini`, a `QuestionAnswerAdvisor` over the pgvector store, and `BookingTools` as tools.
- `BookingTools` act on behalf of the **currently authenticated user** (`SecurityContextHolder`); they never book for others.
- `DataIngestionService` ingests `src/main/resources/documents/*.md` (EN + `.ro.md` variants) into the `vector_store` table at startup. It fingerprints the documents (SHA-256, stored in `ingestion_state`) and only re-ingests when they change. Disable with `app.rag.ingestion.enabled=false` (done in the `test` profile).

### Booking domain rules (`BookingService`)
- Duplicate enrollment (same student + class) → `DuplicateResourceException` (409).
- Capacity: a new booking is `CONFIRMED` if confirmed bookings < `maxCapacity`, otherwise auto-`WAITLISTED`. Capacity is computed by counting `CONFIRMED` bookings live, not a stored counter.
- Cancel sets status `CANCELLED` (soft) — bookings are not deleted.

### Frontend (`frontend/src/`)
- `api.js` — single fetch wrapper + per-entity API objects (`studentApi`, `instructorApi`, `yogaClassApi`, `bookingApi`). All calls go through `request()`, which unwraps `ProblemDetail` errors and treats 204 as null. **Add new endpoints here**, keep components free of raw `fetch`.
- `pages/` — one page per entity plus Home/Schedule; `components/Navbar.jsx`; React Router (`react-router-dom` v7) wired in `App.jsx`. Plain CSS in `index.css`, no UI framework.
- Note `studentApi.findByEmail` hits `/api/students/search?email=` (not documented in README) — check the controller for the full endpoint set rather than trusting the README's table.

## Tests (`src/test/java/com/yogastudio/`)
- `AbstractIntegrationTest` — `@SpringBootTest` + `test` profile + Testcontainers Postgres (`pgvector/pgvector:pg16`, via `@ServiceConnection`). Extend it for integration tests.
- `test` profile (`src/test/resources/application-test.properties`): RAG ingestion off, dummy OpenAI key, `ddl-auto=create-drop`.
- Existing: `YogaStudioApplicationTests` (context loads), `ClassRepositoryIT`, `BookingServiceTest`, `controller/HomeControllerTest`.

## Build pipeline gotcha
`pom.xml` uses `frontend-maven-plugin` to download Node `v22.14.0`, run `npm ci`, and `npm run build` during the Maven build, then `maven-resources-plugin` copies `frontend/dist` → `target/classes/static`. So `mvn package` needs no local Node, but a frontend build/lint failure fails the Maven build.

## Notes
- Swagger UI at `/swagger-ui/index.html` (springdoc).
- Deployment: Railway via root `Dockerfile` (multi-stage Maven build → JRE 21) / `Procfile`. `/actuator/health` is suitable as Railway's healthcheck path. If the deployed frontend origin changes, update the allowed origins in `WebConfig.java`.
- `docker-compose.yml`'s backend healthcheck curls `/actuator/health`.
