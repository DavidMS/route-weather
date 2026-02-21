---
name: build
description: Build both the backend and frontend. Report all errors clearly.
---

# Build Command

Build both the backend and frontend. Report all errors clearly.

## Steps

1. **Backend build**
   ```bash
   cd backend && mvn clean package -DskipTests
   ```
   - If it fails, show the Maven error output and identify the root cause.
   - Do NOT attempt to fix compilation errors silently — report them to the user first.

2. **Frontend build**
   ```bash
   cd frontend && npm run build
   ```
   - If it fails, show the Vite/TypeScript error output.

3. **Summary**
   Report a clear build status for each component:
   - ✓ Backend: SUCCESS / ✗ Backend: FAILED (reason)
   - ✓ Frontend: SUCCESS / ✗ Frontend: FAILED (reason)

If both succeed, confirm the JAR location (`backend/target/*.jar`) and the frontend output (`frontend/dist/`).
