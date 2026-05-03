## Context

The Posters Demo Store currently uses "Buy Here" as the call-to-action text for links on product tiles that navigate to the Product Detail Page (PDP). This text is managed via the localization key `buttonBuyHere` in the Spring Boot message bundles.

## Goals / Non-Goals

**Goals:**
- Update the CTA text to "Shop Now" to align with modern e-commerce best practices.
- Ensure consistent localization across all supported languages (EN, DE, SV, JA).
- Improve the professional feel and conversion potential of the product tiles.

**Non-Goals:**
- Changing the link target or navigation logic.
- Modifying the visual design (CSS) of the buttons.
- Renaming the localization key itself (to avoid unnecessary template changes).

## Decisions

- **Rename the key to `buttonShopNow`**: While "Buy Here" is no longer the text, renaming the key to `buttonShopNow` provides better context and aligns the key name with the specific "Shop Now" call-to-action.
- **Update all templates**: All occurrences of `#{buttonBuyHere}` in Thymeleaf templates will be replaced with `#{buttonShopNow}` to ensure the application continues to function correctly with the new key.
- **Update all translations**: The change must be reflected in all locales to ensure a consistent user experience globally.

## Risks / Trade-offs

- **Test Failures**: Automated functional tests (e.g., using Neodymium/Selenium) might be asserting the presence of the text "Buy Here". These tests will need to be identified and updated.
- **Translation Accuracy**: Ensuring "Shop Now" is correctly translated to capture the same "action-oriented" tone in German, Swedish, and Japanese.
