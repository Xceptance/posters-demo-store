# WebMCP Product Variant Extension

## Feature Capabilities
In addition to basic catalog array retrieval (`search_catalog`), autonomous agents must actively resolve specific physical item geometries and variant configurations mapping through `get_product_details` before finalizing carts.

## Abstraction Rules
- The API explicitly returns hierarchical mappings segregating total unique dimensions (`distinctSizes`), aesthetic characteristics (`availableFinishes`), and precise SKU variants correlating against pricing structures (`variants`).
- Output enforces W3C `modelContext` boundaries ensuring agents natively interpret multi-factor product customizations programmatically without inferring layout semantics.
