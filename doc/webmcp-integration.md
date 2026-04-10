# WebMCP Integration Guide

The Posters Demo Store implements the **W3C Web Model Context Protocol (WebMCP)**, allowing standard HTML e-commerce stores to support AI agents. 

By implementing WebMCP, AI Agents (running locally on the user's browser or via extensions) can interact with the storefront natively—fetching product variations, calculating localized unit prices, and executing shopping cart transactions without scraping the DOM.

## 1. What is WebMCP?
WebMCP is an emerging browser standard enabling web properties to declaratively expose structured "Tools" directly to an active AI context via JavaScript. This bridges the gap between conversational LLMs and strict, database-driven transactional systems (e.g., traditional e-commerce backends or enterprise APIs). 

Instead of an agent visually identifying and interacting with UI elements, it executes backend logic through the `window.navigator.modelContext` registry. Because this executes natively in the browser's JavaScript sandbox, deploying this capability requires no external infrastructure—there is no need to host, maintain, or authenticate a separate standalone MCP server.

### Prerequisites (Enabling WebMCP)
WebMCP is an emerging standard, and the `navigator.modelContext` API may not be available by default in all browser builds. 

To test or develop this capability locally, you must provide the API in your browser environment:
1. **Using the Chrome Extension**: Install the **WebMCP - Model Context Tool Inspector** browser extension to provide the `navigator.modelContext` object in active browser contexts dynamically. (No manual feature flags required).
2. **Native Experimental Chrome Builds**: Alternatively, if you are testing native Chrome upcoming AI features without the extension, enable the experimental feature switches by navigating to `chrome://flags` and toggling **Enable Experimental Web Platform Features**.

## 2. Technical Implementation Architecture

Making the Posters Store support WebMCP required three modifications:

### A. The Front-End Tool Registry
In `src/main/resources/templates/layout/default.html`, capabilities are exposed to the agent context using `window.navigator.modelContext.registerTool(...)`. 

Currently, Posters Demo Store exposes 4 capabilities:
1. **`search_catalog`**: Allows the agent to query the Lucene index, returning an array of items and minimum base prices.
2. **`get_product_details`**: Retrieves specific geometries, localized sizing, and variant parameters bound to integer IDs. 
3. **`add_to_cart`**: Translates integer Product IDs combined with string inputs (`"16x12"`, `"matte"`) into shopping cart items attached to the user's session.
4. **`submit_checkout`**: Facilitates e-commerce validation to convert carts into confirmed orders using Address/Payment JSON objects.

For instance, this is how `get_product_details` structurally maps:
```javascript
window.navigator.modelContext.registerTool({
    name: "get_product_details",
    description: "Retrieves granular details (sizes, finishes, minimum prices, and exact variants) for a specific product ID.",
    execute: async (params) => {
        const response = await fetch(`/api/v2/catalog/product/${params.productId}?locale=${locale}`);
        return await response.json();
    }
});
```

### B. The JSON Gateway (Controller Layer)
Thymeleaf HTML endpoints are intended for UI representation. Therefore, shared business logic (e.g., in `CatalogController.java` and `CartController.java`) was extracted into isolated JSON API gateways using `@ResponseBody`.

```java
@GetMapping(value = "/api/v2/catalog/product/{productId}", produces = "application/json")
@ResponseBody
public ProductDetailDto getJsonProductDetails(@PathVariable("productId") final int productId) {
    // Validates inputs, fetches catalog variants, and outputs an AI-friendly JSON schema
}
```

### C. String Normalization (Service Layer)
AI agents may supply payload formats with additional variations (e.g., passing `"16x12 in"` or `" matte "` instead of raw database representations). 

To handle this, backend services (like `CartService.java`) strip visual metrics and whitespace using `.trim()` and `replace()` operations. This ensures dimensions correctly match the database entries.

## 3. Best Practices for Developers
When extending the Posters platform with new WebMCP capabilities:
1. **Use shared logic**: Extract functionality out of HTMX/Spring MVC paths into Service components. Both the AI's JSON endpoint and the HTML UI should utilize the identical underlying logic.
2. **Implement Input Validation**: AI agents may submit invalid data types or unexpected values. Provide validation that returns appropriate HTTP statuses, such as 400 Bad Request.
3. **Persist the Session and CSRF State**: The Agent's JavaScript `fetch()` calls natively inherit the active browser session (`JSESSIONID`) by default due to standard cross-origin properties. However, for any state-mutating requests (e.g. `POST`), developers must explicitly extract the Spring Framework CSRF rules from the HTML `<meta>` tags and inject them into the `fetch` headers securely so the transaction is not blocked by the backend:
   ```javascript
   const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
   const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
   // Inject these into your fetch() headers alongside the AI's JSON payload
   ```
