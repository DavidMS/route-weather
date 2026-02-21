# Add Feature Command

Scaffold a new feature following hexagonal architecture. Ask the user for the feature name and description before proceeding.

## Step 0 — Create a feature branch (ALWAYS first)
```bash
git checkout main && git pull
git checkout -b feat/<kebab-case-feature-name>
```
Do not write any code until this branch exists.

## Step 1 — Gather Requirements
Ask:
1. What is the feature name? (e.g., "AlertThresholds", "SavedRoutes")
2. What domain concept does it introduce or extend?
3. Does it need a new external API call? If so, which API?
4. Does it need a new REST endpoint? What HTTP method and path?

## Step 2 — Domain Layer
Create or update files in `backend/src/main/java/com/routeweather/domain/`:
- New entity or value object (prefer Java `record` for value objects)
- New domain exception if needed
- Domain model must have zero framework dependencies

## Step 3 — Application Layer (Ports)
- **Inbound port** (`application/port/in/`): Define the use case interface
  ```java
  public interface <FeatureName>UseCase {
      <ReturnType> <methodName>(<Params>);
  }
  ```
- **Outbound port** (`application/port/out/`): Define any new external dependency interface
  ```java
  public interface <FeatureName>Port {
      <ReturnType> <methodName>(<Params>);
  }
  ```

## Step 4 — Application Service
Create `application/service/<FeatureName>Service.java`:
- Implements the inbound port interface
- Receives outbound ports via constructor injection
- No Spring annotations here (wired in config)

## Step 5 — Infrastructure: REST Adapter
In `infrastructure/adapter/in/rest/`:
- Add endpoint to existing controller or create a new `@RestController`
- Create request/response DTOs as Java `record`s
- Create a `<Feature>Mapper` to convert DTOs ↔ domain objects

## Step 6 — Infrastructure: Outbound Adapter
In `infrastructure/adapter/out/<api-name>/`:
- Implement the outbound port interface
- Use `RestTemplate` or `WebClient` for HTTP calls
- Annotate with `@Component`

## Step 7 — Bean Wiring
Update `infrastructure/config/BeanConfiguration.java`:
- Add `@Bean` method that instantiates the service with its port dependencies

## Step 8 — Tests
- Unit test for the service (`application/service/<FeatureName>ServiceTest.java`)
  - Use mocked ports (Mockito)
  - Cover happy path and error cases
- Controller test (`@WebMvcTest`)

## Step 9 — Checklist Before Finishing
- [ ] No Spring annotations in `domain/` or `application/service/`
- [ ] No DTOs leaking into `domain/` or `application/`
- [ ] New beans wired in `BeanConfiguration`
- [ ] At least one unit test for the service
- [ ] Endpoint documented in README or OpenAPI annotations

## Step 10 — Open a Pull Request
```bash
git push -u origin feat/<feature-name>
gh pr create --base main \
  --title "feat: <short description>" \
  --body "## What\n<what changed>\n\n## Why\n<motivation>\n\n## How to test\n<steps>"
```
- Never merge the PR yourself — leave that to the user.
- Never push directly to `main`.
