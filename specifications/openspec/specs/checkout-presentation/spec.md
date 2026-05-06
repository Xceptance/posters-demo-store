# Rich Checkout Presentation Spec

## 1. Overview
The final step of the checkout flow ("Review & Place Order") must render the exact same rich, visually complete line item details as the active shopping cart view.

## 2. Requirements

### 2.1. Feature Requirements
- **Image Display**: Every product in the checkout summary MUST feature its catalog image URL.
- **Variant Details**: Customizations, such as physical `size` and `finish`, MUST visibly render alongside the product name instead of yielding placeholder empty strings.
- **Symmetry**: Line items rendered in `/checkout/placeOrder` MUST identically mirror those available in `/cart/cart`.

### 2.2. Architecture Requirements
- **DTO Decoupling**: Controllers MUST resolve entity logic down to data transfer objects (`CartDto`) via shared domain mapping services, rather than injecting unstructured Entities (`CatalogCart`) to templates.
- **Price Immutability**: Rendering a `CartDto` MUST default to the `unitPrice` persisted in the `CartLineItem` snapshot, preventing presentation-layer components from triggering background price fluctuations.

### 2.3. Code Quality Requirements
- **Immutability**: All local variables and method parameters MUST be marked `final` wherever computationally possible.
- **Documentation**: All new service methods, configuration objects, and DTOs MUST contain formal Javadoc headers explaining their structural behaviors. 
- **Formatting**: The system MUST adhere to Allman brace styles.

### 2.4. Checkout Indicator Integration
- **Include Checkout Indicator**: All checkout step templates (shipping, billing, payment, overview, and confirmation) MUST incorporate the `checkout-indicator` fragment above the primary checkout content and pass the correct current step identifier to the fragment.

### 2.5. Testing Requirements
- The DTO generation engine MUST have explicit unit coverage ensuring image mappings don't fail for newly added catalog items `CartServiceTest`.
- The presentation layer MUST undergo an end-to-end integration test asserting DOM presence of image references within the Checkout stage `CheckoutControllerUiTest`.
- Refactored `CartController` endpoints must be asserted via regression testing to ensure the DTO extraction does not inherently break the mini-cart or primary cart layouts.
