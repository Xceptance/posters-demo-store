## Why

AI agents currently have WebMCP capabilities to discover products via search and add them to the cart natively. To complete the automated shopping loop, agents need the ability to perform a secure checkout. By exposing a dedicated WebMCP checkout tool, we empower agents to autonomically complete the final transaction steps—submitting shipping, billing, and payment details all at once—without resorting to brittle UI automation.

## What Changes

- Refactor `CheckoutController.java` to extract its shipping, billing, and payment handling logic into `CheckoutService.java` to adhere to DRY principles and prevent logic divergence.
- Create a new REST endpoint (`POST /api/v2/checkout`) designed specifically for WebMCP json payloads that accepts full checkout details (addresses and credit card) in one request.
- Update `layout/default.html` to register a new WebMCP tool (`submit_checkout`) via the browser API `navigator.modelContext.registerTool`.
- Enhance the tool description to include a practical JSON payload example, demonstrating explicitly how agents should format their address and payment parameters.
- Add comprehensive integration testing for the new WebMCP checkout capability.

## Capabilities

### New Capabilities
- `webmcp-checkout-integration`: Defines the standard for exposing the site's checkout functionality to AI agents via WebMCP, permitting a pure-JSON/API-driven order completion.

### Modified Capabilities
- *(None: Expanding the existing WebMCP toolset does not modify existing non-agent capabilities)*

## Impact

- `CheckoutController.java` and `CheckoutService.java`: Modified to unify handling of shipping, billing, and payment processing.
- Apply rigorous code style hygiene: add comprehensive Javadoc to all new or refactored methods and apply `final` modifiers to variables and parameters wherever possible.
- `src/main/resources/templates/layout/default.html`: Augmented to include the `submit_checkout` tool parameter declarations.
- Automated tests will be introduced to ensure the JSON checkout flow operates reliably for agents. This will include not only happy path validation but also rigorous error tests (e.g., verifying handling of invalid credit cards, missing addresses, or incomplete payloads).
