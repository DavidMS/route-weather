# Route Weather

A web application that shows weather forecasts along a driving route.
Enter an origin, a destination, and a travel date — the app shows you what the weather will be like at each waypoint on your journey.

## Quick Start

```bash
cp .env.example .env          # add ORS_API_KEY if you have one
cd backend && mvn spring-boot:run   # backend on :8080
cd frontend && npm install && npm run dev  # frontend on :5173
```

Or with Docker:
```bash
docker compose up --build
```

## Architecture

Clean hexagonal (ports & adapters) architecture in the backend.
See [CLAUDE.md](./CLAUDE.md) for full architecture docs and agent instructions.

## Tech Stack
- **Backend**: Spring Boot 3.3, Java 21, Maven
- **Frontend**: React 18, TypeScript, Vite
- **Weather**: [Open-Meteo](https://open-meteo.com/) (free, no key)
- **Routing**: [OpenRouteService](https://openrouteservice.org/) (free tier)
