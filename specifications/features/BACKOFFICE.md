# Backoffice Features

The backoffice is a Spring Boot + Thymeleaf admin panel located at `/backoffice/`. It uses Bootstrap 5, HTMX, and Material Symbols for the UI.

## Authentication & Authorization

- **Login screen** with password reveal toggle
- **Spring Security** integration with `AdminUser` entity and BCrypt password encoding
- **Role-based module access** — each role grants access to specific top-level modules
- **Startup gate** — shows a "Starting up..." page with spinner until database seeding is complete, then auto-redirects to login

## Layout & Navigation

- **Fixed sidebar** with collapsible module tree, role-based visibility
- **Module registry** (`BackofficeModule` enum) defining all navigable modules with icons, URLs, ordering, and parent/child hierarchy
- **Top-level modules**: Dashboard, Security, Catalog, Customers, Orders
- **Security submodules**: Users, Roles, Audit Log, Import/Export, Settings
- **Catalog submodules**: Dashboard, Categories, Products, Variations & Attributes, Pricing, Import/Export, Settings
- **Customers submodules**: Dashboard, Customers, Import/Export, Settings
- **Orders submodules**: Dashboard, Orders, Export
- **Placeholder pages** for unimplemented modules

## Dashboard

- Landing page after login

## Security > User Management

- **User listing** with table view (Username, Display Name, Email, Role, Created)
- **HTMX inline search** with 300ms debounce on keyup, no page reload
- **Role filter dropdown** (HTMX, triggers on change)
- **Inline pagination** via HTMX
- **Create user form** — username, display name, email (required), password with confirmation and show/hide toggle
- **Edit user form** — same fields, password reset as a separate section
- **Single role per user** — radio button selection
- **Delete user** — Bootstrap confirmation modal, self-deletion prevention, last-admin protection
- **Clickable usernames** — link to edit form

## Security > Role Management

- **Role listing** with table view (Name, Description, Modules, Users, Actions)
- **Module badges** — display which modules each role grants access to
- **User count** — shows how many users are assigned to each role
- **Create custom role** — name (required, unique), description (optional), module checkboxes (top-level only)
- **Edit custom role** — same form, clickable role name links to edit
- **Delete custom role** — Bootstrap confirmation modal with user-assignment guard
- **Built-in role protection** — no edit/delete buttons shown, direct URL access rejected
- **Form data preservation** — on validation errors, form re-renders with entered data
- **Default built-in roles**: System Admin, Business Admin, Catalog User, Order User

## Security > Audit Log

- **Audit log listing** — tracks all user management operations (create, update, delete, password reset)
- **Table view** with timestamp, actor, action, target, and details

## Security > Settings

- Placeholder/basic settings page

## Styling

- **Custom CSS** (`backoffice.css`) with design tokens (colors, radii, shadows, transitions)
- **Card-based layout** (`bo-module-card`) for content sections
- **Table styling** — light header background, uppercase labels, lighter data font, no hover effects
- **Local assets** — fonts and Bootstrap served locally (no CDN)
