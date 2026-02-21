# Route Weather — Agent Instructions

## Project Overview
A web application where users enter a travel route (origin city, destination city, date)
and receive weather forecasts along the way. The app calls a routing API to get waypoints
and a weather API to get forecasts at each point.

## Technology Stack
| Layer       | Technology                                      |
|-------------|------------------------------------------------|
| Backend     | Spring Boot 3.3 · Java 21 · Maven              |
| Frontend    | React 18 · TypeScript · Vite                   |
| Weather API | Open-Meteo (free, no key needed)               |
| Routing API | OpenRouteService (free tier, key via env var)  |
| Dev infra   | Docker Compose                                 |

---

## Architecture: Hexagonal (Ports & Adapters)

The **backend** enforces strict hexagonal architecture. Dependencies always point inward:

```
Infrastructure  →  Application  →  Domain
(adapters)         (use cases)     (pure business logic)
```

### Package Layout
```
backend/src/main/java/com/routeweather/
├── domain/
│   ├── model/          ← Entities and value objects (no framework deps)
│   └── exception/      ← Domain-specific exceptions
├── application/
│   ├── port/
│   │   ├── in/         ← Inbound ports: use case interfaces (driving side)
│   │   └── out/        ← Outbound ports: external dependency interfaces (driven side)
│   └── service/        ← Use case implementations (plain Java, no annotations)
└── infrastructure/
    ├── adapter/
    │   ├── in/rest/    ← REST controllers + request/response DTOs + mappers
    │   └── out/
    │       ├── weather/ ← Weather API client (implements WeatherForecastPort)
    │       └── maps/    ← Routing API client (implements RouteCalculatorPort)
    └── config/         ← Spring @Configuration: wires ports to adapters
```

### Architecture Rules (enforce these in every change)
1. `domain/` has **zero** imports from Spring, Jakarta, or any framework.
2. `application/` imports only from `domain/`. No Spring annotations on services.
3. `infrastructure/` is the only layer that knows about Spring, HTTP clients, etc.
4. DTOs **never** enter the domain. Map them to domain objects at the REST boundary.
5. Beans are wired **exclusively** in `BeanConfiguration.java`.

---

## Development Commands

### Backend
```bash
cd backend && mvn spring-boot:run          # Start on port 8080
cd backend && mvn test                     # Run all tests
cd backend && mvn clean package -DskipTests  # Build JAR
cd backend && mvn clean verify             # Build + tests
```

### Frontend
```bash
cd frontend && npm install                 # Install deps (first time)
cd frontend && npm run dev                 # Dev server on port 5173
cd frontend && npm run build               # Production build → dist/
cd frontend && npm test                    # Run Vitest tests
```

### Full Stack (Docker)
```bash
docker compose up --build                  # Build images and start all services
docker compose up                          # Start (images already built)
docker compose down                        # Stop
```

---

## Key Domain Concepts
| Concept             | Description                                                  |
|---------------------|--------------------------------------------------------------|
| `Route`             | A trip: origin + destination (name + coords) + travel date   |
| `Coordinates`       | A lat/lng value object; validates ranges on construction     |
| `WeatherPoint`      | Forecast at one waypoint: temp, precipitation, wind, condition|
| `WeatherCondition`  | Enum: CLEAR, CLOUDY, RAINY, SNOWY, STORMY, FOGGY            |
| `RouteWeatherReport`| Aggregate: the Route + its list of WeatherPoints            |

---

## External API Notes

### Open-Meteo (weather)
- Base URL: `https://api.open-meteo.com/v1/forecast`
- No API key required. Params: `latitude`, `longitude`, `daily`, `start_date`, `end_date`.
- Adapter: `infrastructure/adapter/out/weather/OpenMeteoWeatherAdapter.java`

### OpenRouteService (routing)
- Base URL: `https://api.openrouteservice.org/v2/directions/driving-car`
- Requires `ORS_API_KEY` environment variable (set in `.env` or docker-compose).
- Adapter: `infrastructure/adapter/out/maps/OpenRouteServiceAdapter.java`
- Free geocoding endpoint to convert city names → coordinates also available.

---

## Testing Conventions
- **Domain & service tests**: Pure unit tests, no Spring context, mock the ports.
- **Adapter tests**: Integration tests using `@SpringBootTest` or `WireMock`.
- **Controller tests**: `@WebMvcTest` with mocked use case.
- Naming: `{ClassName}Test.java` for unit, `{ClassName}IT.java` for integration.
- All new domain logic **must** have unit test coverage.

---

## Git Workflow (MANDATORY)

> **Never commit directly to `main`.** Every piece of work — feature, fix, or
> chore — must go through a branch and a pull request.

### Starting any new task
```bash
git checkout main && git pull          # always branch from an up-to-date main
git checkout -b <type>/<short-name>    # e.g. feat/alert-thresholds, fix/map-bounds
```

Branch naming convention:
| Prefix   | When to use                          |
|----------|--------------------------------------|
| `feat/`  | new functionality                    |
| `fix/`   | bug fix                              |
| `chore/` | tooling, deps, config, docs          |
| `test/`  | adding or fixing tests only          |

### During work
Commit early and often to the feature branch. Commit messages follow
[Conventional Commits](https://www.conventionalcommits.org/):
```
feat(map): add polyline following road geometry
fix(weather): handle Open-Meteo out-of-range date error
```

### Opening a PR
When the feature is complete and tests pass:
```bash
git push -u origin <branch-name>
gh pr create --base main --title "<title>" --body "<summary>"
```
- PR title mirrors the main commit message.
- Body: what changed, why, how to test.
- Do **not** merge the PR yourself — leave that to the user.

### What NEVER to do
- `git push origin main` (direct push to main)
- Committing secrets or API keys to any tracked file
- Merging without a PR

---

## Adding a New Feature (step-by-step)
Use the `/add-feature` command for a guided walkthrough. Manual steps:

1. Update/create domain model in `domain/model/`.
2. Define the use case interface in `application/port/in/`.
3. Define new external dependencies in `application/port/out/`.
4. Implement the service in `application/service/`.
5. Add/update the REST endpoint in `infrastructure/adapter/in/rest/`.
6. Implement external adapters in `infrastructure/adapter/out/`.
7. Wire new beans in `infrastructure/config/BeanConfiguration.java`.
8. Write unit tests for domain + service; write adapter tests.

---

## Custom Commands (Skills)
Available via `/command-name` in Claude Code:

| Command           | Purpose                                              |
|-------------------|------------------------------------------------------|
| `/build`          | Build backend and frontend, report errors            |
| `/test`           | Run all tests (backend + frontend), show summary     |
| `/dev`            | Start full dev environment (instructions + checklist)|
| `/add-feature`    | Guided scaffold for a new feature (hexagonal steps)  |
| `/check-arch`     | Audit codebase for hexagonal architecture violations |

---

## Environment Variables
```
ORS_API_KEY=<your OpenRouteService API key>
BACKEND_PORT=8080          # default
FRONTEND_PORT=5173         # default (dev) / 80 (prod docker)
```

Copy `.env.example` to `.env` and fill in values before running.
