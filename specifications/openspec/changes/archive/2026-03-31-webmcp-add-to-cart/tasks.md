## 1. Backend JSON Add-To-Cart Endpoint
- [x] 1.1 Create the endpoint `POST /api/v2/cart/add` in a Spring Controller (such as `CartController` or a dedicated `WebMcpController`).
- [x] 1.2 Implement internal logic to safely add the given `productId` to the session cart securely and return a standard JSON Object representing the updated `cartCount`.

## 2. Dynamic WebMCP JavaScript Integration
- [x] 2.1 Update `src/main/resources/templates/layout/default.html` to register a second WebMCP tool called `add_to_cart`.
- [x] 2.2 Wire the tool's Javascript `execute` function to POST the AI's provided JSON payload (`productId`, `quantity`) directly to our new endpoint.

## 3. Automated Testing & Verification
- [x] 3.1 Update `WebMcpIntegrationTest.java` (in `com.xceptance.posters.entity`) to include a new `@Test` verifying the backend `/api/v2/cart/add` endpoint correctly intercepts the payload and increments the cart.
- [x] 3.2 Update the existing homepage layout `@Test` to ensure *both* WebMCP tool injections are successfully rendered on standard client load.
