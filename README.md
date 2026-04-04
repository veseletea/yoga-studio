# Yoga Studio Management

Aplicatie full-stack pentru managementul unui studio de yoga: instructori, studenti, clase si rezervari.

### Tech Stack (Backend - în dezvoltare)

- **Backend**: Spring Boot 3.4, Java 17, Spring Data JPA, H2 (în memorie)
- **Frontend**: React 19 + Vite (în plan / în dezvoltare)
- **Tools**: Maven, Git, Docker (în curs de configurare)
- **Scop proiect**: Demonstrare competențe Java/Spring Boot 

## Structura Proiectului

```
yoga-studio/
├── src/                        # Spring Boot backend
│   └── main/java/com/yogastudio/
│       ├── entity/             # Student, Instructor, YogaClass, Booking
│       ├── dto/                # Java records (request/response)
│       ├── repository/         # Spring Data JPA
│       ├── service/            # Business logic
│       ├── controller/         # REST API
│       └── exception/          # Global error handling
├── frontend/                   # React frontend
│   └── src/
│       ├── pages/              # Home, Instructors, Students, Classes, Bookings
│       ├── components/         # Navbar
│       └── api.js              # API client
├── Dockerfile                  # Backend container
├── frontend/Dockerfile         # Frontend container (nginx)
└── docker-compose.yml          # Orchestrare
```

## Rulare Locala

### Cerinte
- Java 21
- Maven 3.9+
- Node.js 20+

### Backend
```bash
mvn spring-boot:run
```
Backend disponibil la `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend disponibil la `http://localhost:3000`

## Rulare cu Docker

```bash
docker compose up --build
```
Aplicatia va fi disponibila la `[docker compose up --build](https://yoga-studio-production-86fa.up.railway.app/api/classes)`

## API Endpoints

| Metoda | Endpoint | Descriere |
|--------|----------|-----------|
| GET | `/api/students` | Lista studenti |
| POST | `/api/students` | Adauga student |
| PUT | `/api/students/{id}` | Actualizeaza student |
| DELETE | `/api/students/{id}` | Sterge student |
| GET | `/api/instructors` | Lista instructori |
| POST | `/api/instructors` | Adauga instructor |
| PUT | `/api/instructors/{id}` | Actualizeaza instructor |
| DELETE | `/api/instructors/{id}` | Sterge instructor |
| GET | `/api/classes` | Lista clase |
| GET | `/api/classes/day/{DAY}` | Clase pe zi (ex: MONDAY) |
| POST | `/api/classes` | Adauga clasa |
| PUT | `/api/classes/{id}` | Actualizeaza clasa |
| DELETE | `/api/classes/{id}` | Sterge clasa |
| GET | `/api/bookings` | Lista rezervari |
| GET | `/api/bookings/student/{id}` | Rezervari per student |
| GET | `/api/bookings/class/{id}` | Rezervari per clasa |
| POST | `/api/bookings` | Creaza rezervare |
| PATCH | `/api/bookings/{id}/cancel` | Anuleaza rezervare |

## Exemple API

### Adauga instructor
```bash
curl -X POST http://localhost:8080/api/instructors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ana","lastName":"Popescu","email":"ana@yoga.ro","specialization":"Hatha Yoga"}'
```

### Creaza o clasa
```bash
curl -X POST http://localhost:8080/api/classes \
  -H "Content-Type: application/json" \
  -d '{"name":"Hatha Dimineata","dayOfWeek":"MONDAY","startTime":"10:00","durationMinutes":60,"maxCapacity":12,"instructorId":1}'
```

### Rezerva un student la o clasa
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"studentId":1,"yogaClassId":1}'
```

## H2 Console

Disponibila la `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:yogastudio`
- User: `sa`
- Parola: *(gol)*

## Functionalitati

- CRUD complet pentru instructori, studenti si clase
- Sistem de rezervari cu verificare capacitate
- Waitlist automat cand clasa e plina
- Validare input cu mesaje in romana
- Error handling centralizat (RFC 7807 Problem Detail)
- Date initiale pre-populate (4 instructori, 6 studenti, 9 clase, 13 rezervari)

### Progres curent (aprilie 2026)
- [x] Proiect Spring Boot creat cu Maven
- [x] Structură de pachete inițială
- [ ] Entități JPA (Student, Instructor, YogaClass, Booking)
- [ ] REST Controllers
- [ ] Bază de date + servicii
- [ ] Deploy pe Railway / Render
