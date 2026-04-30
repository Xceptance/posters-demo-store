## Context

AI agents integrated via WebMCP can currently search for products and add them to their shopping cart. However, the existing checkout flow in the application is tightly coupled to the multi-step frontend `CheckoutController` (shipping, billing, payment, and place order). To automate the end-to-end shopping loop, agents require a secure mechanism to finalize an order. Tying the agent to the UI-centric multi-step flow is brittle and error-prone.

## Goals / Non-Goals

**Goals:**
- Provide a robust, single-step JSON integration endpoint `POST /api/v2/checkout` allowing an active session cart to be converted into an order.
- Refactor existing `CheckoutController` logic (e.g., address creation, credit card parsing/validation) into `CheckoutService` to guarantee the exact same constraints apply across UI and WebMCP interfaces while adhering to DRY principles.
- Use explicit Java conventions in all touched files (comprehensive Javadoc and `final` modifiers for immutability where possible).
- Implement thorough error condition testing, explicitly asserting behavior around invalid credit cards, missing datasets, and malformed inputs.
- Register `submit_checkout` via `navigator.modelContext.registerTool` natively in the browser with an actionable example.

**Non-Goals:**
- Refactoring the underlying database or entity schemas for carts and orders.
- Modifying the visual UI/HTMX flow of the human user checkout experience beyond the underlying service delegation.

## Decisions

- **Decision 1: Extracting Validation and Data Population to Service Layer**
  The logic for instantiating and attaching `CartAddress` and `CartCreditCard` to a `CatalogCart`, as well as validating them, will be migrated from `CheckoutController` to `CheckoutService`. 
  *Rationale:* This prevents code duplication and ensures that WebMCP cannot bypass domain constraints (like credit card Luhn checks or required fields). 
  *Alternative Considered:* Creating a separate validation path for WebMCP JSON payloads. This was rejected because maintaining two sets of business rules leads to inevitable drift.
  
- **Decision 2: Single-Step JSON Payload for WebMCP**
  The WebMCP integration will accept an aggregate JSON object containing shipping, billing, payment, and customer details, allowing a cart checkout in one API hit. 
  *Rationale:* Unlike human users who benefit from a progressive disclosure multi-step wizard, AI agents excel at submitting comprehensive data payloads. Forcing the agent to maintain session state across a 4-step sequence greatly increases the risk of hallucination or dropped sessions.

- **Decision 3: Structured JSON Error Handling**
  Instead of throwing fatal HTTP 500 exceptions on validation failure (e.g. invalid credit card number), the `/api/v2/checkout` endpoint will return structured JSON error responses (e.g. `{"success": false, "errors": ["cardNumber:Please enter a valid credit card number."]}`).
  *Rationale:* This allows the AI agent to receive precise feedback and attempt to correct its output (e.g. prompt the user for a new card) rather than encountering an opaque server error.

## Risks / Trade-offs

- **Risk: Disconnected Validation Rules during Refactor**
  *Mitigation:* We will enforce rigorous automated integration testing. We won't just test the "happy path"—we will introduce failure mode tests that explicitly submit invalid inputs (e.g. a bad Luhn digit in the credit card payload or missing state names) to ensure the service layer correctly rejects them.

- **Risk: Exposing Checkout to Automated Abuse**
  *Mitigation:* The WebMCP tool requires an established session with a populated cart to function, so it retains the existing anti-abuse paradigms inherent to an active session context (agents can only act on the user's explicit cart).
