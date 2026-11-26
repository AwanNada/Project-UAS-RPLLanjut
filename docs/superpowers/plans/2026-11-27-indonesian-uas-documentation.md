# Indonesian UAS Documentation Plan

> **For agentic workers:** Execute this plan task-by-task in the current repository. This is a documentation-only change; do not modify application code.

**Goal:** Rewrite the reader-facing project documentation in clear, natural Indonesian without changing technical identifiers or inventing experiment results.

**Architecture:** Keep the existing documentation structure and links. Translate and edit the README, experiment report, and data model document; leave the previously approved implementation plan in English.

**Tech Stack:** Markdown, Spring Boot, Spring AI, Ollama, H2.

**Spec:** `docs/superpowers/specs/2026-11-27-indonesian-uas-documentation-design.md`

## Global Constraints

- Write the reader-facing documentation in Bahasa Indonesia that is direct, natural, and not repetitive.
- Preserve class names, endpoints, configuration keys, commands, version numbers, and diagram identifiers.
- Report only verified tests and observations.
- Do not fabricate Ollama output, quality claims, or screenshots.
- Do not change application code or the existing implementation plan.

## Review Focus

- Setup instructions must match the Spring Boot and Ollama configuration in the repository.
- Test results, environment details, and model availability must be stated with their actual evidence and timeframe.
- Diagram labels and explanations must agree with the entity relationships and schema in the code.
- Any actual model result or screenshot not yet supplied must remain explicitly unrecorded.

---

### Task 1: Rewrite reader-facing documentation in Indonesian

**Files:**
- Modify: `README.md`
- Modify: `docs/experiment-report.md`
- Modify: `docs/data-model.md`
- Keep unchanged: `docs/superpowers/plans/2026-11-27-ai-film-recommender.md`

**Interfaces:**
- Consumes: Current application behavior and configuration in `src/main/java/com/awannada/filmrecommender/`, `src/main/resources/application.properties`, and `pom.xml`.
- Produces: Indonesian user documentation that retains runnable commands and accurate technical details.

- [ ] **Step 1: Rewrite `README.md` in Indonesian**

  Keep the existing sections for requirements, running the application, tests, related documents, and scope. Preserve the PowerShell commands and URLs verbatim.

- [ ] **Step 2: Rewrite `docs/experiment-report.md` in Indonesian**

  Keep the 5W1H structure, environment and test evidence, edge-case table, and limitations. Clearly distinguish the initial no-Ollama check from later user-reported installation/model download. Do not invent model output or screenshots.

- [ ] **Step 3: Rewrite `docs/data-model.md` in Indonesian**

  Translate diagram headings, human-facing labels, and physical model explanations. Preserve Mermaid/PlantUML syntax, entity/table/column names, and database types.

- [ ] **Step 4: Verify documentation consistency**

  Run `git diff --check`. Compare commands and technical claims to the current source, build file, test reports, and observed Ollama output. Confirm the implementation plan is unchanged.

  Expected: no whitespace errors; all commands and claims match their sources; no unobserved model results are presented as fact.
