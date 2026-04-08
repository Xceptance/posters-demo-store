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

## Tasks
*(None scheduled currently)*
