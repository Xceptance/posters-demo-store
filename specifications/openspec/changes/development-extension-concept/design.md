## Context

Posters is a Spring Boot storefront application using standard class-path Thymeleaf template loading and monolithic controller mappings. It has hardcoded layouts and templates, which makes template customizations or logic extensions (hooks) impossible without core codebase changes, rebuilds, and redeployments. To enable modular, flexible, and non-intrusive storefront development, we will introduce a filesystem-based "cartridge" template resolution engine combined with URL-mapped dynamic Activity Pipelines.

## Goals / Non-Goals

**Goals:**
- Provide filesystem cartridge search paths for templates, resolved sequentially at runtime.
- Support live template hot-reloading by invalidating the Thymeleaf cache when files in the cartridge paths are edited.
- Map storefront URLs to dynamic pipelines of sequential activities (`StorefrontPipeline` and `PipelineActivity`).
- Enable standard Java classes (loaded via normal classpath) to hook into and customize URL pipelines dynamically based on active cartridge configurations at runtime.
- Prevent core controllers and services from having hardcoded page rendering and data loading logic.

**Non-Goals:**
- Dynamically compiling Java source files from the filesystem cartridges at runtime.
- Hot-swapping class definitions or custom JVM ClassLoaders (all classes are built/loaded normally).
- Providing an dynamic Admin UI inside the storefront to edit templates (this is a developer-focused extension framework).

## Decisions

### Decision 1: Hierarchical Cartridge Template Resolution
- **Approach:** Create a custom Thymeleaf `FileTemplateResolver` configured with a list of filesystem base directories representing active cartridges (e.g. `[custom-brand, default]`). The resolver will check each cartridge base path sequentially for the requested view name (e.g., `templates/product/detail.html`).
- **Caching & Hot-Reloading:** In local development mode, caching is disabled. In production, caching is enabled, but a filesystem `WatchService` monitors the cartridge directories and triggers a global Thymeleaf cache eviction on modification events.
- **Alternatives Considered:**
  - *Database-driven template storage:* Rejected due to DB round-trip overhead and unnecessary complexity for developers who prefer editing filesystem markup.

### Decision 2: URL-to-Pipeline Dispatching & Dynamic Activities
- **Approach:** Define an interface-based pipeline system:
  ```java
  public interface PipelineActivity {
      void execute(PipelineContext context);
  }
  ```
  And a pipeline runner:
  ```java
  public class StorefrontPipeline {
      private final List<PipelineActivity> activities;
      public void execute(PipelineContext context) {
          for (PipelineActivity activity : activities) {
              activity.execute(context);
          }
      }
  }
  ```
  Instead of hardcoded controller methods, base controllers will look up and execute URL-mapped pipelines (e.g., `home-pipeline`, `product-detail-pipeline`).
- **Alternatives Considered:**
  - *Spring `@ControllerAdvice` or `HandlerInterceptor`:* Rejected because interceptors are too coarse-grained and do not easily allow individual step replacements or insertion of custom business logic between specific loading/calculation stages.

### Decision 3: Cartridge-Aware Pipeline Extension Hooks
- **Approach:** Let active cartridges contribute custom activities or override existing ones by registering `PipelineActivity` beans annotated with a custom qualifier or implementing a priority interface. A `PipelineBuilder` bean constructs the final execution chain at startup (or dynamically resolves it at request time) based on the active cartridge list configured in `application.yml` (e.g., `storefront.cartridges=custom-brand,core`).
- **Attribution & Code Style:**
  - All new files will adhere to the Allman coding style (braces on new lines).
  - Use aggressive `final` modifiers, strict top imports, and no inline FQCNs.
  - Exclusively AI-created files must be marked with class comments: `// AI-generated: Gemini 3.5 Flash`.
  - Add Apache License header to all new source files in `posters-demo-store`.

## Risks / Trade-offs

- **[Risk] Missing templates leading to file resolution errors**
  - *Mitigation:* Ensure a fallback classpath template resolver is registered at the lowest priority so core templates are always available.
- **[Risk] Multiple cartridges overriding the same activity with conflicting order**
  - *Mitigation:* Explicitly enforce ordering via `@Order` annotation or sorted cartridge paths (first cartridge in the path wins).
