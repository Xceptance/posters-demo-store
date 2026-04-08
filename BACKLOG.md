# Backlog

## EPIC

- [ ] EPIC-1: Backoffice
- [ ] EPIC-2: Coupons
- [ ] EPIC-3: Testing backoffice for manipulation of behavior
- [ ] EPIC-4: Inventory

## Feature

- [x] FEAT-1: Footer checkout
- [ ] FEAT-2: Hero header image category
- [ ] FEAT-3: Steps Checkout
- [ ] FEAT-4: Sorting
- [ ] FEAT-5: Refinements for search
- [ ] FEAT-6: Markdown styling of product and other texts
- [ ] FEAT-7: Template concepts and standardized API for using of templates
- [ ] FEAT-8: Themes for templates
- [ ] FEAT-9: Autoimage scaling via url service includes image transformation

## Improvement

- [ ] IMPR-1: Localization of messages such as "this is a demo store", also the homepage message
- [ ] IMPR-2: Localization of ad banner
- [ ] IMPR-3: Styling pagination including colors
- [ ] IMPR-4: Show rest count when refining
- [ ] IMPR-5: Double password when setting account
- [ ] IMPR-6: Cart should not close when mouse hovers over it and stay open longer
- [ ] IMPR-7: Close X for enlarged images needs contrast
- [ ] IMPR-8: Add "progress" indicator for the storefront while the server starts
- [ ] IMPR-9: Integrate jOOQ for type-safe query building — replace ~13 raw JPQL strings (em.createQuery) across WebShopController, CatalogController, SearchController, CartController, CartService, LocalizedTextService, and CatalogDataLoader with jOOQ fluent API. Use JPA+jOOQ hybrid: keep Spring Data repos for CRUD, use jOOQ for complex reads. Requires spring-boot-starter-jooq + codegen Maven plugin.

## Task

- [ ] TASK-1: Formatting: Japanese address checkout routing (Prefecture → City → Block)
- [ ] TASK-2: Logic: Yen-specific UI formatting and backend tax calculators
- [ ] TASK-3: Layout: Support for Right-To-Left / Vertical text directions 
- [ ] TASK-4: Translation: Full `ja-JP` translation of Backoffice admin UI
## Defect

- [x] DEFECT-1: Scroll up button does not work
- [ ] DEFECT-2: Design login screen
- [ ] DEFECT-3: Design create an account
- [x] DEFECT-4: No price updates for change of size defect
- [ ] DEFECT-5: clearing a quantity field on the cart, breaks the cart page completely 
- [ ] DEFECT-6: Account overview broken with template parsing error (`customer/accountOverview.html`)
- [ ] DEFECT-7: Catalog import data only has one finish per product
