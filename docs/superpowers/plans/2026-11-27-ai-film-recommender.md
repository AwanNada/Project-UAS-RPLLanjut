# AI Film Recommender Implementation Plan

## Goal and scope

Build a standalone Java 17 Spring Boot movie recommender. Do not modify the leave/Camunda project. Use Spring Boot 4.1.x and Spring AI 2.0.x through the Spring AI BOM, Spring MVC/Thymeleaf, validation, JPA/Hibernate, H2, and the Spring AI Ollama starter. The dedicated public repository already exists and contains the approved starter README; do not recreate it.

The user chose local Ollama to avoid API charges. Check `ollama --version` before any experiment. Do not install Ollama or download a large model without asking. Runtime H2 is file-backed; tests use in-memory H2.

## Application behavior

- Accept a catalog genre and a nonblank free-text preference of at most 300 characters.
- Select up to three films from a modest curated catalog and show title, year, genre, and a short reason; display saved history.
- Seed the catalog idempotently.
- Give Spring AI only matching catalog candidates; request structured film IDs and reasons, then validate IDs are known and unique and the result count is at most three before persistence.
- An empty candidate list produces an explicit no-results outcome and saves no request.
- Malformed model output or provider errors are visible and save no fake results.
- Do not add an external catalog API, accounts, admin CRUD, or RAG.

## Data model

- `Movie`
- `RecommendationRequest` (genre, preferences, request time)
- `RecommendationItem` (request, movie, rank, reason)

## Global constraints

- Use TDD: add a behavior test first, run it to observe the expected failure, implement the minimum, then rerun it and the task's focused suite.
- Keep deterministic tests independent of Ollama; tests must not require network access or model downloads.
- Persist H2 data only at runtime; do not commit local H2 files or secrets.
- Do not push application code, open a PR, or otherwise publish additional content before the coordinator obtains final user confirmation.
- Include the required co-author trailer on every commit: `Co-authored-by: Copilot App <223556219+Copilot@users.noreply.github.com>`.

## Task 1: Scaffold the standalone recommender

Create the Maven project and wrapper, `FilmRecommenderApplication`, runtime and test H2 configuration, `.gitignore`, and this plan copy. Write a `contextLoads` test before the application class and observe the expected compile failure, then add the minimal application/configuration and confirm the test passes. Runtime H2 must use a local file; test H2 must be in-memory. Ensure local database files and secrets are ignored.

**Produces:** a Java 17-compatible Maven build, executable wrapper, app context, H2 test/runtime profiles, and an isolated project skeleton.
**Verification:** `.\mvnw.cmd -Dtest=FilmRecommenderApplicationTests test` fails before the application exists and passes after implementation; `.\mvnw.cmd test` passes.
**Expected:** the first run fails because the context/application class is missing; the second run exits 0 and reports the context test passing.
**Commit:** `feat: scaffold standalone film recommender`

## Task 2: Persist the catalog and recommendation history

Add `Movie`, `RecommendationRequest` (genre, preferences, request time), and `RecommendationItem` (request, movie, rank, reason), repositories, and an idempotent curated-catalog seeder. Add tests for idempotent seeding, distinct available genres, and saving/loading a request with ranked recommendation items.

**Consumes:** Task 1's Spring Boot/JPA/H2 test setup.
**Produces:** catalog and history persistence available to the recommendation service and web flow.
**Verification:** `.\mvnw.cmd -Dtest=MovieCatalogRepositoryTests,RecommendationHistoryRepositoryTests test`, then `.\mvnw.cmd test`.
**Expected:** repository tests prove repeated seeding does not duplicate movies, genres are distinct, and persisted request/item relationships reload correctly; all tests exit 0.
**Commit:** `feat: persist film catalog and recommendation history`

## Task 3: Recommend catalog films with Spring AI

Add `RecommendationForm`, `MovieSelection`, `MovieSelectionResponse`, `RecommendationOutcome`, `RecommendedFilm`, `MovieSelectionClient`, `SpringAiMovieSelectionClient`, and `MovieRecommendationService`. Provide only matching catalog candidates to Spring AI, request structured candidate IDs and reasons, and validate every selected ID is known and unique and the result count is at most three before persistence. An empty candidate list returns no results without saving a request. Malformed output and provider errors must be explicit and must not persist fake results.

Add deterministic tests for valid selection, no candidates, unknown IDs, duplicate IDs, more than three IDs, malformed model output, and provider failure. Do not call Ollama in tests.

**Consumes:** Task 2's movie catalog and persistence interfaces.
**Produces:** a validated recommendation outcome and an Ollama-backed selection adapter.
**Verification:** `.\mvnw.cmd -Dtest=MovieRecommendationServiceTests,SpringAiMovieSelectionClientTests test`, then `.\mvnw.cmd test`.
**Expected:** all specified behavior tests pass without any Ollama process/model; invalid selections and provider failures persist no request/items.
**Commit:** `feat: recommend catalog films with Spring AI`

## Task 4: Add the recommendation form and history

Add `HomeController` and `index.html`; implement GET `/` and POST `/recommendations` using Post/Redirect/Get. Render genre choices and preference input, show up to three recommendation cards and their reasons, display history, and make no-results/provider/validation errors visible. Invalid input must not call the model.

Add MVC tests for form rendering, required/oversized input validation, validation avoiding a model call, recommendations and history, no-results, and provider errors.

**Consumes:** Task 3's recommendation service/outcomes and Task 2's persistent catalog/history.
**Produces:** the complete browser flow.
**Verification:** `.\mvnw.cmd -Dtest=HomeControllerTests test`, then `.\mvnw.cmd test`.
**Expected:** MVC tests pass for each flow; invalid requests do not reach the model-selection boundary.
**Commit:** `feat: add film recommendation form and history`

## Task 5: Verify and document the experiment

Run the full deterministic suite. Check `ollama --version`; perform a manual experiment only if Ollama is already installed and available, otherwise record that the experiment is blocked without installing Ollama or downloading a model. Document setup/configuration, README, 5W1H, experiment log, observed output and screenshots when available, UML use-case/class/sequence diagrams, logical ERD, and physical data model. Include manual checks for oversized input, empty catalog, unknown/duplicate/too-many IDs, and persistence across a restart.

**Consumes:** the completed application, tests, and persistence model from Tasks 1–4.
**Produces:** runnable documentation and experiment evidence without publishing.
**Verification:** `.\mvnw.cmd test`; manually verify persisted file-backed H2 data across a restart if the app can be run.
**Expected:** the full test suite exits 0; documentation identifies the actual environment/results and accurately reports Ollama availability.
**Commit:** `docs: document film recommender UAS experiment`

## Verification

Use TDD for each behavior: add the failing test, run and observe the expected failure, implement the minimum, then rerun the test and full suite. Include tests/manual checks for unknown IDs, duplicate IDs, more than three results, empty catalog, oversized input, and persistence across restart. Verify the supported Spring Boot/Spring AI pairing against official documentation.

## Publication and secrets

Do not push application code, open a PR, or otherwise publish additional content until the coordinator obtains final user confirmation. Only the starter README was authorized for immediate publication. Do not commit H2 data files or secrets.
