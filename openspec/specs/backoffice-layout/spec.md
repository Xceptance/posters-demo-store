# backoffice-layout

## Purpose

Defines the backoffice UI shell — a professional, dark-themed layout built with Bootstrap 5 and HTMX. The backoffice layout is fully independent from the storefront (no shared templates, CSS, or JS). Includes sidebar navigation, top bar, dashboard placeholder, and standalone login page.

## Requirements

### Requirement: Backoffice SHALL Have Its Own Independent Layout

The backoffice SHALL use a dedicated Thymeleaf layout at `templates/backoffice/layout/default.html`. This layout MUST NOT reuse any storefront templates, fragments, CSS, or JavaScript. The backoffice layout SHALL include a fixed dark sidebar, a top navigation bar, and a main content area.

#### Scenario: Backoffice page renders with backoffice layout

- **WHEN** an authenticated admin navigates to `/backoffice/`
- **THEN** the page renders using the backoffice layout
- **AND** the page contains a dark sidebar and a top navigation bar
- **AND** no storefront layout elements are present (no storefront header, footer, or navigation)

#### Scenario: Storefront layout is unaffected

- **WHEN** a user navigates to any storefront page
- **THEN** the storefront layout renders as before
- **AND** no backoffice layout elements are present

### Requirement: Backoffice Sidebar SHALL Display Navigation Items

The sidebar SHALL display navigation links for future backoffice modules. In this initial version, only the Dashboard link SHALL be active. All other items (Products, Categories, Customers, Orders, Settings) SHALL be rendered as disabled/greyed placeholders.

#### Scenario: Dashboard link is active

- **WHEN** an authenticated admin views the backoffice
- **THEN** the sidebar shows a "Dashboard" navigation item
- **AND** it is visually highlighted as the current/active page

#### Scenario: Placeholder items are disabled

- **WHEN** an authenticated admin views the sidebar
- **THEN** navigation items for Products, Categories, Customers, Orders, and Settings are visible
- **AND** they are visually greyed out or disabled
- **AND** clicking them does not navigate away

### Requirement: Backoffice Top Bar SHALL Show Admin Info and Logout

The top navigation bar SHALL display the authenticated admin's display name and a logout button.

#### Scenario: Admin display name shown

- **WHEN** an authenticated admin views the backoffice
- **THEN** the top bar displays the admin's display name (e.g., "Administrator")

#### Scenario: Logout button present and functional

- **WHEN** an authenticated admin clicks the logout button in the top bar
- **THEN** a POST request to `/backoffice/logout` is triggered
- **AND** the admin is logged out and redirected to the login page

### Requirement: Backoffice Dashboard SHALL Show Placeholder Content

The dashboard at `/backoffice/` SHALL display a welcome message and placeholder cards indicating future modules.

#### Scenario: Dashboard welcome message

- **WHEN** an authenticated admin navigates to `/backoffice/`
- **THEN** the page displays a welcome heading (e.g., "Welcome to the Backoffice")

#### Scenario: Placeholder module cards

- **WHEN** an authenticated admin views the dashboard
- **THEN** placeholder cards are shown for future modules (Products, Orders, Customers, etc.)
- **AND** each card indicates the module is "Coming Soon" or similar

### Requirement: Backoffice Login Page SHALL Have Its Own Standalone Design

The login page at `/backoffice/login` SHALL NOT use the backoffice sidebar layout. It SHALL render as a standalone centered form with professional styling, consistent with the backoffice design system.

#### Scenario: Login page has no sidebar

- **WHEN** a user navigates to `/backoffice/login`
- **THEN** the page renders without the backoffice sidebar or top bar
- **AND** a centered login form is displayed

### Requirement: Backoffice Layout SHALL Include HTMX

The backoffice layout SHALL include the HTMX 2.x JavaScript library from a local static file (`/assets/lib/htmx.min.js`). The library SHALL be available for use by future backoffice screens.

#### Scenario: HTMX library loaded

- **WHEN** any backoffice page loads (after login)
- **THEN** the HTMX JavaScript library is included in the page
- **AND** it is served from a local static file (not a CDN)
