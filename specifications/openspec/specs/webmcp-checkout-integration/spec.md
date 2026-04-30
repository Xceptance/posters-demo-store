# WebMCP Checkout Integration

## ADDED Requirements

### Requirement: WebMCP Checkout Tool Definition
The system SHALL expose a `submit_checkout` tool via the WebMCP `navigator.modelContext.registerTool` function. The tool description MUST include a JSON payload example explicitly demonstrating how agents should format their address and payment parameters.

#### Scenario: Agent discovers checkout tool
- **WHEN** an AI agent inspects the available tools via WebMCP in the browser context
- **THEN** the agent discovers the `submit_checkout` tool with its complete schema and input example

### Requirement: WebMCP Checkout Endpoint
The system SHALL provide a `POST /api/v2/checkout` JSON API endpoint. This endpoint MUST accept a structured payload containing shipping address, billing address, credit card details, and customer email.

#### Scenario: Successful WebMCP checkout
- **WHEN** the agent submits a valid checkout payload to `POST /api/v2/checkout` for an established session with a populated cart
- **THEN** the system converts the cart to an order and returns a successful response with the order details

### Requirement: Cross-Channel Code De-duplication
The system SHALL ensure that both the human checkout UI and the WebMCP checkout endpoint utilize the exact same business logic for population and validation. This MUST be achieved by extracting logic from `CheckoutController` to `CheckoutService` and adding comprehensive Javadoc and `final` modifiers wherever possible.

#### Scenario: Business logic remains intact
- **WHEN** the business logic for verifying address fields or credit card Luhn constraints runs
- **THEN** the identical `CheckoutService` implementation methods are executed regardless of whether the request originated from HTMX (`CheckoutController`) or WebMCP (`/api/v2/checkout`)

### Requirement: Structured Error Responses
The system SHALL return structured JSON error messages (e.g., {"success": false, "errors": [...]}) from the WebMCP checkout endpoint if any validation fails, instead of defaulting to stack traces and server-rendered HTML errors.

#### Scenario: Invalid credit card payload
- **WHEN** the agent attempts a checkout with a credit card number that fails Luhn validation
- **THEN** the system rejects the payload and returns a HTTP 4xx response with a structured JSON array detailing the validation error(s)

#### Scenario: Missing required address data
- **WHEN** the agent omits required fields (e.g., shipping state or country) from the JSON payload
- **THEN** the system returns a descriptive JSON error specifying which required fields are missing

### Requirement: Authenticated Customer Checkout Binding
The system SHALL correctly bind authenticated customer identity and session details to the order during checkout. This guarantees that registered users can successfully complete checkout without errors, ensuring that stored addresses and customer details are automatically and robustly mapped to the final `CatalogOrder`.

#### Scenario: Registered user completes checkout
- **WHEN** an authenticated human user or agent initiates a checkout flow
- **THEN** the system seamlessly retrieves their registered profile, bypassing manual reassignment, and successfully converts the cart into an order linked directly to their customer account.

### Requirement: Empty Cart Prevention
The system SHALL prevent the conversion of an empty cart into an order. If an authenticated user or agent attempts to complete the checkout flow without any line items in their active cart session, the `CheckoutService` MUST vigorously abort the operation by throwing an `IllegalStateException`.

#### Scenario: Agent attempts checkout with 0 items
- **WHEN** the agent or human invokes the checkout endpoint with an empty cart
- **THEN** the API returns a structured JSON error payload `{"success": false, "errors": ["cart:Cannot place an order for an empty cart."]}` and no database order entity is created.
