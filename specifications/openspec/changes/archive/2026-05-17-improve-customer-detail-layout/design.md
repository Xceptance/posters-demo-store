## Context

The current backoffice customer detail layout (`implementation/src/main/resources/templates/backoffice/customers/detail.html`) uses a grid layout with cards for "Profile Summary", "Addresses", and "Credit Cards". While functional, it lacks the rich aesthetics and consistent UI patterns expected in modern web applications. We recently established a new design benchmark in the dashboard layout (`customers/dashboard.html`) which utilizes a cleaner card structure, better typography, and a unified toolbar approach inspired by Star Admin 2. The goal is to update the customer detail view to match this premium standard.

## Goals / Non-Goals

**Goals:**
- Update `detail.html` to reflect modern, premium Bootstrap aesthetics.
- Implement a cleaner card-based layout for Profile, Addresses, and Credit Cards.
- Standardize the toolbar and action buttons.
- Enhance the visual hierarchy using typography and spacing.
- Retain all existing HTMX functionality (inline editing, form swapping).

**Non-Goals:**
- Modifying backend controller logic.
- Adding new customer-related features (e.g., order history on this specific page).
- Completely rewriting the CSS framework (we will stick to existing utility classes and variables defined in `backoffice.css`).

## Decisions

1.  **Layout Structure:** We will maintain the general grid structure (Profile on the left/top, Addresses/Credit cards on the right/bottom) but will enhance the `bo-card` classes with better padding, borders, and shadows defined in our design system.
2.  **Action Consolidation:** For individual items within lists (addresses, credit cards), we will use the `dropdown` pattern for secondary actions (like delete) to keep the UI clean, while keeping primary actions (like edit) accessible, similar to the pattern implemented in the list view.
3.  **Visual Polish:**
    -   Use `text-muted` and `fw-bold` classes effectively to create clear visual hierarchies.
    -   Introduce subtle hover states on cards or list items to make the interface feel more dynamic.
    -   Ensure consistent icon usage (Material Symbols) across all actions.
    -   Move the "Back to List" button to a more standardized breadcrumb/toolbar location.

## Risks / Trade-offs

-   [Risk] HTMX target IDs change during refactoring, breaking inline editing. → **Mitigation:** Carefully preserve all `id` attributes and `hx-target` / `hx-swap` configurations when restructuring the HTML.
-   [Risk] Inconsistent spacing when fragments are injected. → **Mitigation:** Ensure that fragments (e.g., `address-list.html`, `profile-name-form.html`) are self-contained regarding their internal padding and margins, and rely on their container (`bo-card-body`) for external spacing.
