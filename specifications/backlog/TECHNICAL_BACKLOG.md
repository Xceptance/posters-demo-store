# Technical Backlog

> [!IMPORTANT]
> This document tracks all technical-focused Epics, Features, Improvements, and Tasks.
> **Numbering Policy**: Never reuse ID numbers. When adding new items, always count upwards from the highest existing number, even if earlier items are deleted or moved.

## Epics
- [ ] TECH-EPIC-1: Testing backoffice for manipulation of behavior

## Features
- [ ] TECH-FEAT-1: Template concepts and standardized API for using of templates
- [ ] TECH-FEAT-2: Themes for templates
- [ ] TECH-FEAT-3: Autoimage scaling via url service includes image transformation

## Improvements
- [ ] TECH-IMPR-1: Integrate jOOQ for type-safe query building — replace ~13 raw JPQL strings (em.createQuery) across WebShopController, CatalogController, SearchController, CartController, CartService, LocalizedTextService, and CatalogDataLoader with jOOQ fluent API. Use JPA+jOOQ hybrid: keep Spring Data repos for CRUD, use jOOQ for complex reads. Requires spring-boot-starter-jooq + codegen Maven plugin.
- [ ] TECH-IMPR-2: Redirect to account area after registration — Currently users land on homepage after successful registration, which is confusing. Should redirect to `/[locale]/account` instead to provide immediate confirmation and access to account features.

## Bugs
- [ ] TECH-BUG-1: Cart quantity update causes page-in-page rendering — HTMX cart update (updateProductCount) returns full cart fragment but targets #cart-content with innerHTML swap, causing nested rendering. Fragment should either: (a) return only the inner content without wrapper div, or (b) use outerHTML swap strategy. Affects cart.html line 52 and cartBodyFragment.html structure.
- [ ] TECH-BUG-2: Logout session termination incomplete — After logging out, clicking the browser's "Back" button successfully loads the authenticated account overview page. A subsequent refresh correctly forces a login screen. Indicates a critical caching or state mismatch issue, potentially architectural.

## Tasks
*(None scheduled currently)*
