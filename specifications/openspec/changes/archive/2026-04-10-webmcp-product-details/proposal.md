# Proposal: WebMCP Product Variant Exploration Tool

## Purpose
Currently, AI agents browsing the catalog through `search_catalog` only receive the top-level `SearchProductDto` which contains basic properties and minimal prices. They lack any contextual endpoints allowing them to dynamically query a product's available permutations (sizes, finishes), forcing them to blindly guess payload variables when triggering `add_to_cart`.

## Scope
1. Expose a secure, headless REST endpoint in `CatalogController` to extract granular product variants and metadata recursively wrapped inside `ProductDetailDto`.
2. Register a dedicated API tool named `get_product_details` natively alongside `search_catalog` enforcing strict JSON boundaries for MCP bots.

## Expected WebMCP Output Example (Grizzly Bear, ID 1)
When the AI executes `get_product_details` for `productId: 1`, it will receive the exact configuration maps:
```json
{
  "id": 1,
  "name": "Grizzly Bear",
  "descriptionOverview": "Bear looking tired.",
  "minimumPrice": 17.00,
  "availableFinishes": ["matte", "gloss"],
  "distinctSizes": [
    {"value": "16 x 12", "label": "16 x 12 in"},
    {"value": "32 x 24", "label": "32 x 24 in"}
  ],
  "variants": [
    {"id": 101, "sku": "GB-01", "size": "16 x 12 in", "finish": "matte", "price": 17.00},
    {"id": 102, "sku": "GB-02", "size": "32 x 24 in", "finish": "gloss", "price": 28.00}
  ]
}
```

## Out of Scope
- Making changes to the internal database constraints or the structural relationship mapping defining how finishes/sizes are linked.
