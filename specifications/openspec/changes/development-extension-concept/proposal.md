## Why

Currently, the Posters storefront relies on hardcoded classpath-based templates and monolithic Spring MVC controllers to handle storefront requests. To support high customizability, multi-site deployments, and dynamic cartridge overrides without altering base controller logic, we need a flexible request-handling and rendering architecture.

By combining the filesystem-based "cartridge" template engine with a "Pipeline of Activities" concept, we can map storefront URLs to dedicated pipelines of sequential handler steps (activities). This allows cartridges to easily customize storefront request processing by extending, appending, prepending, or overriding specific activities in a URL's pipeline (e.g., product detail, cart, checkout) without modifying the base core framework code.

## What Changes

- **NEW** Filesystem-based cartridge template resolver that searches a configurable hierarchy of directories for template files.
- **NEW** Cartridge configuration properties (e.g., `storefront.cartridges`) to define the priority order of search paths.
- **NEW** Template watcher/invalidation mechanism to monitor active cartridge template directories for modifications, automatically clearing the Thymeleaf template cache so changed templates are re-parsed instantly.
- **NEW** Storefront Pipeline Engine mapping storefront URLs to dedicated execution pipelines.
- **NEW** Pipeline and Activity model interfaces (`StorefrontPipeline` and `PipelineActivity`) representing structured request-processing segments (e.g., load data, apply discounts, select template, execute extension hooks).
- **NEW** Cartridge-aware Pipeline Registry that builds and executes URL pipelines at runtime, allowing active cartridges to dynamically contribute, re-order, or override activities in the pipeline based on the cartridge hierarchy.
- **NEW** Fallback resolution mechanism to default classpath templates if a template is not found in any of the active cartridges in the hierarchy.
- **MODIFY** Refactor existing core storefront controllers (e.g., HomeController, ProductController, CartController) to delegate request processing to dedicated, URL-mapped storefront pipelines.

## Capabilities

### New Capabilities
- `hierarchical-cartridge-templates`: A custom Thymeleaf filesystem template resolution engine that searches and loads templates from a priority-based list of filesystem cartridge directories, supporting live template overrides and hot-reloading (via template cache invalidation) for rapid storefront adjustment.
- `storefront-activity-pipelines`: A request-processing engine mapping storefront URLs to dedicated pipelines of sequential activities (e.g., data loading, calculation, template selection). This enables active cartridges to contribute custom activities or override existing ones dynamically based on the configured cartridge hierarchy, eliminating hardcoded controller logic.

### Modified Capabilities
- `storefront`: Refactor storefront URL routing, request handling, and model preparation to utilize dynamic activity pipelines and cartridge template resolution.

## Impact

- **Affected Components**: Spring Boot Thymeleaf configuration, Spring Controller layer (refactored to delegate to storefront pipelines), and core request mapping configurations.
- **Properties**: `application.yml` additions for configuring active cartridges and pipeline overrides.
