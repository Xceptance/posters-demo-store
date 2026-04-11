# Design: WebMCP Product Variant Tool Integration

## 1. REST Endpoint Abstraction (Zero Duplication)
Instead of rewriting mapping logic, we will aggressively refactor the existing code inside `CatalogController` to prevent any code duplication. 
- Extract the core `ProductDetailDto` assembly logic out of the native `productDetail()` HTML endpoint into a shared, centralized helper method (e.g. `buildProductDetailDto(Product entity, String locale, String currency)`).
- Both the pre-existing front-end HTML mapping endpoint and the new API JSON interface (`@GetMapping(value = "/api/v2/catalog/product/{productId}", produces = "application/json")`) will exclusively call this single, unified pipeline.
- The dedicated WebMCP route will merely operate as a `@ResponseBody` gateway wrapping around the shared entity logic.

## 2. Security and Input Validation
- The `@PathVariable("productId")` must be strictly typed as a positive `int` protecting against malicious injection strings.
- Explicitly validate `productId > 0`. If the validation fails, immediately return a rigorous `400 Bad Request` or `404 Not Found` bypassing all database lookups.
- Only load records via parameterized JPA lookups built into `catalogService.getProductById(productId)`.

## 2. WebMCP API Registry
Inside the `src/main/resources/templates/layout/default.html` script block:
- Execute `window.navigator.modelContext.registerTool`.
- Target identity: `get_product_details`.
- Required `productId` format: `number`.
- Dynamic fetch request directed at `/api/v2/catalog/product/${params.productId}?locale=${localeCode}` resolving natively within the agent layer.
