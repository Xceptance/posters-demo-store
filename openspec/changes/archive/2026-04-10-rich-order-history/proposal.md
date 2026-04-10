# Proposal: Rich Order History Presentation

## Purpose
The customer "My Account" area currently renders a deeply simplified order history table that lacks critical layout attributes (product thumbnails, variant names, specific sizes). Leveraging the newly built immutable `OrderDto` mapping payload, we can safely project richer checkout snapshots directly into the order history overview without risking any template-engine database relationship queries.

## Scope
- Integrate `CheckoutService` into `CustomerController` mapping flows.
- Map the backend `CatalogOrder` native loops out to frontend-friendly `List<OrderDto>` loops.
- Overhaul `customer/orderOverview.html` flat tables into flexbox-based elements dynamically bound to the injected properties mimicking `orderConfirmation.html`.

## Out of Scope
- Modifying backend table schema (the snapshot capabilities were constructed and locked within the prior `rich-order-confirmation` iteration).
- Modifying administrative dashboard order render flows.
