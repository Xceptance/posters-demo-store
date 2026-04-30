## 1. Dependencies & Build Setup

- [x] 1.1 Add `spring-boot-starter-security` dependency to `pom.xml`
- [x] 1.2 Download HTMX 2.x minified JS and place at `src/main/resources/static/lib/htmx.min.js`
- [x] 1.3 Verify project compiles with new dependency

## 2. AdminUser Entity & Repository

- [x] 2.1 Create `AdminUser` JPA entity (`admin_users` table) with fields: `id` (Long), `username` (unique), `password` (BCrypt), `displayName`, `createdAt`
- [x] 2.2 Create `AdminUserRepository` (Spring Data JPA)
- [x] 2.3 Create `AdminDataLoader` component to seed default admin (`admin` / `admin-2026!`) on startup if table is empty

## 3. Spring Security Configuration

- [x] 3.1 Create `SecurityConfig` class with two `@Order`-ed `SecurityFilterChain` beans
- [x] 3.2 Backoffice chain (`@Order(1)`): require auth for `/backoffice/**`, form login at `/backoffice/login`, logout at `/backoffice/logout`
- [x] 3.3 Storefront chain (`@Order(2)`): `permitAll()` for `/**`, CSRF disabled
- [x] 3.4 Create `AdminUserDetailsService` implementing `UserDetailsService` for backoffice auth
- [x] 3.5 Update `WebConfig` to exclude `/backoffice/**` from `CommonDataInterceptor`

## 4. Backoffice Controller

- [x] 4.1 Create `BackofficeController` with mappings for `/backoffice/` (dashboard) and `/backoffice/login`
- [x] 4.2 Pass admin display name to dashboard model via `Principal` / `@AuthenticationPrincipal`

## 5. Backoffice Templates & Styling

- [x] 5.1 Create `templates/backoffice/layout/default.html` — dark sidebar, top bar, content fragment, HTMX include
- [x] 5.2 Create `templates/backoffice/login.html` — standalone centered login form
- [x] 5.3 Create `templates/backoffice/dashboard.html` — welcome message and placeholder module cards
- [x] 5.4 Create backoffice CSS file with design tokens (sidebar `#1e1e2f`, accent `#6366f1`, etc.)
- [x] 5.5 Add sidebar navigation items: Dashboard (active), Products/Categories/Customers/Orders/Settings (disabled)
- [x] 5.6 Add top bar with admin display name and logout button (POST form)

## 6. Verification

- [x] 6.1 Verify application starts and default admin is seeded
- [x] 6.2 Verify storefront pages load without login prompts or CSRF issues
- [x] 6.3 Verify `/backoffice/` redirects to `/backoffice/login` when unauthenticated
- [x] 6.4 Verify login with `admin` / `admin-2026!` succeeds and redirects to dashboard
- [x] 6.5 Verify login with wrong credentials shows error
- [x] 6.6 Verify logout works and session is invalidated
- [x] 6.7 Verify backoffice layout renders independently (no storefront elements)
