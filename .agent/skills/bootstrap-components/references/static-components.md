# Static Components Reference

Detailed documentation for Bootstrap 5.3 components that work primarily with CSS and HTML, without requiring JavaScript initialization.

## Alerts

To display contextual feedback messages, use the alert component with contextual color variants. Alerts communicate success, warnings, errors, or informational messages to users.

```html
<div class="alert alert-primary" role="alert">A primary alert</div>
<div class="alert alert-success" role="alert">A success alert</div>
<div class="alert alert-danger" role="alert">A danger alert</div>
<div class="alert alert-warning" role="alert">A warning alert</div>

<!-- With styled link -->
<div class="alert alert-info" role="alert">
  Check <a href="#" class="alert-link">this link</a>.
</div>

<!-- With heading and structured content -->
<div class="alert alert-success" role="alert">
  <h4 class="alert-heading">Well done!</h4>
  <p>You successfully completed the task.</p>
  <hr>
  <p class="mb-0">Additional information here.</p>
</div>

<!-- Dismissible alert -->
<div class="alert alert-warning alert-dismissible fade show" role="alert">
  Dismissible alert!
  <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
```

**Accessibility:** Always include `role="alert"` for important messages. For dismissible alerts, the close button must have `aria-label="Close"`. Screen readers announce alerts automatically when they appear in the DOM.

## Badges

To add labels, counters, or status indicators to elements, use the badge component. Badges work well for notification counts, status labels, or categorization tags.

```html
<span class="badge bg-primary">Primary</span>
<span class="badge bg-secondary">Secondary</span>
<span class="badge bg-success">Success</span>
<span class="badge bg-danger">Danger</span>
<span class="badge bg-warning text-dark">Warning</span>
<span class="badge bg-info text-dark">Info</span>

<!-- Pill badges (fully rounded) -->
<span class="badge rounded-pill bg-primary">Pill</span>

<!-- As button counter -->
<button class="btn btn-primary">
  Notifications <span class="badge bg-secondary">4</span>
</button>

<!-- Positioned notification badge -->
<button class="btn btn-primary position-relative">
  Inbox
  <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
    99+
    <span class="visually-hidden">unread messages</span>
  </span>
</button>
```

**Accessibility:** When using badges as counters, include `visually-hidden` text to provide context for screen readers. The visible badge alone ("99+") lacks context without the hidden "unread messages" text.

## Breadcrumb

To show navigation hierarchy and help users understand their location within a site, use the breadcrumb component. Breadcrumbs improve navigation UX on multi-level sites.

```html
<nav aria-label="breadcrumb">
  <ol class="breadcrumb">
    <li class="breadcrumb-item"><a href="#">Home</a></li>
    <li class="breadcrumb-item"><a href="#">Library</a></li>
    <li class="breadcrumb-item active" aria-current="page">Data</li>
  </ol>
</nav>

<!-- Custom divider using CSS variable -->
<nav style="--bs-breadcrumb-divider: '>';" aria-label="breadcrumb">
  <ol class="breadcrumb">...</ol>
</nav>
```

**Accessibility:** Wrap breadcrumbs in a `<nav>` element with `aria-label="breadcrumb"`. Mark the current page with `aria-current="page"` on the active item.

## Buttons

To create interactive button elements, use the button component with contextual color variants. Buttons trigger actions and should clearly indicate their purpose.

```html
<!-- Contextual variants -->
<button class="btn btn-primary">Primary</button>
<button class="btn btn-secondary">Secondary</button>
<button class="btn btn-success">Success</button>
<button class="btn btn-danger">Danger</button>
<button class="btn btn-warning">Warning</button>
<button class="btn btn-info">Info</button>
<button class="btn btn-light">Light</button>
<button class="btn btn-dark">Dark</button>
<button class="btn btn-link">Link</button>

<!-- Outline variants (transparent background) -->
<button class="btn btn-outline-primary">Outline Primary</button>

<!-- Sizes -->
<button class="btn btn-lg btn-primary">Large</button>
<button class="btn btn-sm btn-primary">Small</button>

<!-- Block button (full width) -->
<div class="d-grid">
  <button class="btn btn-primary">Block button</button>
</div>

<!-- States -->
<button class="btn btn-primary" disabled>Disabled</button>
<button class="btn btn-primary active">Active</button>
```

**Accessibility:** Use `<button>` elements for actions and `<a>` elements for navigation. For disabled buttons, the `disabled` attribute prevents interaction and announces the disabled state to screen readers.

## Button Group

To group related buttons together visually and semantically, use the button group component. Button groups are useful for toolbars, segmented controls, or related action sets.

```html
<div class="btn-group" role="group" aria-label="Basic example">
  <button type="button" class="btn btn-primary">Left</button>
  <button type="button" class="btn btn-primary">Middle</button>
  <button type="button" class="btn btn-primary">Right</button>
</div>

<!-- Toolbar (multiple button groups) -->
<div class="btn-toolbar" role="toolbar" aria-label="Toolbar with button groups">
  <div class="btn-group me-2" role="group" aria-label="First group">...</div>
  <div class="btn-group" role="group" aria-label="Second group">...</div>
</div>

<!-- Vertical button group -->
<div class="btn-group-vertical" role="group" aria-label="Vertical button group">...</div>
```

**Accessibility:** Always include `role="group"` and `aria-label` on button groups to describe their purpose. For toolbars, use `role="toolbar"` on the container.

## Cards

To create flexible content containers with headers, footers, images, and various content types, use the card component. Cards are versatile building blocks for displaying related content.

```html
<div class="card" style="width: 18rem;">
  <img src="..." class="card-img-top" alt="Card image description">
  <div class="card-body">
    <h5 class="card-title">Card title</h5>
    <p class="card-text">Some quick example text.</p>
    <a href="#" class="btn btn-primary">Go somewhere</a>
  </div>
</div>

<!-- Card with header and footer -->
<div class="card">
  <div class="card-header">Featured</div>
  <div class="card-body">
    <h5 class="card-title">Special title</h5>
    <p class="card-text">Content</p>
  </div>
  <div class="card-footer text-muted">2 days ago</div>
</div>

<!-- Card with list group -->
<div class="card">
  <div class="card-header">Featured</div>
  <ul class="list-group list-group-flush">
    <li class="list-group-item">Item 1</li>
    <li class="list-group-item">Item 2</li>
  </ul>
</div>

<!-- Card grid layout -->
<div class="row row-cols-1 row-cols-md-3 g-4">
  <div class="col">
    <div class="card h-100">...</div>
  </div>
</div>
```

**Accessibility:** Always provide meaningful `alt` text for card images. Use semantic heading elements (`<h5>`, `<h6>`) for card titles to maintain document outline.

## Close Button

To create a generic dismiss button for alerts, modals, toasts, and other dismissible content, use the close button component. Close buttons provide consistent dismissal UI across components.

```html
<!-- Basic close button -->
<button type="button" class="btn-close" aria-label="Close"></button>

<!-- Disabled state -->
<button type="button" class="btn-close" disabled aria-label="Close"></button>

<!-- White variant for dark backgrounds -->
<div class="bg-dark p-3">
  <button type="button" class="btn-close btn-close-white" aria-label="Close"></button>
</div>

<!-- In alert -->
<div class="alert alert-warning alert-dismissible fade show" role="alert">
  Alert content
  <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>

<!-- In modal header -->
<div class="modal-header">
  <h1 class="modal-title fs-5">Modal title</h1>
  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
</div>

<!-- In toast header -->
<div class="toast-header">
  <strong class="me-auto">Toast title</strong>
  <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
</div>
```

**Accessibility:** Always include `aria-label="Close"` since the button has no visible text. The `data-bs-dismiss` attribute specifies which parent component to dismiss.

## List Group

To display a series of content items as a flexible and powerful list, use the list group component. List groups handle lists of links, buttons, or custom content with optional actions.

```html
<ul class="list-group">
  <li class="list-group-item">An item</li>
  <li class="list-group-item active" aria-current="true">Active item</li>
  <li class="list-group-item disabled" aria-disabled="true">Disabled item</li>
</ul>

<!-- Actionable items (links or buttons) -->
<div class="list-group">
  <a href="#" class="list-group-item list-group-item-action active" aria-current="true">Active link</a>
  <a href="#" class="list-group-item list-group-item-action">Link</a>
  <button type="button" class="list-group-item list-group-item-action">Button</button>
</div>

<!-- Flush variant (no outer borders) -->
<ul class="list-group list-group-flush">...</ul>

<!-- Horizontal layout -->
<ul class="list-group list-group-horizontal">...</ul>

<!-- With badges -->
<li class="list-group-item d-flex justify-content-between align-items-center">
  Item
  <span class="badge bg-primary rounded-pill">14</span>
</li>

<!-- Contextual colors -->
<li class="list-group-item list-group-item-primary">Primary item</li>
<li class="list-group-item list-group-item-danger">Danger item</li>
```

**Accessibility:** Use `aria-current="true"` on the active item. For disabled items, include `aria-disabled="true"`. Use `<ul>` for static lists, `<div>` for actionable items.

## Navbar

To create a responsive navigation header with branding, links, and collapsible content, use the navbar component. Navbars adapt to mobile screens with a hamburger toggle.

```html
<nav class="navbar navbar-expand-lg bg-body-tertiary">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Navbar</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
      aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav">
        <li class="nav-item">
          <a class="nav-link active" aria-current="page" href="#">Home</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#">Features</a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" aria-expanded="false">Dropdown</a>
          <ul class="dropdown-menu">
            <li><a class="dropdown-item" href="#">Action</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>

<!-- Color schemes -->
<nav class="navbar bg-dark" data-bs-theme="dark">...</nav>
<nav class="navbar bg-primary" data-bs-theme="dark">...</nav>

<!-- Placement options -->
<nav class="navbar fixed-top">...</nav>    <!-- Fixed to viewport top -->
<nav class="navbar fixed-bottom">...</nav> <!-- Fixed to viewport bottom -->
<nav class="navbar sticky-top">...</nav>   <!-- Sticks after scrolling past -->
```

**Accessibility:** Include `aria-label="Toggle navigation"` on the toggler button. Mark the current page with `aria-current="page"`. The toggler manages `aria-expanded` automatically.

## Navs and Tabs

To create base navigation with multiple visual styles (tabs, pills, underlines), use the nav component. Navs handle both navigation and tabbed content interfaces.

```html
<!-- Basic nav -->
<ul class="nav">
  <li class="nav-item">
    <a class="nav-link active" aria-current="page" href="#">Active</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="#">Link</a>
  </li>
  <li class="nav-item">
    <a class="nav-link disabled" aria-disabled="true">Disabled</a>
  </li>
</ul>

<!-- Tabs style -->
<ul class="nav nav-tabs">...</ul>

<!-- Pills style -->
<ul class="nav nav-pills">...</ul>

<!-- Underline style -->
<ul class="nav nav-underline">...</ul>

<!-- Fill (equal width) and justify (full width) -->
<ul class="nav nav-pills nav-fill">...</ul>
<ul class="nav nav-pills nav-justified">...</ul>

<!-- Vertical orientation -->
<ul class="nav flex-column">...</ul>

<!-- Tabs with content panels -->
<ul class="nav nav-tabs" id="myTab" role="tablist">
  <li class="nav-item" role="presentation">
    <button class="nav-link active" id="home-tab" data-bs-toggle="tab" data-bs-target="#home-pane"
      type="button" role="tab" aria-controls="home-pane" aria-selected="true">Home</button>
  </li>
  <li class="nav-item" role="presentation">
    <button class="nav-link" id="profile-tab" data-bs-toggle="tab" data-bs-target="#profile-pane"
      type="button" role="tab" aria-controls="profile-pane" aria-selected="false">Profile</button>
  </li>
</ul>
<div class="tab-content" id="myTabContent">
  <div class="tab-pane fade show active" id="home-pane" role="tabpanel" aria-labelledby="home-tab" tabindex="0">
    Home content...
  </div>
  <div class="tab-pane fade" id="profile-pane" role="tabpanel" aria-labelledby="profile-tab" tabindex="0">
    Profile content...
  </div>
</div>
```

**Accessibility:** For tabs, use proper ARIA roles: `role="tablist"` on container, `role="presentation"` on `<li>`, `role="tab"` on buttons, `role="tabpanel"` on content panes. Include `aria-selected`, `aria-controls`, and `aria-labelledby` as shown.

## Pagination

To create navigation for paginated content (search results, tables, lists), use the pagination component. Pagination helps users navigate through pages of content.

```html
<nav aria-label="Page navigation">
  <ul class="pagination">
    <li class="page-item">
      <a class="page-link" href="#" aria-label="Previous">
        <span aria-hidden="true">&laquo;</span>
      </a>
    </li>
    <li class="page-item"><a class="page-link" href="#">1</a></li>
    <li class="page-item active" aria-current="page"><a class="page-link" href="#">2</a></li>
    <li class="page-item"><a class="page-link" href="#">3</a></li>
    <li class="page-item">
      <a class="page-link" href="#" aria-label="Next">
        <span aria-hidden="true">&raquo;</span>
      </a>
    </li>
  </ul>
</nav>

<!-- Disabled state for unavailable pages -->
<li class="page-item disabled">
  <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Previous</a>
</li>

<!-- Size variants -->
<ul class="pagination pagination-lg">...</ul>
<ul class="pagination pagination-sm">...</ul>

<!-- Alignment -->
<ul class="pagination justify-content-center">...</ul>
<ul class="pagination justify-content-end">...</ul>
```

**Accessibility:** Wrap in `<nav>` with descriptive `aria-label`. Mark current page with `aria-current="page"`. For prev/next arrows, use `aria-label` on the link and `aria-hidden="true"` on decorative icons.

## Placeholders

To show loading states with animated placeholder elements while content loads, use the placeholder component. Placeholders indicate that content is being fetched or processed.

```html
<!-- Basic placeholder text -->
<p class="placeholder-glow">
  <span class="placeholder col-6"></span>
</p>

<!-- Card skeleton -->
<div class="card" aria-hidden="true">
  <div class="card-body">
    <h5 class="card-title placeholder-glow">
      <span class="placeholder col-6"></span>
    </h5>
    <p class="card-text placeholder-glow">
      <span class="placeholder col-7"></span>
      <span class="placeholder col-4"></span>
      <span class="placeholder col-4"></span>
      <span class="placeholder col-6"></span>
    </p>
    <a class="btn btn-primary disabled placeholder col-6" aria-disabled="true"></a>
  </div>
</div>

<!-- Size variants -->
<span class="placeholder col-12"></span>
<span class="placeholder placeholder-lg col-12"></span>
<span class="placeholder placeholder-sm col-12"></span>
<span class="placeholder placeholder-xs col-12"></span>

<!-- Color variants -->
<span class="placeholder col-12 bg-primary"></span>
<span class="placeholder col-12 bg-secondary"></span>
<span class="placeholder col-12 bg-success"></span>

<!-- Animation styles -->
<p class="placeholder-glow">  <!-- Pulsing glow -->
  <span class="placeholder col-12"></span>
</p>
<p class="placeholder-wave">  <!-- Wave animation -->
  <span class="placeholder col-12"></span>
</p>
```

**Accessibility:** Add `aria-hidden="true"` to placeholder containers since they contain no meaningful content. Replace placeholders with actual content once loaded.

## Progress

To display progress indicators for tasks, uploads, or loading states, use the progress component. Progress bars provide visual feedback on completion status.

```html
<div class="progress" role="progressbar" aria-label="Basic example" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100">
  <div class="progress-bar" style="width: 25%"></div>
</div>

<!-- With visible label -->
<div class="progress" role="progressbar" aria-label="Example with label" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100">
  <div class="progress-bar" style="width: 25%">25%</div>
</div>

<!-- Contextual colors -->
<div class="progress-bar bg-success" style="width: 25%"></div>
<div class="progress-bar bg-info" style="width: 50%"></div>
<div class="progress-bar bg-warning" style="width: 75%"></div>
<div class="progress-bar bg-danger" style="width: 100%"></div>

<!-- Striped -->
<div class="progress-bar progress-bar-striped" style="width: 50%"></div>

<!-- Animated stripes -->
<div class="progress-bar progress-bar-striped progress-bar-animated" style="width: 75%"></div>

<!-- Multiple bars (stacked) -->
<div class="progress" role="progressbar" aria-label="Segment example">
  <div class="progress-bar" style="width: 15%"></div>
  <div class="progress-bar bg-success" style="width: 30%"></div>
  <div class="progress-bar bg-info" style="width: 20%"></div>
</div>
```

**Accessibility:** Always include `role="progressbar"`, `aria-valuenow`, `aria-valuemin`, `aria-valuemax`, and `aria-label` on the outer `.progress` container. Update `aria-valuenow` dynamically when progress changes.

## Spinners

To indicate loading states with animated spinner indicators, use the spinner component. Spinners show users that an action is processing.

```html
<!-- Border spinner -->
<div class="spinner-border" role="status">
  <span class="visually-hidden">Loading...</span>
</div>

<!-- Growing spinner -->
<div class="spinner-grow" role="status">
  <span class="visually-hidden">Loading...</span>
</div>

<!-- Color variants -->
<div class="spinner-border text-primary" role="status"></div>
<div class="spinner-border text-secondary" role="status"></div>
<div class="spinner-border text-success" role="status"></div>
<div class="spinner-border text-danger" role="status"></div>

<!-- Size variants -->
<div class="spinner-border spinner-border-sm" role="status"></div>
<div class="spinner-grow spinner-grow-sm" role="status"></div>

<!-- In button (loading state) -->
<button class="btn btn-primary" type="button" disabled>
  <span class="spinner-border spinner-border-sm" aria-hidden="true"></span>
  <span class="visually-hidden" role="status">Loading...</span>
</button>

<button class="btn btn-primary" type="button" disabled>
  <span class="spinner-border spinner-border-sm" aria-hidden="true"></span>
  Loading...
</button>
```

**Accessibility:** Include `role="status"` on spinners. Add `visually-hidden` text inside for screen readers. When used in buttons, add `aria-hidden="true"` to the spinner icon if visible text (like "Loading...") is also present.
