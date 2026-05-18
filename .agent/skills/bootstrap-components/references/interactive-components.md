# Interactive Components Reference

Detailed documentation for Bootstrap 5.3 components that require or benefit from JavaScript initialization.

## Accordion

To create collapsible content panels that show one section at a time, use the accordion component. Accordions are ideal for FAQs, settings panels, or any content that benefits from progressive disclosure.

```html
<div class="accordion" id="accordionExample">
  <div class="accordion-item">
    <h2 class="accordion-header">
      <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
        Accordion Item #1
      </button>
    </h2>
    <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#accordionExample">
      <div class="accordion-body">Content for first item.</div>
    </div>
  </div>
  <div class="accordion-item">
    <h2 class="accordion-header">
      <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
        Accordion Item #2
      </button>
    </h2>
    <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
      <div class="accordion-body">Content for second item.</div>
    </div>
  </div>
</div>

<!-- Flush variant (removes outer borders) -->
<div class="accordion accordion-flush">...</div>

<!-- Always open (remove data-bs-parent to allow multiple open panels) -->
<div id="collapseOne" class="accordion-collapse collapse show">...</div>
```

**JavaScript (optional):** Control accordion panels programmatically using the Collapse API:

```javascript
// Get or create a Collapse instance for a specific panel
const collapseOne = bootstrap.Collapse.getOrCreateInstance('#collapseOne', {
  toggle: false  // Don't toggle on init
});

// Methods
collapseOne.show();
collapseOne.hide();
collapseOne.toggle();

// Open all panels (requires removing data-bs-parent from markup)
document.querySelectorAll('.accordion-collapse').forEach(el => {
  bootstrap.Collapse.getOrCreateInstance(el, { toggle: false }).show();
});

// Close all panels
document.querySelectorAll('.accordion-collapse').forEach(el => {
  bootstrap.Collapse.getOrCreateInstance(el, { toggle: false }).hide();
});

// Events
document.getElementById('collapseOne').addEventListener('shown.bs.collapse', () => {
  console.log('Panel is now visible');
});

document.getElementById('collapseOne').addEventListener('hidden.bs.collapse', () => {
  console.log('Panel is now hidden');
});
```

**Accessibility:** Include `aria-expanded` (true/false) and `aria-controls` (matching the collapse panel's `id`) on each accordion button. Bootstrap manages `aria-expanded` state automatically. Ensure each `accordion-collapse` has a unique `id` that matches its trigger's `data-bs-target` and `aria-controls`.

## Carousel

To create an image slideshow with optional controls and indicators, use the carousel component. Carousels cycle through content automatically or on user interaction.

```html
<div id="carouselExample" class="carousel slide">
  <div class="carousel-inner">
    <div class="carousel-item active">
      <img src="..." class="d-block w-100" alt="First slide description">
    </div>
    <div class="carousel-item">
      <img src="..." class="d-block w-100" alt="Second slide description">
    </div>
  </div>
  <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample" data-bs-slide="prev">
    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
    <span class="visually-hidden">Previous</span>
  </button>
  <button class="carousel-control-next" type="button" data-bs-target="#carouselExample" data-bs-slide="next">
    <span class="carousel-control-next-icon" aria-hidden="true"></span>
    <span class="visually-hidden">Next</span>
  </button>
</div>

<!-- With slide indicators -->
<div class="carousel-indicators">
  <button type="button" data-bs-target="#carouselExample" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
  <button type="button" data-bs-target="#carouselExample" data-bs-slide-to="1" aria-label="Slide 2"></button>
</div>

<!-- With captions -->
<div class="carousel-item">
  <img src="..." class="d-block w-100" alt="...">
  <div class="carousel-caption d-none d-md-block">
    <h5>Slide label</h5>
    <p>Description text.</p>
  </div>
</div>

<!-- Autoplay -->
<div class="carousel slide" data-bs-ride="carousel">...</div>
```

**JavaScript (optional):** Control carousel programmatically:

```javascript
const carousel = new bootstrap.Carousel('#carouselExample', {
  interval: 5000,  // Time between slides (ms)
  wrap: true,      // Cycle continuously
  pause: 'hover'   // Pause on mouse hover
});

// Methods
carousel.next();
carousel.prev();
carousel.pause();
```

**Accessibility:** Provide descriptive `alt` text for all images. Include `visually-hidden` text in controls. Consider users who prefer reduced motion by respecting `prefers-reduced-motion` media query (Bootstrap handles this automatically).

## Collapse

To toggle the visibility of content with smooth animations, use the collapse component. Collapse is useful for expandable sections, show/hide functionality, and accordion-like behavior.

```html
<button class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#collapseExample">
  Toggle
</button>
<div class="collapse" id="collapseExample">
  <div class="card card-body">
    Collapsible content here.
  </div>
</div>

<!-- Horizontal collapse -->
<button class="btn btn-primary" data-bs-toggle="collapse" data-bs-target="#collapseWidthExample">
  Toggle width
</button>
<div class="collapse collapse-horizontal" id="collapseWidthExample">
  <div class="card card-body" style="width: 300px;">
    Horizontal content
  </div>
</div>

<!-- Multiple targets -->
<button class="btn btn-primary" data-bs-toggle="collapse" data-bs-target=".multi-collapse">
  Toggle both
</button>
<div class="collapse multi-collapse" id="multiCollapseOne">First content</div>
<div class="collapse multi-collapse" id="multiCollapseTwo">Second content</div>
```

**JavaScript (optional):** Control collapse programmatically:

```javascript
const collapse = new bootstrap.Collapse('#collapseExample', {
  toggle: false  // Don't toggle on init
});

collapse.show();
collapse.hide();
collapse.toggle();
```

**Accessibility:** The trigger element automatically manages `aria-expanded` state. Ensure the `data-bs-target` matches the collapse element's `id`.

## Dropdowns

To create toggleable contextual menus for displaying lists of links and actions, use the dropdown component. Dropdowns work via data attributes or JavaScript.

```html
<div class="dropdown">
  <button class="btn btn-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
    Dropdown button
  </button>
  <ul class="dropdown-menu">
    <li><a class="dropdown-item" href="#">Action</a></li>
    <li><a class="dropdown-item" href="#">Another action</a></li>
    <li><hr class="dropdown-divider"></li>
    <li><a class="dropdown-item" href="#">Separated link</a></li>
  </ul>
</div>

<!-- Direction variants -->
<div class="dropup">...</div>    <!-- Opens upward -->
<div class="dropend">...</div>   <!-- Opens to the right -->
<div class="dropstart">...</div> <!-- Opens to the left -->

<!-- Dark dropdown menu -->
<ul class="dropdown-menu dropdown-menu-dark">...</ul>

<!-- With headers -->
<li><h6 class="dropdown-header">Header</h6></li>

<!-- Disabled item -->
<li><a class="dropdown-item disabled" href="#" aria-disabled="true">Disabled</a></li>
```

**Accessibility:** The trigger button must have `aria-expanded` attribute (Bootstrap manages this automatically). Disabled items should include `aria-disabled="true"`. Keyboard navigation is built-in: Arrow keys navigate, Enter selects, Escape closes.

## Modal

To display dialog boxes, confirmations, or focused content that requires user interaction, use the modal component. Modals overlay the page and trap focus within themselves.

```html
<!-- Trigger button -->
<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
  Launch modal
</button>

<!-- Modal structure -->
<div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        Modal content here.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>

<!-- Size variants -->
<div class="modal-dialog modal-sm">...</div>  <!-- 300px -->
<div class="modal-dialog modal-lg">...</div>  <!-- 800px -->
<div class="modal-dialog modal-xl">...</div>  <!-- 1140px -->

<!-- Fullscreen variants -->
<div class="modal-dialog modal-fullscreen">...</div>           <!-- Always fullscreen -->
<div class="modal-dialog modal-fullscreen-sm-down">...</div>   <!-- Fullscreen below 576px -->
<div class="modal-dialog modal-fullscreen-md-down">...</div>   <!-- Fullscreen below 768px -->
<div class="modal-dialog modal-fullscreen-lg-down">...</div>   <!-- Fullscreen below 992px -->
<div class="modal-dialog modal-fullscreen-xl-down">...</div>   <!-- Fullscreen below 1200px -->
<div class="modal-dialog modal-fullscreen-xxl-down">...</div>  <!-- Fullscreen below 1400px -->

<!-- Vertically centered -->
<div class="modal-dialog modal-dialog-centered">...</div>

<!-- Scrollable body -->
<div class="modal-dialog modal-dialog-scrollable">...</div>

<!-- Static backdrop (can't close by clicking outside) -->
<div class="modal" data-bs-backdrop="static" data-bs-keyboard="false">...</div>
```

**JavaScript (optional):** Control modal programmatically:

```javascript
const modal = new bootstrap.Modal('#exampleModal', {
  backdrop: true,    // Show backdrop
  keyboard: true,    // Close on Escape
  focus: true        // Focus modal on open
});

modal.show();
modal.hide();
modal.toggle();
modal.handleUpdate();  // Readjust position after dynamic height change

// Events
document.getElementById('exampleModal').addEventListener('shown.bs.modal', () => {
  // Modal is fully visible
});

document.getElementById('exampleModal').addEventListener('hidePrevented.bs.modal', () => {
  // Fired when backdrop is static and user clicks outside, or Escape pressed with keyboard: false
  // Use this to provide feedback (e.g., shake animation, highlight close button)
});
```

**Varying modal content with relatedTarget:** Reuse a single modal with different content based on which button triggered it:

```html
<button data-bs-toggle="modal" data-bs-target="#dynamicModal" data-bs-user="@alice">Edit Alice</button>
<button data-bs-toggle="modal" data-bs-target="#dynamicModal" data-bs-user="@bob">Edit Bob</button>
```

```javascript
const modal = document.getElementById('dynamicModal');
modal.addEventListener('show.bs.modal', event => {
  // Get the button that triggered the modal
  const button = event.relatedTarget;
  // Extract data from data-bs-* attributes
  const user = button.getAttribute('data-bs-user');
  // Update modal content
  modal.querySelector('.modal-title').textContent = `Edit ${user}`;
  modal.querySelector('#userInput').value = user;
});
```

**Embedding YouTube videos:** YouTube iframes continue playing when modal closes. Stop playback manually:

```javascript
modal.addEventListener('hidden.bs.modal', () => {
  const iframe = modal.querySelector('iframe');
  if (iframe) {
    // Reset src to stop video
    const src = iframe.src;
    iframe.src = '';
    iframe.src = src;
  }
});
```

**Accessibility:** Modals must have `aria-labelledby` pointing to the modal title's `id`. Include `aria-hidden="true"` initially. Bootstrap handles focus trapping automatically—focus stays within the modal until closed.

## Offcanvas

To create hidden sidebars that slide in from the edge of the viewport, use the offcanvas component. Offcanvas panels work well for mobile navigation, filters, or secondary content.

```html
<button class="btn btn-primary" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasExample"
  aria-controls="offcanvasExample">
  Toggle offcanvas
</button>

<div class="offcanvas offcanvas-start" tabindex="-1" id="offcanvasExample" aria-labelledby="offcanvasExampleLabel">
  <div class="offcanvas-header">
    <h5 class="offcanvas-title" id="offcanvasExampleLabel">Offcanvas</h5>
    <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
  </div>
  <div class="offcanvas-body">
    Content here.
  </div>
</div>

<!-- Placement variants -->
<div class="offcanvas offcanvas-start">...</div>  <!-- Left (default) -->
<div class="offcanvas offcanvas-end">...</div>    <!-- Right -->
<div class="offcanvas offcanvas-top">...</div>    <!-- Top -->
<div class="offcanvas offcanvas-bottom">...</div> <!-- Bottom -->

<!-- Static backdrop (no dismiss on outside click) -->
<div class="offcanvas" data-bs-backdrop="static">...</div>

<!-- Enable body scrolling while open -->
<div class="offcanvas" data-bs-scroll="true">...</div>
```

**JavaScript (optional):** Control offcanvas programmatically:

```javascript
const offcanvas = new bootstrap.Offcanvas('#offcanvasExample');

offcanvas.show();
offcanvas.hide();
offcanvas.toggle();
```

**Accessibility:** Include `aria-labelledby` pointing to the title's `id`. The trigger must have `aria-controls` matching the offcanvas `id`. Focus is trapped within the offcanvas when open.

## Popovers

To display rich overlay content triggered by click or hover, use the popover component. Popovers can contain titles, text, and HTML content.

**Requires JavaScript initialization.** Popovers will not work without explicit initialization.

```html
<!-- Basic popover -->
<button type="button" class="btn btn-lg btn-danger" data-bs-toggle="popover"
  data-bs-title="Popover title" data-bs-content="And here's some amazing content.">
  Click to toggle popover
</button>

<!-- Placement directions -->
<button type="button" class="btn btn-secondary" data-bs-toggle="popover"
  data-bs-placement="top" data-bs-content="Top popover">
  Popover on top
</button>
<button type="button" class="btn btn-secondary" data-bs-toggle="popover"
  data-bs-placement="right" data-bs-content="Right popover">
  Popover on right
</button>
<button type="button" class="btn btn-secondary" data-bs-toggle="popover"
  data-bs-placement="bottom" data-bs-content="Bottom popover">
  Popover on bottom
</button>
<button type="button" class="btn btn-secondary" data-bs-toggle="popover"
  data-bs-placement="left" data-bs-content="Left popover">
  Popover on left
</button>

<!-- Dismiss on next click (focus trigger) -->
<a tabindex="0" class="btn btn-lg btn-danger" role="button" data-bs-toggle="popover"
  data-bs-trigger="focus" data-bs-title="Dismissible popover"
  data-bs-content="Click anywhere else to dismiss.">
  Dismissible popover
</a>

<!-- Hover trigger -->
<button type="button" class="btn btn-secondary" data-bs-toggle="popover"
  data-bs-trigger="hover focus" data-bs-content="Hover popover">
  Hover me
</button>

<!-- HTML content -->
<button type="button" class="btn btn-secondary" data-bs-toggle="popover"
  data-bs-html="true" data-bs-title="<em>HTML</em> title"
  data-bs-content="<b>Bold</b> and <u>underlined</u> content.">
  HTML Popover
</button>
```

**JavaScript initialization (required):**

```javascript
// Initialize all popovers on the page
const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
const popoverList = [...popoverTriggerList].map(el => new bootstrap.Popover(el));

// Initialize with options
const popover = new bootstrap.Popover('#myPopover', {
  placement: 'right',
  trigger: 'hover focus',
  html: true,
  content: '<strong>Rich</strong> content'
});

// Methods
popover.show();
popover.hide();
popover.toggle();
popover.dispose();       // Destroy popover
popover.enable();        // Re-enable a disabled popover
popover.disable();       // Prevent popover from showing
popover.toggleEnabled(); // Toggle enabled state

// Update popover content dynamically after initialization
popover.setContent({
  '.popover-header': 'New title',
  '.popover-body': 'New content'
});
```

**Key options:**

| Option | Default | Description |
|--------|---------|-------------|
| `container` | `false` | Appends popover to specific element. Use `'body'` to avoid rendering issues in input groups, button groups, or tables. |
| `selector` | `false` | Delegate popovers to dynamically added elements. Example: `selector: '[data-bs-toggle="popover"]'` |
| `boundary` | `'clippingParents'` | Overflow constraint for positioning. Set to `document.body` if popover gets clipped. |

**Dynamic elements:** Use the `selector` option to handle popovers on elements added after initialization:

```javascript
// Initialize once on a container, works for dynamically added elements
new bootstrap.Popover(document.body, {
  selector: '[data-bs-toggle="popover"]'
});
```

**Accessibility:** Popovers are not ideal for essential information since they may be missed by screen readers. Use for supplementary content only. For dismissible popovers, ensure keyboard users can trigger and dismiss them.

## Scrollspy

To automatically update navigation based on scroll position, use the scrollspy component. Scrollspy highlights nav items as you scroll through corresponding content sections.

**Works with data attributes**, but JavaScript can enhance functionality.

```html
<!-- Navbar with scrollspy -->
<nav id="navbar-example" class="navbar bg-body-tertiary px-3 mb-3">
  <a class="navbar-brand" href="#">Navbar</a>
  <ul class="nav nav-pills">
    <li class="nav-item">
      <a class="nav-link" href="#scrollspyHeading1">First</a>
    </li>
    <li class="nav-item">
      <a class="nav-link" href="#scrollspyHeading2">Second</a>
    </li>
    <li class="nav-item dropdown">
      <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#">Dropdown</a>
      <ul class="dropdown-menu">
        <li><a class="dropdown-item" href="#scrollspyHeading3">Third</a></li>
        <li><a class="dropdown-item" href="#scrollspyHeading4">Fourth</a></li>
      </ul>
    </li>
  </ul>
</nav>

<!-- Scrollable content with scrollspy -->
<div data-bs-spy="scroll" data-bs-target="#navbar-example" data-bs-root-margin="0px 0px -40%"
  data-bs-smooth-scroll="true" class="scrollspy-example p-3 rounded-2" tabindex="0"
  style="height: 300px; overflow-y: scroll;">
  <h4 id="scrollspyHeading1">First heading</h4>
  <p>Content for first section...</p>
  <h4 id="scrollspyHeading2">Second heading</h4>
  <p>Content for second section...</p>
  <h4 id="scrollspyHeading3">Third heading</h4>
  <p>Content for third section...</p>
  <h4 id="scrollspyHeading4">Fourth heading</h4>
  <p>Content for fourth section...</p>
</div>

<!-- List group variant -->
<div class="row">
  <div class="col-4">
    <div id="list-example" class="list-group">
      <a class="list-group-item list-group-item-action" href="#list-item-1">Item 1</a>
      <a class="list-group-item list-group-item-action" href="#list-item-2">Item 2</a>
    </div>
  </div>
  <div class="col-8">
    <div data-bs-spy="scroll" data-bs-target="#list-example" data-bs-smooth-scroll="true"
      class="scrollspy-example" tabindex="0">
      <h4 id="list-item-1">Item 1</h4>
      <p>Content...</p>
      <h4 id="list-item-2">Item 2</h4>
      <p>Content...</p>
    </div>
  </div>
</div>
```

**Key data attributes:**

| Attribute | Purpose |
|-----------|---------|
| `data-bs-spy="scroll"` | Enable scrollspy on this element |
| `data-bs-target` | CSS selector for navigation to update |
| `data-bs-root-margin` | Intersection Observer margin (e.g., "0px 0px -40%") |
| `data-bs-smooth-scroll="true"` | Enable smooth scrolling |

**JavaScript initialization (optional):**

```javascript
const scrollspy = new bootstrap.ScrollSpy(document.body, {
  target: '#navbar-example',
  rootMargin: '0px 0px -40%',
  smoothScroll: true
});

// Refresh after DOM changes
scrollspy.refresh();
```

**Accessibility:** The scrollspy container must have `tabindex="0"` so keyboard-only users can focus the element and scroll it using arrow keys (without `tabindex`, the container cannot receive focus and is inaccessible to keyboard users). Ensure section headings have unique `id` attributes matching nav hrefs.

## Toasts

To display lightweight, dismissible notifications, use the toast component. Toasts appear temporarily and stack when multiple are shown.

**Requires JavaScript to show.** Toasts are hidden by default and must be shown programmatically.

```html
<!-- Basic toast -->
<div class="toast" role="alert" aria-live="assertive" aria-atomic="true">
  <div class="toast-header">
    <strong class="me-auto">Bootstrap</strong>
    <small>11 mins ago</small>
    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
  </div>
  <div class="toast-body">
    Hello, world! This is a toast message.
  </div>
</div>

<!-- Simple toast (no header) -->
<div class="toast align-items-center" role="alert" aria-live="assertive" aria-atomic="true">
  <div class="d-flex">
    <div class="toast-body">
      Hello, world! This is a toast message.
    </div>
    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
  </div>
</div>

<!-- Color variants -->
<div class="toast align-items-center text-bg-primary border-0" role="alert">...</div>

<!-- Toast container for stacking -->
<div class="toast-container position-fixed bottom-0 end-0 p-3">
  <div class="toast">...</div>
  <div class="toast">...</div>
</div>

<!-- Placement options (via container positioning) -->
<div class="toast-container position-fixed top-0 start-50 translate-middle-x p-3">...</div>
<div class="toast-container position-fixed top-0 end-0 p-3">...</div>
```

**JavaScript initialization (required to show):**

```javascript
// Initialize and show a toast
const toastEl = document.querySelector('.toast');
const toast = new bootstrap.Toast(toastEl, {
  autohide: true,    // Auto dismiss
  delay: 5000        // Dismiss after 5 seconds
});
toast.show();

// Show toast triggered by button
document.getElementById('showToastBtn').addEventListener('click', () => {
  const toast = new bootstrap.Toast(document.getElementById('myToast'));
  toast.show();
});

// Methods
toast.show();
toast.hide();
toast.dispose();

// Events
toastEl.addEventListener('shown.bs.toast', () => {
  // Toast is now visible
});
```

**Accessibility:** Include `role="alert"`, `aria-live="assertive"` (for important messages) or `aria-live="polite"` (for less urgent), and `aria-atomic="true"`. Screen readers will announce toast content when shown.

## Tooltips

To add hover hints that provide brief descriptions, use the tooltip component. Tooltips display extra information when users hover or focus on an element.

**Requires JavaScript initialization.** Tooltips will not work without explicit initialization.

```html
<!-- Tooltip triggers -->
<button type="button" class="btn btn-secondary"
  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Tooltip on top">
  Tooltip on top
</button>
<button type="button" class="btn btn-secondary"
  data-bs-toggle="tooltip" data-bs-placement="right" data-bs-title="Tooltip on right">
  Tooltip on right
</button>
<button type="button" class="btn btn-secondary"
  data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-title="Tooltip on bottom">
  Tooltip on bottom
</button>
<button type="button" class="btn btn-secondary"
  data-bs-toggle="tooltip" data-bs-placement="left" data-bs-title="Tooltip on left">
  Tooltip on left
</button>

<!-- HTML content in tooltip -->
<button type="button" class="btn btn-secondary"
  data-bs-toggle="tooltip" data-bs-html="true"
  data-bs-title="<em>Tooltip</em> <u>with</u> <b>HTML</b>">
  Tooltip with HTML
</button>

<!-- Tooltip on disabled button (requires wrapper with tabindex for keyboard accessibility) -->
<span class="d-inline-block" tabindex="0" data-bs-toggle="tooltip" data-bs-title="Disabled tooltip">
  <button class="btn btn-primary" type="button" disabled>Disabled button</button>
</span>
```

**Note:** Use `data-bs-title` instead of `title` when possible. Both attributes work, but `data-bs-title` is the modern approach that avoids conflicts with the browser's native tooltip behavior.

**Disabled elements:** The `tabindex="0"` on the wrapper is essential—without it, keyboard users cannot focus the element to trigger the tooltip.

**JavaScript initialization (required):**

```javascript
// Initialize all tooltips on the page
const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
const tooltipList = [...tooltipTriggerList].map(el => new bootstrap.Tooltip(el));

// Initialize with options
const tooltip = new bootstrap.Tooltip('#myTooltip', {
  placement: 'top',
  trigger: 'hover focus',
  html: false,
  delay: { show: 500, hide: 100 },
  container: 'body'  // Recommended for complex DOM scenarios
});

// Methods
tooltip.show();
tooltip.hide();
tooltip.toggle();
tooltip.dispose();
tooltip.enable();       // Re-enable a disabled tooltip
tooltip.disable();      // Prevent tooltip from showing
tooltip.toggleEnabled(); // Toggle enabled state

// Update tooltip content dynamically after initialization
tooltip.setContent({ '.tooltip-inner': 'New tooltip text' });
```

**Key options:**

| Option | Default | Description |
|--------|---------|-------------|
| `container` | `false` | Appends tooltip to specific element. Use `'body'` to avoid rendering issues in input groups, button groups, or tables. |
| `selector` | `false` | Delegate tooltips to dynamically added elements. Example: `selector: '[data-bs-toggle="tooltip"]'` |
| `boundary` | `'clippingParents'` | Overflow constraint for positioning. Set to `document.body` if tooltip gets clipped. |

**Dynamic elements:** Use the `selector` option to handle tooltips on elements added after initialization:

```javascript
// Initialize once on a container, works for dynamically added elements
new bootstrap.Tooltip(document.body, {
  selector: '[data-bs-toggle="tooltip"]'
});
```

**Accessibility:** Tooltips are not announced to all screen readers—avoid putting essential information only in tooltips. Use tooltips for supplementary hints, not critical content. Ensure tooltips are keyboard-accessible (trigger element must be focusable).
