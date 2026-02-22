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
1) Create local environment file (already gitignored):
```bash
cat > .env <<'EOF'
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/scooterdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=CHANGE_ME_STRONG_PASSWORD
CORS_ALLOWED_ORIGINS=https://scooter-delivery.duckdns.org,http://localhost:5173
JWT_SECRET=CHANGE_ME_TO_A_LONG_RANDOM_SECRET_32+_CHARS
JWT_EXPIRATION_MINUTES=1440
ADMIN_EMAIL=
ADMIN_PASSWORD_BCRYPT=$2a$12$7xxy7f8aPR2Eq7vWvJ4P3OF7fAQUm6mLCfWfslWQj8fmgJ8q2HEga
APP_FLYWAY_AUTO_REPAIR_ON_VALIDATION_ERROR=false
EOF
```

2) Export variables and run Spring Boot (Linux/macOS):
```bash
set -a && source .env && set +a
cd backend
mvn spring-boot:run
```

3) Windows PowerShell:
```powershell
Get-Content .env | ForEach-Object { if($_ -match "^(.*?)=(.*)$"){ [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2]) } }
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

- Default local API URL: `http://localhost:8080/api` (set via `VITE_API_BASE_URL`).
- For VPS/local network access, update `frontend/.env` with your backend host, for example `VITE_API_BASE_URL=http://144.xx.xx.xx:8080/api`, then restart Vite.

## Flyway migration safety
- Never edit a Flyway migration that has already been applied in any environment.
- Always add a new versioned migration file (for example `V5__...sql`) for follow-up schema/data changes.

## Troubleshooting: "binary files not supported"
If your Git provider shows **"binary files not supported"** for files that should be plain text, add or update `.gitattributes` so those extensions are explicitly marked as text. This repository now includes a baseline `.gitattributes` to make diffs work for Java, TypeScript, SQL, YAML, JSON, and other source/config files.

After adding `.gitattributes`, re-stage the affected files so Git recalculates attributes:
```bash
git add --renormalize .
```

## VPS (systemd) environment setup
Use an environment file and let systemd load it (recommended for production):

```ini
# /etc/systemd/system/scooter-backend.service
[Unit]
Description=Scooter Delivery Backend
After=network.target

[Service]
Type=simple
User=scooter
WorkingDirectory=/opt/scooter-backend/backend
EnvironmentFile=/opt/scooter-backend/.env
ExecStart=/usr/bin/java -jar /opt/scooter-backend/backend/target/backend-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Then reload and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable scooter-backend
sudo systemctl restart scooter-backend
sudo systemctl status scooter-backend
```
