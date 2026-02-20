# Test Command

Run the full test suite for backend and frontend. Provide a clear summary.

## Steps

1. **Backend tests**
   ```bash
   cd backend && mvn test
   ```
   - Parse the Surefire output.
   - Report: tests run, failures, errors, skipped.
   - If any test fails, show the failing test name, the expected vs actual values, and the stack trace.

2. **Frontend tests**
   ```bash
   cd frontend && npm test -- --run
   ```
   - Report: tests passed, failed, total.
   - Show failing test details.

3. **Coverage** (optional, run only if the user asks)
   ```bash
   cd backend && mvn verify -Pcoverage
   ```

## Summary Format
```
Backend:  X passed, Y failed, Z skipped
Frontend: X passed, Y failed

[List any failures with file:line references]
```

If all tests pass, confirm and suggest running `/build` next if not already done.
