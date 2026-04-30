## 1. Refactor Checkout Logic to CheckoutService

- [x] 1.1 Extract shipping address validation and population logic from `CheckoutController.submitShippingAddress` to a new `updateShippingAddress` method in `CheckoutService`. Apply Javadoc and `final` modifiers.
- [x] 1.2 Extract billing address validation and population logic from `CheckoutController.submitBillingAddress` to a new `updateBillingAddress` method in `CheckoutService`. Apply Javadoc and `final` modifiers.
- [x] 1.3 Extract payment parsing/validation and population logic from `CheckoutController.submitPayment` to a new `updatePayment` method in `CheckoutService`. Apply Javadoc and `final` modifiers.
- [x] 1.4 Refactor `CheckoutController`'s existing HTTP POST endpoints to delegate to these new `CheckoutService` methods.

## 2. WebMCP API Checkout Endpoint

- [x] 2.1 Define data structures (DTOs or inline classes) to represent the consolidated WebMCP checkout JSON payload (shipping, billing, payment, and customer info).
- [x] 2.2 Create the `POST /api/v2/checkout` endpoint. This endpoint MUST accept the consolidated payload, use the new `CheckoutService` validation methods, and invoke `CheckoutService.checkout` on success.
- [x] 2.3 Implement robust error handling so that if the `CheckoutService` validation fails, a structured JSON error array is returned instead of an HTML-formatted 500 stack trace.

## 3. WebMCP UI Tool Registration

- [x] 3.1 Update `src/main/resources/templates/layout/default.html` to declare the `submit_checkout` tool parameter schema using `navigator.modelContext.registerTool`.
- [x] 3.2 Add a complete JSON payload example within the `submit_checkout` tool description so AI agents know precisely how to construct their requests.

## 4. Integration Testing

- [x] 4.1 Implement a happy path test in `WebMcpIntegrationTest.java` (or a dedicated checkout test) that successfully submits a complete JSON checkout payload and verifies order creation.
- [x] 4.2 Implement an error test scenario verifying that submitting an invalid credit card number (violating Luhn check) results in the correct 4xx JSON response structure.
- [x] 4.3 Implement an error test scenario verifying that omitting required address fields returns a structured JSON error payload indicating missing data.
