# Tasks: WebMCP Product Variant Exploration Tool

## 1. Controller Rest Mapping
- [x] 1.1 Locate `CatalogController.java` and extract the internal mapping logic from the `productDetail()` HTML endpoint into a private unified builder method (`buildProductDetailDto(...)`).
- [x] 1.2 Construct a new `@GetMapping("/api/v2/catalog/product/{productId}")` endpoint labeled exclusively for JSON payload outputs.
- [x] 1.3 Implement rigorous security checks parsing `@PathVariable("productId")` strictly as a positive integer `int > 0`, bouncing any illegal scopes natively with standard error formats.
- [x] 1.4 Route validated lookups to invoke the extracted `buildProductDetailDto(...)` handler feeding safely via Jackson `@ResponseBody`.

## 2. Agent Context Registration
- [x] 2.1 Update `src/main/resources/templates/layout/default.html` appending `get_product_details` to `window.navigator.modelContext.registerTool()`.
- [x] 2.2 Wire the async execution map seamlessly against the new `CatalogController` JSON route.

## 3. Testing 
- [x] 3.1 Construct a basic integration layer within the `CatalogController` suite formally validating JSON variant assertions and error bounds natively.
- [x] 3.2 Update `WebMcpIntegrationTest.java` incorporating `get_product_details`. Extract its `productId: 1` example payload natively from `default.html`'s tool description and validate that the `CatalogController` REST API outputs the exact array mappings (variant IDs, prices) matching the Grizzly Bear configurations.
- [x] 3.3 Add negative security injections querying `/api/v2/catalog/product/{productId}` natively validating 400 Bad Request/404 assertions when fed illegal states (`productId: -1`, `NaN` strings) bypassing database loading explicitly.
- [x] 3.4 **Database Zero-State Test**: Inject a fully valid integer ID that does *not* exist in the database (e.g., `productId: 99999`) verifying the JSON layer securely handles null object exceptions returning a strict `404 Not Found`.
- [x] 3.5 **Locale Context Parameter Test**: Inject URL locale configurations dynamically mapping `?locale=de-DE` validating the JSON payload successfully mutates currency abstractions (EUR mapping instead of USD).
- [x] 3.6 **HTML Backwards Compatibility Check**: Build an isolated `@WebMvcTest` unit test executing the standard `/product/{name}/{productId}` HTML endpoint to assert the new `buildProductDetailDto(...)` refactoring loop did not fracture the native front-end rendering engine.

## 4. Code Standards Validation
- [x] 4.1 Enforce strict `AGENTS.md` rules globally: aggressively apply `final` modifiers across all extracted variables, method arguments, and fields to force absolute immutability.
- [x] 4.2 Validate robust Javadoc application across the extracted `buildProductDetailDto(...)` mapping helper and the new `@GetMapping` JSON endpoint ensuring absolute clarity.
