## 1. DTO Construction

- [x] 1.1 Create `OrderDto` mapping all required summary properties recursively inside `com.xceptance.posters.dto`. Must include Javadoc.
- [x] 1.2 Create `OrderItemDto` modeling individual layout primitives matching `CartItemDto`. Implement manual `get` accessors for `imageUrl` targeting Thymeleaf binding errors. Include Javadoc.

## 2. Service Logic Modifications

- [x] 2.1 Refactor `CartToOrderConverter.convert` to natively resolve the runtime generated image paths into `OrderLineItem`'s dormant schema columns.
- [x] 2.2 Implement `CheckoutService.toOrderDto` utility method isolating mapping capabilities away from the primary controller.

## 3. Presentation View Updates

- [x] 3.1 Overhaul `orderConfirmation.html` mapping logic replacing implicit entities with explicit `OrderDto` properties mirroring `/cart`.
- [x] 3.2 Update `CheckoutController` to consume the new `toOrderDto` functionality, removing leaked dependencies. Add `final` enforcement aggressively.

## 4. Test Integration

- [x] 4.1 Create `CartToOrderConverterTest` verifying `imageUrl` database injections operate successfully via JUnit.
- [x] 4.2 Create `CheckoutServiceTest` validating `OrderDto` structural accuracy.
- [x] 4.3 Update `CheckoutControllerUiTest` injecting strict `<img>` evaluation onto the mock intercept layer layout logic.
