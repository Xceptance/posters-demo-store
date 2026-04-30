## Context

The Posters Demo Store is a Spring Boot 3.x application using Thymeleaf, JPA/Hibernate (H2, `ddl-auto: update`), and Bootstrap 5. The storefront has its own layout (`templates/layout/default.html`) and currently has no authentication or authorization layer — Spring Security is not a dependency. The legacy backoffice was previously removed (see `backoffice-cleanup` spec). Data is seeded at startup by `CatalogDataLoader` via the `EntityManager`.

This design covers the foundation only: admin user authentication (login/logout) and the backoffice layout shell with a placeholder dashboard. No CRUD management screens yet.

## Goals / Non-Goals

**Goals:**
- Introduce Spring Security with two isolated filter chains (storefront: permit all, backoffice: authenticated)
- Create an `AdminUser` entity with its own table, independent from `CatalogCustomer`
- Seed a default admin account (`admin` / `admin-2026!`) at startup
- Build a professional dark-sidebar backoffice layout shell using Bootstrap 5 + HTMX
- Serve the backoffice under `/backoffice/**` with its own Thymeleaf templates
- Show a placeholder dashboard after login

**Non-Goals:**
- No CRUD screens for products, categories, customers, orders, etc. (future changes)
- No role-based access control — single admin role for now
- No REST API for the backoffice — all server-rendered with HTMX partials
- No password reset or admin self-registration
- No changes to storefront behavior or appearance

## Decisions

### 1. Spring Security — Dual Filter Chain

**Decision:** Use two `@Order`-ed `SecurityFilterChain` beans in a single `SecurityConfig` class.

**Chain 1 — Backoffice** (`@Order(1)`, matches `/backoffice/**`):
- Form-based login at `/backoffice/login`
- Logout at `/backoffice/logout` → redirect to `/backoffice/login?logout`
- All `/backoffice/**` URLs require authentication
- Login page itself is permitted

**Chain 2 — Storefront** (`@Order(2)`, matches `/**`):
- `permitAll()` on everything — no authentication for storefront
- CSRF disabled for storefront to avoid breaking existing forms
- Completely transparent to current storefront behavior

**Why dual chains:** Keeps storefront completely unaffected. A single chain with mixed matchers would be fragile and risk breaking existing storefront functionality.

**Alternative considered:** Separate Spring Boot applications or profiles. Rejected — too much overhead for a demo store; same JVM is simpler and sufficient.

### 2. AdminUser Entity — Separate from CatalogCustomer

**Decision:** New `AdminUser` JPA entity mapping to `admin_users` table. Fields: `id` (Long, auto-increment), `username` (unique, not null), `password` (BCrypt hash), `displayName`, `createdAt`.

**Why separate:** The proposal explicitly requires strict separation. Admin accounts must never be usable in the storefront and vice versa. A shared user table with a role flag would create coupling.

**UserDetailsService:** A custom `AdminUserDetailsService` implements `UserDetailsService`, scoped only to the backoffice filter chain. It loads from `AdminUserRepository` (Spring Data JPA).

### 3. Admin Seeding via CatalogDataLoader

**Decision:** Add admin seeding to the existing `CatalogDataLoader` (or a new `AdminDataLoader` `@Component` with `@PostConstruct`). Seed user: username `admin`, password `admin-2026!` (BCrypt-hashed), displayName `Administrator`.

**Why alongside catalog data:** Keeps the pattern consistent. The admin user is only seeded if the table is empty (idempotent).

### 4. Template & URL Structure

**Decision:**

| URL | Purpose |
|-----|---------|
| `/backoffice/login` | Login page (public) |
| `/backoffice/logout` | Logout action (POST) |
| `/backoffice/` | Dashboard (authenticated) |

**Template directory:** `templates/backoffice/` — a fully self-contained subtree with:
- `layout/default.html` — Thymeleaf layout (dark sidebar, top bar, content fragment)
- `login.html` — standalone login page (no sidebar)
- `dashboard.html` — placeholder welcome page

**Strict isolation:** The backoffice templates MUST NOT reuse any storefront pieces — no shared Thymeleaf fragments, no shared CSS files, no shared JavaScript. The backoffice has its own layout decorators, its own SCSS/CSS, and its own JS. This ensures the two applications can evolve independently without cross-contamination. Bootstrap 5 and HTMX are the only shared *libraries* (included separately in the backoffice layout `<head>`).

### 5. HTMX Integration

**Decision:** Include HTMX 2.x via a static JS file under `/assets/lib/htmx.min.js`. The backoffice layout includes it in the `<head>`. For this initial change, HTMX is wired but not actively used — the dashboard is a full page. Future CRUD screens will use `hx-get`, `hx-post`, `hx-target`, etc. for partial updates.

**Why static file (not CDN):** Demo store should work offline / in air-gapped environments.

### 6. Backoffice Layout Design

**Decision:** Dark fixed sidebar (240px) with navigation links, top bar with admin display name + logout button, and a white/light content area. Based on the SaaS sidebar patterns already established in the project's knowledge base.

**Design tokens:**
- Sidebar: `#1e1e2f` (deep indigo-charcoal)
- Sidebar hover: `#2a2a40`
- Top bar: white with subtle bottom border
- Content area: `#f5f6fa` (soft off-white)
- Accent: `#6366f1` (indigo, matches existing patterns)

**Sidebar navigation items** (placeholders for future screens):
- Dashboard (active in this change)
- Products (disabled/greyed)
- Categories (disabled/greyed)
- Customers (disabled/greyed)
- Orders (disabled/greyed)
- Settings (disabled/greyed)

### 7. WebConfig Adjustment

**Decision:** Exclude `/backoffice/**` from the `CommonDataInterceptor` path patterns. The interceptor attaches storefront-specific model data (cart, locale, etc.) that is irrelevant to the backoffice and could cause errors if backoffice templates don't expect it.

Updated pattern:
```java
registry.addInterceptor(commonDataInterceptor)
    .addPathPatterns("/**")
    .excludePathPatterns("/assets/**", "/h2-console/**", "/api/**", "/backoffice/**");
```

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| Adding Spring Security could break existing storefront forms (CSRF) | Storefront filter chain explicitly disables CSRF and permits all |
| `CommonDataInterceptor` might still fire for backoffice | Explicit exclusion pattern in `WebConfig` |
| HTMX library adds ~14KB to static assets | Acceptable for a demo store; enables future SPA-like UX |
| Default admin credentials are hardcoded in seed data | This is a demo store — documented in README; not a production concern |
| `ddl-auto: update` may create table differently than expected | `AdminUser` entity is simple (no complex relationships); verify on startup |
