## Why

The Posters Demo Store currently has no administrative interface — the legacy backoffice was removed in a recent cleanup. Store operators have no way to manage data without directly editing the database or CSV import files. A modern, professionally styled backoffice application is needed, and we are building it incrementally.

This first change establishes the **foundation**: the backoffice frame (layout shell, navigation, HTMX wiring) and admin user authentication (login, logout). No data management screens yet — those will be added step by step in follow-up changes.

The backoffice must be **strictly separated** from the customer-facing storefront: a dedicated URL prefix (`/backoffice`), its own authentication system (independent admin accounts, not storefront customer accounts), and its own UI layout.

## What Changes

- **Spring Security integration** — new dependency; two security filter chains: one permitting all storefront traffic, one requiring admin authentication for `/backoffice/**`
- **Admin user system** — new `admin_users` table with BCrypt-hashed passwords; a seeded default admin account; login and logout pages
- **Backoffice layout shell** — fixed dark sidebar, top navbar with session info, HTMX-powered content area; completely independent from the storefront layout (`templates/layout/default.html`)
- **HTMX integration** — HTMX JS library added to backoffice layout for future partial page updates
- **Bootstrap 5 professional styling** — dark sidebar, clean typography, responsive design following modern SaaS patterns
- **Placeholder dashboard** — simple landing page showing "Welcome to the Backoffice" with placeholder cards for future modules (Products, Orders, Customers, etc.)

## Capabilities

### New Capabilities
- `backoffice-users`: Admin user management and authentication — login page, logout, session handling, Spring Security filter chain for `/backoffice/**`, `admin_users` entity, seeded default admin
- `backoffice-layout`: Shared backoffice layout shell — sidebar navigation, top bar, HTMX container setup, Bootstrap 5 design system, placeholder dashboard landing page

### Modified Capabilities
_None — this is an entirely new subsystem with no changes to existing storefront specs._

## Impact

- **Dependencies**: `spring-boot-starter-security` added to `pom.xml`; HTMX JS library added to static assets
- **Configuration**: Two security filter chains — one permitting all storefront traffic (`/**`), one requiring admin auth for `/backoffice/**`
- **Database**: New `admin_users` table; initial admin seed data — username `admin` with password `admin-2026!` (BCrypt-hashed)
- **Existing code**: `WebConfig` interceptor patterns may need adjustment to exclude `/backoffice/**`; no changes to existing entities
- **Templates**: New template directory (`templates/backoffice/`) with its own layout, fully independent from the storefront
- **Static assets**: New SCSS/CSS for backoffice theme; HTMX library
