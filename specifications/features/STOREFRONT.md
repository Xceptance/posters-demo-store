# Storefront Features

The storefront is a Spring Boot + Thymeleaf e-commerce demo store. It uses Bootstrap for the UI with localized content (English, German, Swedish, Japanese).

## Catalog

- **Homepage** with featured products and category overview
- **Category overview** with product grid and filter sidebar
- **Product detail pages** with images, descriptions, and pricing
- **Search** — full-text product search with per-language stemming (EN, DE, SV, JA), prefix matching, and instant suggestions (search-as-you-type)
- **Product grid fragment** for AJAX-style filtering

## Shopping Cart

- **Add to cart** from product pages
- **Cart page** with line items, quantity controls, and totals
- **Mini cart** (dropdown/popover fragment in header)
- **Cart body fragment** for dynamic updates

## Checkout Flow

- **Multi-step checkout process**:
  1. **Billing Address** — form with country dropdown (localized country names)
  2. **Shipping Address** — separate or same-as-billing
  3. **Payment** — premium credit card form with:
     - Real-time card vendor detection (Visa, MasterCard, Amex)
     - Auto-formatting with visual spacing
     - Combined MM/YY expiry input
     - CVV field with vendor-specific length validation
     - Luhn algorithm validation
     - Show/hide password-style toggle
     - PCI-compliant masking on re-display
  4. **Place Order** — order review and confirmation
  5. **Order Confirmation** — success page with order summary

## Customer Accounts

- **Registration** — new account creation
- **Login** — email/password authentication
- **Account overview** — customer dashboard
- **Order history** — past order overview

## Localization

- **Multi-language support** — English, German, Swedish, Japanese
- **Localized country names** in checkout dropdowns (rendered in selected locale)
- **Localized disclaimer banner** using message keys

## Layout & Design

- **Responsive layout** with Bootstrap
- **Default layout** for storefront pages
- **Checkout layout** — streamlined for the checkout flow
- **Error page** — custom error template
- **Price fragment** — reusable price display with currency formatting
