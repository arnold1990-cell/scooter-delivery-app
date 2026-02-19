# Scooter Delivery App

Production-style full-stack scooter delivery platform with JWT auth, role-based access, and Docker Compose one-command startup.

## Stack
- Backend: Java 21, Spring Boot 3.2, Spring Security 6, Spring Data JPA, Flyway, PostgreSQL 15, JWT (jjwt 0.12.6), SpringDoc
- Frontend: React 18, Vite 5, TypeScript 5, Axios, React Context, Tailwind CSS 3
- Infra: Docker Compose v2

## Run with Docker
```bash
docker compose up --build
```

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Local development
### Database
```bash
docker run -d --name scooter-pg \
  -e POSTGRES_DB=scooterdb \
  -e POSTGRES_USER=scooter \
  -e POSTGRES_PASSWORD=scooter \
  -p 5432:5432 postgres:15-alpine
```

### Backend
```bash
cd backend
SCOOTER_DB_URL=jdbc:postgresql://localhost:5432/scooterdb \
SCOOTER_DB_USERNAME=scooter \
SCOOTER_DB_PASSWORD=scooter \
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```
