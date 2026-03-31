## 1. Backend JSON Search Endpoint

- [x] 1.1 Update `SearchController.java` to add a new REST endpoint: `GET /api/v2/catalog/search?q={query}`.
- [x] 1.2 Implement the endpoint to leverage `CatalogService` or `LuceneSearchService` to return a JSON array of search results, matching the structure expected by the AI agent.

## 2. Dynamic WebMCP JavaScript Integration

- [x] 2.1 Update the default layout template (`src/main/resources/templates/layout/default.html`) to include the official WebMCP JavaScript snippet.
- [x] 2.2 Securely wrap the execution payload in the script: `if (window.navigator.modelContext) { navigator.modelContext.registerTool(...) }` pointing its internal `fetch` API to our new `/api/v2/catalog/search` endpoint.

## 3. Automated Testing & Verification

- [x] 3.1 Write a robust `@SpringBootTest` equipped with `MockMvc` (e.g., `WebMcpIntegrationTest.java`).
- [x] 3.2 Add a test case verifying the new JSON search endpoint works correctly.
- [x] 3.3 Add a test case verifying the homepage (`/`) accurately serves the `<script>` tag containing the WebMCP registration logic.

## 4. Documentation

- [x] ~~4.1 Update `README-webmcp.md`~~ *(Deleted per user review - keeping repository pristine)*
