# Tasks: Rich Order History Presentation

## 1. Controller Mapping
- [x] 1.1 Dependency inject `CheckoutService` globally inside `CustomerController.java` ensuring all `final` configuration configurations execute securely.
- [x] 1.2 Overhaul `orderOverview()` controller logic to intercept standard `List<CatalogOrder>`, streaming the array iteratively into a strict mapped `List<OrderDto> orderDtos` layout block.
- [x] 1.3 Add `model.addAttribute("orderDtos", orderDtos)` effectively transmitting layout mapping objects downstream to Thymeleaf.

## 2. Template Refactoring
- [x] 2.1 Refactor `/customer/orderOverview.html` template structures mapping arrays against the newly structured `OrderDto` format natively replacing legacy table outputs.
- [x] 2.2 Replicate identical DOM visual rendering attributes mirroring `/checkout/orderConfirmation.html` precisely so that the frontend layouts match exactly (Images + Variants).

## 3. Test Integration
- [x] 3.1 Create `CustomerControllerTest.java` (Unit Test) utilizing pure `@MockitoExtension` isolation to formally verify `model.getAttribute("orderDtos")` binds the mapped `CheckoutService` payload successfully.
- [x] 3.2 Update `CustomerControllerUiTest` (or isolated UI logic scopes) explicitly expecting layout assertions (tags, variants, prices) to populate across order history tables appropriately.
