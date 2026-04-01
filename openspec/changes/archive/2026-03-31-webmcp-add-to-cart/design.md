## Context
We successfully introduced WebMCP search functionality, allowing native browser-integrated AI agents to query the catalog without DOM scraping. To complete the shopping loop natively, we need to expand this integration by allowing the AI to add products to the user's shopping cart automatically when instructed.

## Goals / Non-Goals
**Goals:**
- Design a reliable JSON-native API endpoint (e.g., `/api/v2/cart/add`) that securely accepts a `productId` and `quantity`, adds them to the Spring Session cart, and returns the updated cart state.
- Inject the `add_to_cart` tool schema into `default.html` using the W3C draft API.
- Ensure proper error handling if a product ID is invalid or out of stock, returning clean JSON errors.

**Non-Goals:**
- Handling the full checkout or payment process (this will be a separate capability in the future).
- Modifying or breaking the existing UI-driven HTMX flows used by human users.

## Decisions
- **Decision: Dedicated JSON API Endpoint.** We will build a new endpoint tailored specifically to WebMCP rather than attempting to coerce the existing Thymeleaf/HTMX-returning cart methods.
  - *Rationale*: AI agents expect structured, machine-readable JSON (not an HTML snippet to inject into a DOM they cannot see). This isolates the AI API from UI-specific logic.
- **Decision: Tool Schema Definition.** The input properties strictly require `productId` (integer) and optionally accept `quantity` (capped at 99, default 1), `size` (string, max 50 chars) and `finish` (string, max 50 chars).
  - *Rationale*: Forces the AI to conform to backend constraints *before* the request is fired. Supplying variant properties empowers the agent to fulfill extremely specific product selections while securely clamping inputs mitigates DB overflow or OOM attacks globally.

## Risks / Trade-offs
- **Session Management**: The AI tool `fetch` request must be tightly bound to the current user's session cookie. If not, the items will go into a void "new" cart.
  - *Mitigation*: Ensure the fetch implementation implicitly includes credentials (usually the default for same-origin `fetch`), and verify session continuity using comprehensive `@SpringBootTest` tests mocking the active checkout session.
- **CSRF Token Resilience**: Since standard `POST` requests require CSRF tokens under Spring Security, hardcoding the fetch request to ignore CSRF breaks future compliance, while rigidly demanding a token breaks the tool on unprotected instances.
  - *Mitigation*: Use a progressive enhancement pattern inside the tool `execute` block. Dynamically sniff the DOM payload for Spring's injected metadata tags (`_csrf` and `_csrf_header`) and dynamically construct the POST headers exclusively when they exist.
