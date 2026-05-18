## Why

The current backoffice customer detail layout needs an update to follow a nice, modern Bootstrap design, specifically taking inspiration from modern dynamic web design and premium aesthetics (e.g. vibrant colors, glassmorphism, smooth animations) as outlined in the project's web application development rules. Furthermore, the dashboard page provides a visual benchmark (e.g. Star Admin 2 theme) which points toward a clean, card-based UI. Standardizing the layout ensures a consistent, high-quality user experience across the admin interface.

## What Changes

- Redesign the customer detail page (`detail.html`) to use a modern, premium Bootstrap layout.
- Apply rich design aesthetics (curated colors, modern typography, subtle gradients, and micro-animations on hover).
- Ensure components (Profile Summary, Addresses, Credit Cards) utilize the standard design system (e.g., card layouts, clean toolbars, and unified action menus).
- Consolidate actions to prevent UI clutter.

## Capabilities

### New Capabilities
- None. This is a purely visual UI/UX layout update to existing features.

### Modified Capabilities
- `backoffice-customers`: The visual layout of the customer detail view is changing to adhere to the new premium design standards. The underlying capabilities (viewing profile, addresses, credit cards) remain the same.

## Impact

- `implementation/src/main/resources/templates/backoffice/customers/detail.html`
- Various fragments in `implementation/src/main/resources/templates/backoffice/customers/fragments/` may need adjustment to fit the new card-based structure.
- `implementation/src/main/resources/static/css/backoffice.css` (potentially adding new design tokens or utility classes to support the updated layout).
