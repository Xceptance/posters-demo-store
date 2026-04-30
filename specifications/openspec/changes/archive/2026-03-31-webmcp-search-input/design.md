## Context
AI agents need a standardized way to interact with our search functionality. The official W3C WebMCP standard is a browser-native JavaScript API that allows websites to register tools directly in the frontend context, executed natively by the browser on the agent's behalf.

## Goals / Non-Goals

**Goals:**
- Make site search discoverable and usable by AI agents using the official WebMCP JavaScript API (`navigator.modelContext.registerTool`).
- Ensure the agent receives structured JSON data back from its search execution.
- Validate the integration with a proper `@SpringBootTest` that runs in the CI/CD build pipeline.

**Non-Goals:**
- Using outdated, static JSON payload manifests (`webmcp.json`).
- Building fragile standalone demo scripts that don't hook into the test suite.

## Decisions

- **Decision: Dynamic Browser-Native Integration via JavaScript**
  - **Rationale**: The official W3C draft for WebMCP uses an imperative JavaScript API (`navigator.modelContext.registerTool`). This ensures compatibility with upcoming browser features (e.g., Chrome 146+) and eliminates the need for separate manifest files.
- **Decision: Add JSON Search Endpoint**
  - **Rationale**: Since the WebMCP JavaScript execution block uses `fetch` and expects a JSON return object, we will extend `NewCatalogController.java` to provide a dedicated `/api/v2/catalog/search` endpoint instead of relying on the legacy HTML-returning `SearchController`.

## Risks / Trade-offs

- **Risk**: The `navigator.modelContext` API is still an early W3C draft and behind browser flags.
  - **Mitigation**: We wrap the registration in a safety check (`if (window.navigator.modelContext)`) so it does not throw errors on standard browsers lacking the API.
