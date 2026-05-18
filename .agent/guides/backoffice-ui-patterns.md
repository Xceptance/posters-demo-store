# Backoffice UI Patterns Guide

> [!IMPORTANT]
> This is the authoritative reference for AI agents implementing or updating any backoffice template.
> Read this before generating any Thymeleaf HTML for the backoffice. Do not deviate from these patterns.

---

## 1. Stack & Asset References

The backoffice is a **standalone** design system fully independent from storefront styles.

| Asset | Path in layout |
|-------|----------------|
| Custom CSS | `/assets/css/backoffice.css` |
| Custom Fonts | `/assets/css/backoffice-fonts.css` |
| Bootstrap 5 | `/assets/lib/bootstrap/bootstrap.min.css` + `bootstrap.bundle.min.js` |
| HTMX | `/assets/lib/htmx.min.js` |
| Icons | Google Material Symbols Outlined (loaded via `backoffice-fonts.css`) |
| Charts | Apache ECharts 5 (CDN, included per-page only where needed) |
| Font | `Inter` (loaded via `backoffice-fonts.css`) |

**Never** import storefront CSS or JS into a backoffice template.

---

## 2. Page Layout Contract

All backoffice content pages use the Thymeleaf Layout Dialect and decorate `backoffice/layout/default`.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{backoffice/layout/default}">
<head>
    <title>My Page Title</title>
</head>
<body>
    <!-- Required: sets the top-bar h1 -->
    <div layout:fragment="pageTitle">My Page Title</div>

    <!-- Optional: top-bar action buttons (e.g., a global "New" button) -->
    <div layout:fragment="pageActions"></div>

    <!-- Required: main page content -->
    <div layout:fragment="content">
        <!-- ... -->
    </div>

    <!-- Optional: page-specific JavaScript blocks -->
    <th:block layout:fragment="pageScripts">
        <script>/* ... */</script>
    </th:block>
</body>
</html>
```

**Rules:**
- `pageTitle` is rendered as an `<h1 class="bo-page-title">` in the top bar. Keep it short (≤3 words).
- `pageActions` is rendered next to the title in the top bar. Use only for top-level, page-wide actions.
- `pageScripts` is for page-specific JS only. ECharts initialization goes here.

---

## 3. Design Tokens (CSS Variables)

Always use these tokens. Never hardcode colours or sizes that correspond to tokens.

| Token | Value | Use |
|-------|-------|-----|
| `--bo-accent` | `#6366f1` | Primary action colour (indigo) |
| `--bo-accent-hover` | `#5558e6` | Hover state for accent |
| `--bo-accent-light` | `rgba(99,102,241,0.1)` | Accent background tints |
| `--bo-text-primary` | `#1e1e2f` | Main body text |
| `--bo-text-secondary` | `#6b7280` | Labels, secondary info |
| `--bo-text-muted` | `#9ca3af` | Placeholders, hints |
| `--bo-card-bg` | `#ffffff` | Card background |
| `--bo-card-border` | `#e5e7eb` | Card border |
| `--bo-card-shadow` | subtle elevation | Card default shadow |
| `--bo-content-bg` | `#f5f6fa` | Page background |
| `--bo-radius` | `12px` | Large radius (cards) |
| `--bo-radius-sm` | `8px` | Small radius (inputs, buttons) |
| `--bo-transition` | `0.2s ease` | Standard transition |

---

## 4. Custom CSS Classes Reference

These classes live in `backoffice.css`. Use them — do not recreate their styles inline.

### Cards & Content
| Class | Purpose |
|-------|---------|
| `.bo-module-card` | White rounded card with border and shadow. The main building block for all content areas. Usually combined with `p-0` when content fills the card edge-to-edge (e.g., tables). |
| `.bo-module-title` | Section heading inside a card (rendered as `<h5>`). |
| `.bo-module-icon` | Large accent-coloured icon for dashboard/placeholder cards. |
| `.bo-module-desc` | Secondary text below a module title. |
| `.bo-badge-soon` | Accent-coloured pill badge for "Coming Soon" states. |
| `.bo-welcome-banner` | Gradient indigo hero banner for module dashboards. |

### Layout
| Class | Purpose |
|-------|---------|
| `.bo-body` | Applied to `<body>`. Sets Inter font and full-height flex layout. |
| `.bo-sidebar` | Fixed dark sidebar. Do not modify inline. |
| `.bo-main` | Content area to the right of sidebar. |
| `.bo-content` | Inner padding wrapper inside `.bo-main`. |
| `.bo-topbar` | Sticky white top bar. |
| `.bo-page-title` | `<h1>` inside the top bar. |

### Login Page (isolated page, no sidebar)
Use `.bo-login-body`, `.bo-login-card`, `.bo-input`, `.bo-login-btn` etc. These are for the login page only.

---

## 5. Content Cards

### Standard Card

All major sections of a page MUST be wrapped in a `bo-module-card`.

```html
<!-- Flush card (table inside, content goes to edges) -->
<div class="bo-module-card p-0">
    <div class="d-flex justify-content-between align-items-center p-4 border-bottom">
        <h5 class="bo-module-title mb-0">Section Title</h5>
        <!-- Optional right-side action button -->
        <button class="btn btn-sm btn-outline-primary d-inline-flex align-items-center">
            <span class="material-symbols-outlined fs-6 me-1">add</span> Add Item
        </button>
    </div>
    <!-- content -->
</div>

<!-- Padded card (for forms, key-value displays, etc.) -->
<div class="bo-module-card">
    <!-- content directly, no inner p-4 needed -->
</div>
```

### Dashboard Metric Tiles

For module dashboards, use Bootstrap background utilities with `bo-card` (not `bo-module-card`):

```html
<div class="row g-4 mb-4">
    <div class="col-md-4">
        <div class="bo-card h-100 bg-primary text-white text-center p-4">
            <h6 class="text-white-50 text-uppercase tracking-wide mb-2">Metric Label</h6>
            <h2 class="display-5 fw-bold mb-0" th:text="${metrics.value}">0</h2>
        </div>
    </div>
    <!-- repeat for additional tiles: bg-success, bg-info -->
</div>
```

Tile colours in order: `bg-primary`, `bg-success`, `bg-info`.

---

## 6. Tables & List Views

### Full Structure

```html
<!-- Toolbar above the card -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <form th:action="@{/backoffice/module}" method="get" class="d-flex gap-2">
        <input type="text" name="q" class="form-control" placeholder="Search..." 
               th:value="${q}" style="width: 350px;">
        <button type="submit" class="btn btn-outline-secondary">
            <span class="material-symbols-outlined" style="vertical-align: middle; font-size: 18px;">search</span>
        </button>
    </form>
    <!-- Optional: global "New" button on the right -->
</div>

<!-- Card with table -->
<div class="bo-module-card p-0">
    <!-- Empty state (shown instead of table when list is empty) -->
    <div th:if="${result.items.empty}" class="p-5 text-center text-muted">
        <span class="material-symbols-outlined fs-1 mb-3">icon_name</span>
        <h5>No [items] found</h5>
        <p th:if="${not #strings.isEmpty(q)}">Try adjusting your search criteria.</p>
    </div>

    <!-- Table (only rendered when list is non-empty) -->
    <div th:unless="${result.items.empty}" class="table-responsive">
        <table class="table table-hover mb-0 align-middle">
            <thead>
                <tr>
                    <th style="width: 80px;">#</th>
                    <th>Name</th>
                    <!-- ... -->
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="item : ${result.items}">
                    <td>
                        <span class="badge bg-light text-dark border" th:text="${item.number}">001</span>
                    </td>
                    <!-- ... -->
                    <td class="text-end">
                        <!-- Action buttons — see Section 7 -->
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
```

### Table Thead Styling
Thead styles are handled automatically by `.bo-module-card .table thead th` in `backoffice.css`:
- `background: #f8f9fa`, uppercase, `font-size: 13px`, `font-weight: 600`, `color: --bo-text-secondary`.

**Do not add these inline.** Just use `<thead>` inside a `.bo-module-card` and the CSS handles it.

### Pagination

```html
<nav th:if="${result.totalPages > 1}" class="mt-3">
    <ul class="pagination justify-content-center">
        <li class="page-item" th:classappend="${result.page == 0} ? 'disabled'">
            <a class="page-link" th:href="@{/backoffice/module(q=${q}, page=${result.page - 1}, size=${result.size})}">Previous</a>
        </li>
        <li class="page-item" th:each="p : ${#numbers.sequence(0, result.totalPages - 1)}"
            th:classappend="${p == result.page} ? 'active'">
            <a class="page-link" th:href="@{/backoffice/module(q=${q}, page=${p}, size=${result.size})}" th:text="${p + 1}">1</a>
        </li>
        <li class="page-item" th:classappend="${result.page == result.totalPages - 1} ? 'disabled'">
            <a class="page-link" th:href="@{/backoffice/module(q=${q}, page=${result.page + 1}, size=${result.size})}">Next</a>
        </li>
    </ul>
</nav>
```

---

## 7. Row-Level Action Menus

Use this exact pattern. Two components side by side in an `Actions` column (`text-end`):

```html
<td class="text-end">
    <div class="d-flex justify-content-end gap-1">
        <!-- Primary action: direct icon button (e.g., view/edit) -->
        <a th:href="@{/backoffice/module/{id}(id=${item.id})}"
           class="btn btn-sm btn-outline-primary d-inline-flex align-items-center justify-content-center"
           title="View Details"
           style="width: 32px; height: 32px; padding: 0;">
            <span class="material-symbols-outlined" style="font-size: 18px;">edit</span>
        </a>

        <!-- Secondary actions: three-dot dropdown -->
        <div class="dropdown">
            <button class="btn btn-sm btn-outline-secondary d-inline-flex align-items-center justify-content-center"
                    type="button" data-bs-toggle="dropdown"
                    style="width: 32px; height: 32px; padding: 0;" title="More Actions">
                <span class="material-symbols-outlined" style="font-size: 18px;">more_vert</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                <li>
                    <!-- Destructive actions in red with icon -->
                    <button type="button" class="dropdown-item text-danger d-flex align-items-center gap-2">
                        <span class="material-symbols-outlined" style="font-size: 18px;">delete</span>
                        Delete
                    </button>
                </li>
            </ul>
        </div>
    </div>
</td>
```

**Rules:**
- Maximum **one** primary icon button per row. Use `btn-outline-primary`.
- All secondary/destructive actions go in the three-dot dropdown.
- Destructive items use `text-danger` and always include an icon.
- **Never** use JavaScript `alert()` or `confirm()` for delete confirmations. Use a Bootstrap popover or modal.

---

## 8. Read-Only Key-Value Display (Detail Views)

Use `<dl>` inside a padded card section:

```html
<div class="p-4">
    <dl class="row mb-0">
        <dt class="col-sm-4 text-muted fw-normal">Email</dt>
        <dd class="col-sm-8 fw-semibold" th:text="${entity.email}"></dd>

        <dt class="col-sm-4 text-muted fw-normal mt-2">Created</dt>
        <dd class="col-sm-8 fw-semibold mt-2"
            th:text="${#temporals.format(entity.createdAt, 'MMM dd, yyyy')}"></dd>
    </dl>
</div>
```

Use `mt-2` on subsequent `<dt>`/`<dd>` pairs for vertical spacing.

---

## 9. Inline HTMX Editing

For inline editing without a full page load (e.g., editing a name field directly on a detail page):

```html
<!-- Trigger button -->
<button class="btn btn-sm btn-outline-primary d-inline-flex align-items-center"
        th:hx-get="@{/backoffice/module/{id}/edit-field(id=${entity.id})}"
        hx-target="#field-container"
        hx-swap="outerHTML">
    <span class="material-symbols-outlined fs-6 me-1">edit</span> Edit
</button>

<!-- Target container (replaced by HTMX with edit form fragment) -->
<div id="field-container" th:replace="~{module/fragments/field-display :: field-display(entity=${entity})}"></div>
```

Fragment files live under `templates/backoffice/<module>/fragments/`. Each fragment provides both a **display** variant and an **edit form** variant. The edit form posts back and returns the display variant on success.

**CSRF in HTMX:** All state-modifying HTMX requests (POST/PUT/DELETE) automatically get CSRF headers injected by the `htmx:configRequest` listener in `default.html`. Nothing extra is needed in fragment templates.

---

## 10. Badges

| Purpose | HTML |
|---------|------|
| Entity IDs / Reference numbers | `<span class="badge bg-light text-dark border">0001</span>` |
| Counts (orders, items) | `<span class="badge bg-info text-white rounded-pill">5</span>` |
| Status (active) | `<span class="badge bg-success">Active</span>` |
| Status (inactive/disabled) | `<span class="badge bg-secondary">Inactive</span>` |
| Coming soon | `<span class="bo-badge-soon">Soon</span>` |

---

## 11. Icons

Use **Google Material Symbols Outlined** exclusively. Loaded via `backoffice-fonts.css`.

```html
<!-- Inline with text (most common usage) -->
<span class="material-symbols-outlined fs-6 me-1">icon_name</span>

<!-- Standalone icon button (18px in 32x32 button) -->
<span class="material-symbols-outlined" style="font-size: 18px;">icon_name</span>

<!-- Large decorative/empty-state icon -->
<span class="material-symbols-outlined fs-1 mb-3">icon_name</span>
```

Always wrap icon + text in `d-inline-flex align-items-center` on the parent element.

**Common icon names in use:**
`arrow_back`, `add`, `edit`, `delete`, `search`, `more_vert`, `group_off`, `dashboard`, `group`, `manage_accounts`, `receipt_long`, `admin_panel_settings`, `logout`, `history`, `shield_person`, `storefront`.

---

## 12. Forms (Add/Edit)

- Use Bootstrap's standard `form-control` and `form-select` classes.
- Mandatory fields: suffix the `<label>` text with ` *` (a space and an asterisk).
- Use `th:action` (never hardcoded `action`) on all `<form method="post">` elements.
- Validation error display: use `is-invalid` on the input and `<div class="invalid-feedback">` below it.

---

## 13. Toast Notifications

The global toast container is already in `default.html`. To trigger it from an HTMX response, emit a custom event via an `HX-Trigger` response header:

```java
// In controller, after a successful operation:
response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Saved successfully.\", \"type\": \"success\"}}");
```

Types: `primary`, `success`, `danger`, `warning`, `info`.

---

## 14. ECharts (Dashboard Charts)

Include ECharts per-page only, not globally:

```html
<head>
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js"></script>
</head>
```

Initialize in the `pageScripts` fragment block. Always:
- Use `document.addEventListener("DOMContentLoaded", ...)` 
- Call `myChart.resize()` on `window.addEventListener('resize', ...)`
- Use the accent colour `#6366f1` as the primary series colour.

---

## 15. Back Navigation

Detail pages always provide a back link as the first element inside `layout:fragment="content"`:

```html
<div class="mb-4">
    <a th:href="@{/backoffice/module}" class="btn btn-outline-secondary d-inline-flex align-items-center">
        <span class="material-symbols-outlined fs-6 me-1">arrow_back</span>
        Back to List
    </a>
</div>
```

---

## 16. Design Decisions & Future Considerations

### Why `bo-*` Custom Classes Instead of Pure Bootstrap Utilities

The `bo-*` prefix is an intentional architectural choice, not legacy debt. It serves four purposes:

1. **Isolation from storefront** — The storefront has its own CSS (`posters.css`, `style.css`, etc.) which is loaded on shared pages (error pages, etc.). The `bo-` namespace guarantees zero collisions.
2. **Semantic identity** — `bo-module-card` conveys *what* it is and *where* it belongs. A chain of Bootstrap utilities (`rounded-3 bg-white border shadow-sm p-4`) conveys only visual properties, not identity. Future maintainers can grep for `bo-module-card` and find every card instance.
3. **Design token binding** — All `--bo-*` CSS custom properties need an anchor. The `bo-*` classes are those anchors. Changing `--bo-accent` in one place updates every component that references it.
4. **Single-point restyling** — To restyle all cards, change one rule in `backoffice.css`. With pure utilities, you'd need a global find-and-replace across every template.

The `bo-*` classes *complement* Bootstrap utilities — they are not a replacement. Bootstrap utilities handle spacing, flex, display, and colour. `bo-*` handles design system identity and token binding.

**Do not refactor `bo-*` classes to pure Bootstrap utilities.**

---

### Why Plain CSS Instead of Sass

`backoffice.css` is intentionally plain CSS with CSS custom properties. There is **no Sass/SCSS compilation pipeline** in this project. Bootstrap itself is vendored as pre-compiled CSS in `static/lib/` — there is no frontend build step, no Node, no `frontend-maven-plugin`.

**Benefits of the current approach:**
- Zero tooling overhead — edit CSS, save, refresh.
- No build dependencies to maintain.
- CSS custom properties (`--bo-*`) provide runtime theming without compilation.

**What Sass would add:**
- Nesting (modest readability improvement for 554-line stylesheet)
- `@use` module system (relevant if the stylesheet grows significantly)
- The ability to override Bootstrap Sass variables *before* compilation, so Bootstrap generates colours matching `--bo-accent` natively — removing the need for dual-variable overrides

**When to consider migrating to Sass:**
- When `backoffice.css` grows beyond ~1000 lines and organisation becomes painful
- When new modules require enough shared mixins/utilities that duplication becomes a maintenance problem
- When you want Bootstrap to generate its own utilities in the accent colour (`$primary: #6366f1`) instead of overriding after the fact

**If you do migrate:** Use `frontend-maven-plugin` + `dart-sass`. Override Bootstrap Sass variables (`$primary`, `$border-radius`, `$font-family-base`) before `@use "bootstrap"`. The `bootstrap-customize` skill (not currently installed) covers this migration pattern.

**Do not add a Sass build pipeline without explicit user approval — it is a project-wide infrastructure change.**
