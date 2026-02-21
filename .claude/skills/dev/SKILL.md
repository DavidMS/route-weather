---
name: dev
description: Start the full development environment and verify everything is running.
---

# Dev Command

Start the full development environment and verify everything is running.

## Pre-flight Checks
1. Check that `.env` exists (copy from `.env.example` if missing and warn the user).
2. Check that Java 21+ is available: `java -version`
3. Check that Node 18+ is available: `node --version`
4. Check that Maven is available: `mvn --version`

## Start Instructions

Instruct the user to open **three terminal tabs** and run:

**Tab 1 — Backend:**
```bash
cd backend && mvn spring-boot:run
```
Wait for: `Started RouteWeatherApplication in X seconds`

**Tab 2 — Frontend:**
```bash
cd frontend && npm run dev
```
Wait for: `Local: http://localhost:5173`

**Tab 3 — (optional) Logs / other tools**

## Verification Checklist
After both services start:
- [ ] Backend health: `curl http://localhost:8080/actuator/health` → `{"status":"UP"}`
- [ ] Frontend: Open `http://localhost:5173` in browser
- [ ] API test: `curl -X POST http://localhost:8080/api/routes/weather -H 'Content-Type: application/json' -d '{"origin":"Madrid","destination":"Barcelona","travelDate":"2025-06-15"}'`

## Docker Alternative
If the user prefers Docker:
```bash
docker compose up --build
```
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:80`

## Troubleshooting
- Port 8080 in use: check `lsof -i :8080` and kill the process.
- Port 5173 in use: Vite will auto-increment to 5174.
- Missing `ORS_API_KEY`: routing will return stub waypoints only.
