# Order History Presentation Spec

## Overview
The order history page in the customer account area must dynamically map and project rich visual metadata attached to historical orders ensuring they are presented gracefully mirroring the checkout layout standards.

## Functional Requirements
- **Order Details Mapping**: The MVC tier must stream historical database records (`CatalogOrder`) into front-end native abstraction loops (`OrderDto`) decoupling layout representations from table schemas.
- **Rich Visual Rendering**: Native layout representations must use flexbox layouts (not flat tables) containing product thumbnails, variant descriptions (e.g., sizes, materials), unit prices, and quantities.
- **Null Safety Fallbacks**: Products missing thumbnail references must default to `/images/placeholder.webp`. Products lacking explicit variant string maps must decay back into strict `sku` projections gracefully.
- **Financial Architecture**: Total payload value nodes (currency string outputs) must align strictly against standard Java `#numbers.formatCurrency()` directives retaining locale parity.
