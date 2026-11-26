# 5W1H and Experiment Report

## 5W1H

### Who

The intended user is a person choosing a film for a casual watch, and the application is a standalone software-engineering course demo. It has no accounts or user-specific profiles.

### What

The user chooses a genre and writes a preference of up to 300 characters. Spring AI asks a local Ollama model to select up to three films from the matching catalog entries and provide a short reason for each. The app displays the results and keeps successful requests in a history list.

### Where

The browser UI is served locally by Spring Boot at `http://localhost:8080`. Ollama is expected at its local default endpoint. H2 stores runtime data in `data/film-recommender.mv.db`; tests use an isolated in-memory database.

### When

This is a local demonstration and experiment, not a production recommendation service. History is recorded when a valid recommendation response is returned; empty candidate sets and provider/response failures do not create history entries.

### Why

The experiment demonstrates an AI-assisted selection flow while keeping the result space constrained to a known catalog. The model supplies ranking and explanation, not movie facts or arbitrary titles.

### How

1. The browser submits genre and preference text.
2. Server-side validation rejects blank fields and preferences longer than 300 characters before the model boundary.
3. The service loads candidates for the selected genre. If none exist, it returns a no-results outcome without calling Ollama or saving a request.
4. Spring AI sends only those candidates and the preference to Ollama and requests structured movie IDs and reasons.
5. The service rejects malformed, unknown, duplicate, or over-three selections before persistence.
6. Successful selections are saved as a request and ranked items, then shown with the saved history.

## Experiment log

**Run date:** 2026-11-27

**Environment:** Windows; Java 26.0.2 available; project compiler target Java 17; Spring Boot 4.1.0; Spring AI 2.0.0; H2 2.4.240. Maven Wrapper was used.

| Check | Result |
| --- | --- |
| Ollama availability (`ollama --version`) | Not installed. No Ollama installation or model download was attempted. |
| Deterministic MVC tests | Passed: home form, required and oversized input, invalid-input short-circuit, PRG success/history, no results, and visible provider errors. |
| Recommendation service tests | Passed: empty candidates, malformed response, unknown/duplicate IDs, over-three selections, and provider failure; unsuccessful outcomes save no request. |
| File-backed database restart | Passed: a dedicated test closes and reopens the Spring application context against the same temporary H2 file and reloads the request and ranked item. |
| Local HTTP smoke check | Passed: the app returned HTTP 200 on port 18080; the response contained the home page, genre options, and 300-character input limit. Port 8080 was already occupied in this environment, so the smoke check used an alternate port. |
| Live model recommendation | Not run: Ollama was unavailable. No AI-generated recommendation or model-quality claim is reported. |
| Screenshot of model output | Not available because no model output was generated. |

### Observed output

The live smoke check confirmed an HTTP 200 home page with the title “AI Film Recommender”, a genre selector, a preference field limited to 300 characters, and the history area. The actual AI recommendation output remains unobserved; the test suite uses a deterministic stub and is not presented as model output.

### Reproducible checks

Run the deterministic suite with:

```powershell
.\mvnw.cmd test
```

To try the model experiment on a machine with Ollama:

```powershell
ollama pull llama3.2
$env:SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL = "llama3.2"
.\mvnw.cmd spring-boot:run
```

Choose a genre and a preference in the browser, then record the actual returned titles and reasons along with the model name and version. Do not treat test-fixture responses as experimental results.

## Limitations

- The catalog is deliberately small and curated (14 films across seven genres), so recommendations are limited to those entries.
- No live model behavior, latency, or recommendation quality was measured in this environment.
- H2 and the default local Ollama setup are for demonstration; authentication, production database migrations, and deployment hardening are out of scope.
