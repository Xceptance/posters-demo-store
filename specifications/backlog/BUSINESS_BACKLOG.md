# Business Backlog

> [!IMPORTANT]
> This document tracks all business-focused Epics, Features, Improvements, and Tasks.
> **Numbering Policy**: Never reuse ID numbers. When adding new items, always count upwards from the highest existing number, even if earlier items are deleted or moved.

## Epics
- [ ] BUS-EPIC-1: Backoffice
- [ ] BUS-EPIC-2: Coupons
- [ ] BUS-EPIC-3: Inventory

## Features
- [x] BUS-FEAT-1: Footer checkout
- [ ] BUS-FEAT-2: Hero header image category
- [ ] BUS-FEAT-3: Steps Checkout
- [ ] BUS-FEAT-4: Sorting
- [ ] BUS-FEAT-5: Refinements for search — Currently, all user input is sanitized via `MultiFieldQueryParser.escape()` before Lucene parsing, which neutralizes query syntax characters (`-`, `"`, `+`, `:`, etc.). This means advanced search features like negation (`-term`), exact phrase matching (`"exact phrase"`), and boolean operators are **not available** to end users. Consider selectively allowing safe Lucene syntax (e.g., quoted phrases for exact matching, `-` for exclusion) while still protecting against injection.
- [ ] BUS-FEAT-6: Markdown styling of product and other texts
- [ ] BUS-FEAT-7: SKU search — Product SKU codes (e.g., `P001-GRIBEA-V1M`) are not included in the Lucene search index. Adding SKU as a searchable field would enable support/backoffice lookup scenarios directly from the storefront search bar.

## Improvements
- [ ] BUS-IMPR-1: Localization of messages such as "this is a demo store", also the homepage message
- [ ] BUS-IMPR-2: Localization of ad banner
- [ ] BUS-IMPR-3: Styling pagination including colors
- [ ] BUS-IMPR-4: Show rest count when refining
- [ ] BUS-IMPR-5: Double password when setting account
- [ ] BUS-IMPR-6: Cart should not close when mouse hovers over it and stay open longer
- [ ] BUS-IMPR-7: Close X for enlarged images needs contrast
- [ ] BUS-IMPR-8: Add "progress" indicator for the storefront while the server starts
- [ ] BUS-IMPR-9: Search results pagination — The search results page renders all matches up to a hard limit of 100 with no pagination. Category browsing pages already have pagination. Consider adding consistent pagination to search results.
- [ ] BUS-IMPR-10: Price search — Product prices are not included in the Lucene index (only name, short description, and long description are indexed). Searching for a price value (e.g., `$17.00` or `17`) only matches if that number appears in a product description. Consider adding price-range filtering or a price field to the search index.

## Tasks
- [ ] BUS-TASK-1: Formatting: Japanese address checkout routing (Prefecture → City → Block)
- [ ] BUS-TASK-2: Logic: Yen-specific UI formatting and backend tax calculators
- [ ] BUS-TASK-3: Layout: Support for Right-To-Left / Vertical text directions 
- [ ] BUS-TASK-4: Translation: Full `ja-JP` translation of Backoffice admin UI
