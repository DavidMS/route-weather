---
name: check-arch
description: Audit the backend codebase for hexagonal architecture violations. Read all Java files and report any issues.
---

# Check Architecture Command

Audit the backend codebase for hexagonal architecture violations. Read all Java files and report any issues.

## Rules to Check

### Rule 1 — Domain must have zero framework dependencies
Search `backend/src/main/java/com/routeweather/domain/` for:
- `import org.springframework.*`
- `import jakarta.*`
- `import javax.*`
- Any annotation from Spring (`@Component`, `@Service`, `@Repository`, etc.)

**Violation**: any of the above imports exist in the domain layer.

### Rule 2 — Application layer must not depend on infrastructure
Search `backend/src/main/java/com/routeweather/application/` for:
- `import com.routeweather.infrastructure.*`
- Spring annotations (`@Service`, `@Component`, `@Autowired`)

**Violation**: infrastructure imports or Spring annotations in application services.

### Rule 3 — Infrastructure must not be imported by domain or application
Verify that files in `domain/` and `application/` do not import anything from `infrastructure/`.

### Rule 4 — DTOs must not cross into domain or application
Search `domain/` and `application/service/` for references to classes from `adapter/in/rest/dto/`.

**Violation**: DTO class names appearing in domain or application service signatures.

### Rule 5 — Bean wiring only in BeanConfiguration
Check that `application/service/*.java` files have no `@Bean`, `@Service`, or `@Component` annotations.
All beans should be defined in `infrastructure/config/BeanConfiguration.java`.

### Rule 6 — Ports must be interfaces
Check that all files in `application/port/in/` and `application/port/out/` are `interface` declarations (not classes).

## Report Format
```
Architecture Audit Report
=========================

Rule 1 (Domain isolation):   PASS / FAIL
  - [list violations with file:line]

Rule 2 (Application isolation): PASS / FAIL
  - [list violations]

Rule 3 (No infrastructure imports in inner layers): PASS / FAIL
Rule 4 (No DTO leakage): PASS / FAIL
Rule 5 (Beans only in BeanConfiguration): PASS / FAIL
Rule 6 (Ports are interfaces): PASS / FAIL

Overall: PASS / X violations found
```

If violations are found, suggest the specific fix for each one.
