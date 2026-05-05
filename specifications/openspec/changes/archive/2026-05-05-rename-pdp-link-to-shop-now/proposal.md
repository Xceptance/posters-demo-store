## Why

The current link text "Buy Here" on product tiles is considered suboptimal for user experience. It feels overly transactional and direct. "Shop Now" is a more standard, action-oriented call-to-action (CTA) that better fits the premium feel of a poster store and clearly indicates the next step in the shopping funnel.

## What Changes

The link text from product tiles (and potentially other locations using the same key) to the product detail page will be changed from "Buy Here" to "Shop Now". Additionally, the localization key will be renamed from `buttonBuyHere` to `buttonShopNow` to be more descriptive and maintainable.

Localized values for `buttonShopNow`:
- English: "Shop Now"
- German: "Jetzt shoppen"
- Swedish: "Handla nu"
- Japanese: "今すぐ購入"

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- storefront: Update the call-to-action text for product navigation to improve UX.

## Impact

This change affects:
- `messages.properties` and its localized versions (key rename and value update).
- All templates that use the `buttonBuyHere` localization key (update to `linkProductDetail`).
- Any active automated tests and manual test specifications that assert the text "Buy Here" (excluding historical records).
