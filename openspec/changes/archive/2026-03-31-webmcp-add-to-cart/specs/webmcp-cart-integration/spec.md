# WebMCP Cart Integration (webmcp-cart-integration)

## Summary
AI agents must be able to autonomously add specified products to the user's active session cart directly via the browser's model context interface.

## Requirements

### Req 1: Dedicated Add To Cart REST API
- **Action**: Provides a fast, headless endpoint (`POST /api/v2/cart/add`) that expects a JSON payload containing `productId`, `quantity`, `size`, and `finish`.
- **Rules**: Must securely bypass HTML rendering, directly inject the mathematically correct product variant into the backend `Cart` tied perfectly to the user's active session cookie. Must return a structured JSON response reflecting the success state and new total `cartCount`.
- **Constraint**: Must handle invalid product IDs smoothly by returning a false boolean state.
- **Security Check**: The quantity added per request must not exceed 99 to prevent DB overflow or Denial of Service. Text parameters must be clamped to 50 characters to prevent Memory bloat/ReDoS attacks.

### Req 2: Browser-Native Tool Registration
- **Action**: Injects a second `registerTool("add_to_cart")` block into the `navigator.modelContext` object inside `default.html`.
- **Schema**: 
  - `productId`: Required (number).
  - `quantity`: Optional (number, capped at 99 max).
  - `size`: Optional (string, max 50 chars, e.g. "36 x 24 in").
  - `finish`: Optional (string, max 50 chars, e.g. "matte").
- **Execution**: Must use `fetch()` configured to securely pass session credentials to the new POST endpoint, returning the JSON object back up to the AI agent natively so it can confirm success to the human user.
  - *CSRF Support*: The integration must dynamically inspect the DOM for `<meta name="_csrf">` and securely attach an `X-CSRF-TOKEN` header if Spring Security is ever actively deployed, ensuring forward-compatibility.
