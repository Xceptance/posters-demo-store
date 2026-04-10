# Design: Rich Order History Presentation

## Architecture Validation
We strictly isolate backend JPA constraints out of visual rendering nodes utilizing mapping architectures. Because `orderOverview.html` fundamentally tracks the exact identical requirements as checkout confirmations (product presentation via thumbnail rendering combined with textual price blocks), we will reuse `CheckoutService.toOrderDto` recursively inside the user's dashboard view.

## Core Modifications

### 1. CustomerController Native Routing
The `CustomerController.orderOverview()` method currently fetches `List<CatalogOrder>`.
1. Inject `CheckoutService checkoutService` via the controller constructor.
2. Intercept the standard entity list.
3. Recursively execute `.map(checkoutService::toOrderDto)` against the payload returning a formal `List<OrderDto>`.
4. Inject the parsed list natively into the Thymeleaf `Model` under the `orderDtos` attribute, retaining the raw `orders` object unmodified for backward compatibility logic mapping contexts (like `orderDate`).

### 2. orderOverview.html DOM Replacement
Substitute the existing semantic `<table class="table table-sm">` blocks iteratively mapped to `order.lineItems` with identical structural DOM layouts parsed from `orderConfirmation.html`.
- Utilize standard `<img th:src="@{${op.imageUrl}}">` binding to render active product visual layouts securely.
- Ensure textual nodes strictly bind to `op.productName`, `op.variantDescription`, and `op.totalPrice`.
