# AI Film Recommender

A standalone Spring Boot demo that recommends up to three films from a small, curated catalog. It uses Spring AI with a locally running Ollama model; the model can select catalog entries and explain their fit, but cannot introduce films outside the supplied candidates. Requests and their ranked results are saved in H2.

## Requirements

- JDK 17 or newer
- Ollama running locally with a chat model available
- No separate Maven installation is needed; use the included Maven Wrapper

## Run locally

1. Install and start Ollama, then download a model supported by your machine. For example:

   ```powershell
   ollama pull llama3.2
   ```

2. Select the local model and start the application:

   ```powershell
   $env:SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL = "llama3.2"
   .\mvnw.cmd spring-boot:run
   ```

3. Open <http://localhost:8080>.

The Ollama service uses its local default endpoint. No cloud API key is required. The application opens without calling the model, but recommendations require Ollama to be running with the selected model available.

The curated catalog is seeded on startup. Runtime data is stored in `data/film-recommender.mv.db`; the `data/` directory is ignored by Git. The demo uses the local H2 `sa` account with a blank password and is not configured for production deployment.

## Test

The deterministic suite uses an in-memory H2 database and a stub selection client; it never downloads a model or contacts Ollama.

```powershell
.\mvnw.cmd test
```

## Design and experiment notes

- [5W1H, setup, and experiment log](docs/experiment-report.md)
- [UML diagrams and database model](docs/data-model.md)
- [Approved implementation plan](docs/superpowers/plans/2026-11-27-ai-film-recommender.md)

## Scope and limitations

The demo has one browser form, a fixed catalog, and saved recommendation history. It does not include user accounts, catalog administration, an external movie API, or RAG. The language model response is validated for candidate membership, duplicates, result count, and reason length before anything is persisted.

The project targets Java 17 and uses Spring Boot 4.1.0 and Spring AI 2.0.0. Compatibility was checked against the [Spring AI 2.0 GA announcement](https://spring.io/blog/2026/06/12/spring-ai-2-0-0-GA-available-now/), and the [Spring AI Ollama chat reference](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html).
