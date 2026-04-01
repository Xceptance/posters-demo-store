## Why
Now that AI agents can successfully search the store catalog via a native WebMCP tool, they need a way to act on those findings. Enabling a new WebMCP tool empowers agents to autonomously build a user's shopping cart. This bridges the gap between simply "finding" products and actually executing commerce actions without relying on brittle UI automation.

## What Changes
- Register an `add_to_cart` tool within the existing `navigator.modelContext` Javascript block in `default.html`.
- Define an input schema requiring the AI to provide a product ID (and optionally, a quantity).
- Add a dedicated JSON endpoint (or adapt `CartController`) to safely add items to the active user's session cart in the background, rather than bouncing back an HTML fragment.
- Protect the new integration with a comprehensive `@SpringBootTest`.

## Capabilities
### New Capabilities
- `webmcp-cart-integration`: Adds WebMCP support for cart modifications to allow pure-JSON/API-driven cart assembly in real-time.

### Modified Capabilities
- *(None: Expanding the existing WebMCP toolset does not modify existing non-agent capabilities)*

## Impact
- **Template Layer**: The `default.html` template will be expanded with a second tool registration.
- **Controller Layer**: Controller adjustments required to cleanly accept and return JSON payloads for adding to the cart, avoiding standard Thymeleaf view resolution.
- **Security**: Must ensure that adding to cart via this endpoint securely utilizes the existing Spring Session / cart architecture.
