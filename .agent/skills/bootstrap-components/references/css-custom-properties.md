# CSS Custom Properties Reference

Bootstrap 5.2+ provides CSS custom properties (variables) for runtime component customization without Sass compilation.

## Overview

CSS custom properties allow theming at three levels:

```css
/* Global - affects all components */
:root {
  --bs-primary: #0d6efd;
}

/* Component class - affects specific component type */
.modal {
  --bs-modal-bg: #f8f9fa;
}

/* Instance - affects single element */
<div class="card" style="--bs-card-bg: #fff3cd;">
```

## Accordion

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-accordion-color` | `var(--bs-body-color)` | Text color |
| `--bs-accordion-bg` | `var(--bs-body-bg)` | Background color |
| `--bs-accordion-transition` | Box shadow, bg, color | Transition properties |
| `--bs-accordion-border-color` | `var(--bs-border-color)` | Border color |
| `--bs-accordion-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-accordion-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-accordion-inner-border-radius` | Calculated | Inner item radius |
| `--bs-accordion-btn-padding-x` | `1.25rem` | Button horizontal padding |
| `--bs-accordion-btn-padding-y` | `1rem` | Button vertical padding |
| `--bs-accordion-btn-color` | `var(--bs-body-color)` | Button text color |
| `--bs-accordion-btn-bg` | `var(--bs-accordion-bg)` | Button background |
| `--bs-accordion-btn-icon` | URL-encoded SVG | Expand icon |
| `--bs-accordion-btn-icon-width` | `1.25rem` | Icon width |
| `--bs-accordion-btn-icon-transform` | `rotate(-180deg)` | Icon rotation when expanded |
| `--bs-accordion-btn-icon-transition` | `transform .2s ease-in-out` | Icon animation |
| `--bs-accordion-btn-active-icon` | URL-encoded SVG | Active state icon |
| `--bs-accordion-btn-focus-box-shadow` | `0 0 0 0.25rem rgba(...)` | Focus ring |
| `--bs-accordion-body-padding-x` | `1.25rem` | Body horizontal padding |
| `--bs-accordion-body-padding-y` | `1rem` | Body vertical padding |
| `--bs-accordion-active-color` | Primary emphasis | Active item text |
| `--bs-accordion-active-bg` | Primary subtle | Active item background |

**Example:**

```css
.accordion {
  --bs-accordion-active-bg: #e7f1ff;
  --bs-accordion-active-color: #0c63e4;
  --bs-accordion-btn-focus-box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}
```

## Alert

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-alert-bg` | `transparent` | Background color |
| `--bs-alert-padding-x` | `1rem` | Horizontal padding |
| `--bs-alert-padding-y` | `1rem` | Vertical padding |
| `--bs-alert-margin-bottom` | `1rem` | Bottom margin |
| `--bs-alert-color` | `inherit` | Text color |
| `--bs-alert-border-color` | `transparent` | Border color |
| `--bs-alert-border` | `var(--bs-border-width) solid` | Border shorthand |
| `--bs-alert-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-alert-link-color` | `inherit` | Link color |

**Example:**

```css
.alert-custom {
  --bs-alert-bg: #cfe2ff;
  --bs-alert-color: #084298;
  --bs-alert-border-color: #b6d4fe;
}
```

## Badge

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-badge-padding-x` | `0.65em` | Horizontal padding |
| `--bs-badge-padding-y` | `0.35em` | Vertical padding |
| `--bs-badge-font-size` | `0.75em` | Font size |
| `--bs-badge-font-weight` | `700` | Font weight |
| `--bs-badge-color` | `#fff` | Text color |
| `--bs-badge-border-radius` | `var(--bs-border-radius)` | Border radius |

## Breadcrumb

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-breadcrumb-padding-x` | `0` | Horizontal padding |
| `--bs-breadcrumb-padding-y` | `0` | Vertical padding |
| `--bs-breadcrumb-margin-bottom` | `1rem` | Bottom margin |
| `--bs-breadcrumb-bg` | None | Background |
| `--bs-breadcrumb-border-radius` | None | Border radius |
| `--bs-breadcrumb-divider-color` | `var(--bs-secondary-color)` | Divider color |
| `--bs-breadcrumb-item-padding-x` | `0.5rem` | Item spacing |
| `--bs-breadcrumb-item-active-color` | `var(--bs-secondary-color)` | Active item color |

**Example:**

```css
.breadcrumb {
  --bs-breadcrumb-divider: '>';
  --bs-breadcrumb-divider-color: #6c757d;
}
```

## Button

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-btn-padding-x` | `0.75rem` | Horizontal padding |
| `--bs-btn-padding-y` | `0.375rem` | Vertical padding |
| `--bs-btn-font-family` | None | Font family |
| `--bs-btn-font-size` | `1rem` | Font size |
| `--bs-btn-font-weight` | `400` | Font weight |
| `--bs-btn-line-height` | `1.5` | Line height |
| `--bs-btn-color` | `var(--bs-body-color)` | Text color |
| `--bs-btn-bg` | `transparent` | Background |
| `--bs-btn-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-btn-border-color` | `transparent` | Border color |
| `--bs-btn-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-btn-hover-border-color` | `transparent` | Hover border |
| `--bs-btn-box-shadow` | `inset 0 1px 0 rgba(...)` | Box shadow |
| `--bs-btn-disabled-opacity` | `0.65` | Disabled opacity |
| `--bs-btn-focus-box-shadow` | `0 0 0 0.25rem rgba(...)` | Focus ring |

**Example:**

```css
.btn-custom {
  --bs-btn-bg: #6f42c1;
  --bs-btn-color: #fff;
  --bs-btn-border-color: #6f42c1;
  --bs-btn-hover-bg: #59359a;
  --bs-btn-hover-border-color: #59359a;
  --bs-btn-active-bg: #4d2d85;
}
```

## Card

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-card-spacer-y` | `1rem` | Vertical spacing |
| `--bs-card-spacer-x` | `1rem` | Horizontal spacing |
| `--bs-card-title-spacer-y` | `0.5rem` | Title bottom margin |
| `--bs-card-title-color` | None | Title color |
| `--bs-card-subtitle-color` | None | Subtitle color |
| `--bs-card-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-card-border-color` | `var(--bs-border-color-translucent)` | Border color |
| `--bs-card-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-card-box-shadow` | None | Box shadow |
| `--bs-card-inner-border-radius` | Calculated | Inner radius |
| `--bs-card-cap-padding-y` | `0.5rem` | Header/footer vertical padding |
| `--bs-card-cap-padding-x` | `1rem` | Header/footer horizontal padding |
| `--bs-card-cap-bg` | `rgba(var(--bs-body-color-rgb), .03)` | Header/footer background |
| `--bs-card-cap-color` | None | Header/footer text color |
| `--bs-card-height` | None | Card height |
| `--bs-card-color` | None | Card text color |
| `--bs-card-bg` | `var(--bs-body-bg)` | Card background |
| `--bs-card-img-overlay-padding` | `1rem` | Image overlay padding |
| `--bs-card-group-margin` | `0.75rem` | Card group spacing |

**Example:**

```css
.card {
  --bs-card-bg: #f8f9fa;
  --bs-card-border-color: #dee2e6;
  --bs-card-cap-bg: #e9ecef;
}
```

## Carousel

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-carousel-control-color` | `#fff` | Control arrow color |
| `--bs-carousel-control-width` | `15%` | Control width |
| `--bs-carousel-control-opacity` | `0.5` | Control opacity |
| `--bs-carousel-control-hover-opacity` | `0.9` | Hover opacity |
| `--bs-carousel-control-transition` | `opacity .15s ease` | Control transition |
| `--bs-carousel-indicator-width` | `30px` | Indicator width |
| `--bs-carousel-indicator-height` | `3px` | Indicator height |
| `--bs-carousel-indicator-hit-area-height` | `10px` | Click area height |
| `--bs-carousel-indicator-spacer` | `3px` | Indicator spacing |
| `--bs-carousel-indicator-opacity` | `0.5` | Indicator opacity |
| `--bs-carousel-indicator-active-bg` | `#fff` | Active indicator color |
| `--bs-carousel-indicator-active-opacity` | `1` | Active indicator opacity |
| `--bs-carousel-indicator-transition` | `opacity .6s ease` | Indicator transition |
| `--bs-carousel-caption-width` | `70%` | Caption width |
| `--bs-carousel-caption-color` | `#fff` | Caption text color |
| `--bs-carousel-caption-padding-y` | `1.25rem` | Caption vertical padding |
| `--bs-carousel-transition-duration` | `0.6s` | Slide transition time |

## Close Button

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-btn-close-color` | `#000` | Icon color |
| `--bs-btn-close-bg` | URL-encoded SVG | Background icon |
| `--bs-btn-close-opacity` | `0.5` | Default opacity |
| `--bs-btn-close-hover-opacity` | `0.75` | Hover opacity |
| `--bs-btn-close-focus-shadow` | `0 0 0 0.25rem rgba(...)` | Focus ring |
| `--bs-btn-close-focus-opacity` | `1` | Focus opacity |
| `--bs-btn-close-disabled-opacity` | `0.25` | Disabled opacity |
| `--bs-btn-close-white-filter` | `invert(1) grayscale(100%) brightness(200%)` | White variant filter |

## Dropdown

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-dropdown-zindex` | `1000` | Stack order |
| `--bs-dropdown-min-width` | `10rem` | Minimum width |
| `--bs-dropdown-padding-x` | `0` | Horizontal padding |
| `--bs-dropdown-padding-y` | `0.5rem` | Vertical padding |
| `--bs-dropdown-spacer` | `0.125rem` | Gap from toggle |
| `--bs-dropdown-font-size` | `1rem` | Font size |
| `--bs-dropdown-color` | `var(--bs-body-color)` | Text color |
| `--bs-dropdown-bg` | `var(--bs-body-bg)` | Background |
| `--bs-dropdown-border-color` | `var(--bs-border-color-translucent)` | Border color |
| `--bs-dropdown-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-dropdown-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-dropdown-inner-border-radius` | Calculated | Inner radius |
| `--bs-dropdown-divider-bg` | `var(--bs-border-color-translucent)` | Divider color |
| `--bs-dropdown-divider-margin-y` | `0.5rem` | Divider spacing |
| `--bs-dropdown-box-shadow` | `0 0.5rem 1rem rgba(...)` | Shadow |
| `--bs-dropdown-link-color` | `var(--bs-body-color)` | Link color |
| `--bs-dropdown-link-hover-color` | Darkened | Link hover color |
| `--bs-dropdown-link-hover-bg` | `var(--bs-tertiary-bg)` | Link hover bg |
| `--bs-dropdown-link-active-color` | `#fff` | Active link color |
| `--bs-dropdown-link-active-bg` | `var(--bs-primary)` | Active link bg |
| `--bs-dropdown-link-disabled-color` | `var(--bs-tertiary-color)` | Disabled color |
| `--bs-dropdown-item-padding-x` | `1rem` | Item horizontal padding |
| `--bs-dropdown-item-padding-y` | `0.25rem` | Item vertical padding |
| `--bs-dropdown-header-color` | `var(--bs-secondary-color)` | Header text |
| `--bs-dropdown-header-padding-x` | `1rem` | Header padding |
| `--bs-dropdown-header-padding-y` | `0.5rem` | Header padding |

**Example:**

```css
.dropdown-menu {
  --bs-dropdown-bg: #212529;
  --bs-dropdown-color: #f8f9fa;
  --bs-dropdown-link-color: #f8f9fa;
  --bs-dropdown-link-hover-bg: #495057;
}
```

## List Group

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-list-group-color` | `var(--bs-body-color)` | Text color |
| `--bs-list-group-bg` | `var(--bs-body-bg)` | Background |
| `--bs-list-group-border-color` | `var(--bs-border-color)` | Border color |
| `--bs-list-group-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-list-group-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-list-group-item-padding-x` | `1rem` | Horizontal padding |
| `--bs-list-group-item-padding-y` | `0.5rem` | Vertical padding |
| `--bs-list-group-action-color` | `var(--bs-secondary-color)` | Action link color |
| `--bs-list-group-action-hover-color` | `var(--bs-emphasis-color)` | Hover color |
| `--bs-list-group-action-hover-bg` | `var(--bs-tertiary-bg)` | Hover background |
| `--bs-list-group-action-active-color` | `var(--bs-body-color)` | Active text |
| `--bs-list-group-action-active-bg` | `var(--bs-secondary-bg)` | Active background |
| `--bs-list-group-disabled-color` | `var(--bs-secondary-color)` | Disabled text |
| `--bs-list-group-disabled-bg` | `var(--bs-body-bg)` | Disabled background |
| `--bs-list-group-active-color` | `#fff` | Selected text |
| `--bs-list-group-active-bg` | `var(--bs-primary)` | Selected background |
| `--bs-list-group-active-border-color` | `var(--bs-primary)` | Selected border |

## Modal

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-modal-zindex` | `1055` | Stack order |
| `--bs-modal-width` | `500px` | Default width |
| `--bs-modal-padding` | `1rem` | Content padding |
| `--bs-modal-margin` | `0.5rem` | Modal margin |
| `--bs-modal-color` | None | Text color |
| `--bs-modal-bg` | `var(--bs-body-bg)` | Background |
| `--bs-modal-border-color` | `var(--bs-border-color-translucent)` | Border color |
| `--bs-modal-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-modal-border-radius` | `var(--bs-border-radius-lg)` | Border radius |
| `--bs-modal-box-shadow` | `0 0.125rem 0.25rem rgba(...)` | Shadow |
| `--bs-modal-inner-border-radius` | Calculated | Inner radius |
| `--bs-modal-header-padding-x` | `1rem` | Header horizontal padding |
| `--bs-modal-header-padding-y` | `1rem` | Header vertical padding |
| `--bs-modal-header-padding` | Shorthand | Header padding |
| `--bs-modal-header-border-color` | `var(--bs-border-color)` | Header border |
| `--bs-modal-header-border-width` | `var(--bs-border-width)` | Header border width |
| `--bs-modal-title-line-height` | `1.5` | Title line height |
| `--bs-modal-footer-gap` | `0.5rem` | Footer button gap |
| `--bs-modal-footer-bg` | None | Footer background |
| `--bs-modal-footer-border-color` | `var(--bs-border-color)` | Footer border |
| `--bs-modal-footer-border-width` | `var(--bs-border-width)` | Footer border width |

**Example:**

```css
.modal {
  --bs-modal-width: 600px;
  --bs-modal-bg: #f8f9fa;
  --bs-modal-border-radius: 1rem;
}
```

## Nav and Tabs

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-nav-link-padding-x` | `1rem` | Link horizontal padding |
| `--bs-nav-link-padding-y` | `0.5rem` | Link vertical padding |
| `--bs-nav-link-font-size` | None | Link font size |
| `--bs-nav-link-font-weight` | None | Link font weight |
| `--bs-nav-link-color` | `var(--bs-link-color)` | Link color |
| `--bs-nav-link-hover-color` | `var(--bs-link-hover-color)` | Hover color |
| `--bs-nav-link-disabled-color` | `var(--bs-secondary-color)` | Disabled color |
| `--bs-nav-tabs-border-width` | `var(--bs-border-width)` | Tabs border width |
| `--bs-nav-tabs-border-color` | `var(--bs-border-color)` | Tabs border color |
| `--bs-nav-tabs-border-radius` | `var(--bs-border-radius)` | Tabs border radius |
| `--bs-nav-tabs-link-hover-border-color` | `var(--bs-secondary-bg)` | Hover border |
| `--bs-nav-tabs-link-active-color` | `var(--bs-emphasis-color)` | Active text |
| `--bs-nav-tabs-link-active-bg` | `var(--bs-body-bg)` | Active background |
| `--bs-nav-tabs-link-active-border-color` | `var(--bs-border-color)` | Active border |
| `--bs-nav-pills-border-radius` | `var(--bs-border-radius)` | Pills radius |
| `--bs-nav-pills-link-active-color` | `#fff` | Active pill text |
| `--bs-nav-pills-link-active-bg` | `var(--bs-primary)` | Active pill bg |
| `--bs-nav-underline-gap` | `1rem` | Underline nav spacing |
| `--bs-nav-underline-border-width` | `0.125rem` | Underline width |
| `--bs-nav-underline-link-active-color` | `var(--bs-emphasis-color)` | Active color |

## Navbar

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-navbar-padding-x` | `0` | Horizontal padding |
| `--bs-navbar-padding-y` | `0.5rem` | Vertical padding |
| `--bs-navbar-color` | `rgba(var(--bs-emphasis-color-rgb), 0.65)` | Link color |
| `--bs-navbar-hover-color` | `rgba(var(--bs-emphasis-color-rgb), 0.8)` | Hover color |
| `--bs-navbar-disabled-color` | `rgba(var(--bs-emphasis-color-rgb), 0.3)` | Disabled color |
| `--bs-navbar-active-color` | `rgba(var(--bs-emphasis-color-rgb), 1)` | Active color |
| `--bs-navbar-brand-padding-y` | `0.3125rem` | Brand vertical padding |
| `--bs-navbar-brand-margin-end` | `1rem` | Brand right margin |
| `--bs-navbar-brand-font-size` | `1.25rem` | Brand font size |
| `--bs-navbar-brand-color` | `var(--bs-navbar-active-color)` | Brand color |
| `--bs-navbar-brand-hover-color` | `var(--bs-navbar-active-color)` | Brand hover |
| `--bs-navbar-nav-link-padding-x` | `0.5rem` | Nav link padding |
| `--bs-navbar-toggler-padding-y` | `0.25rem` | Toggler vertical padding |
| `--bs-navbar-toggler-padding-x` | `0.75rem` | Toggler horizontal padding |
| `--bs-navbar-toggler-font-size` | `1.25rem` | Toggler size |
| `--bs-navbar-toggler-icon-bg` | URL-encoded SVG | Toggler icon |
| `--bs-navbar-toggler-border-color` | `rgba(var(--bs-emphasis-color-rgb), 0.15)` | Toggler border |
| `--bs-navbar-toggler-border-radius` | `var(--bs-border-radius)` | Toggler radius |
| `--bs-navbar-toggler-focus-width` | `0.25rem` | Focus ring width |
| `--bs-navbar-toggler-transition` | `box-shadow .15s ease-in-out` | Toggler transition |

## Offcanvas

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-offcanvas-zindex` | `1045` | Stack order |
| `--bs-offcanvas-width` | `400px` | Panel width |
| `--bs-offcanvas-height` | `30vh` | Panel height (top/bottom) |
| `--bs-offcanvas-padding-x` | `1rem` | Horizontal padding |
| `--bs-offcanvas-padding-y` | `1rem` | Vertical padding |
| `--bs-offcanvas-color` | `var(--bs-body-color)` | Text color |
| `--bs-offcanvas-bg` | `var(--bs-body-bg)` | Background |
| `--bs-offcanvas-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-offcanvas-border-color` | `var(--bs-border-color-translucent)` | Border color |
| `--bs-offcanvas-box-shadow` | `0 0.125rem 0.25rem rgba(...)` | Shadow |
| `--bs-offcanvas-transition` | `transform .3s ease-in-out` | Slide animation |
| `--bs-offcanvas-title-line-height` | `1.5` | Title line height |

**Example:**

```css
.offcanvas {
  --bs-offcanvas-width: 350px;
  --bs-offcanvas-bg: #212529;
  --bs-offcanvas-color: #f8f9fa;
}
```

## Pagination

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-pagination-padding-x` | `0.75rem` | Horizontal padding |
| `--bs-pagination-padding-y` | `0.375rem` | Vertical padding |
| `--bs-pagination-font-size` | `1rem` | Font size |
| `--bs-pagination-color` | `var(--bs-link-color)` | Link color |
| `--bs-pagination-bg` | `var(--bs-body-bg)` | Background |
| `--bs-pagination-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-pagination-border-color` | `var(--bs-border-color)` | Border color |
| `--bs-pagination-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-pagination-hover-color` | `var(--bs-link-hover-color)` | Hover text |
| `--bs-pagination-hover-bg` | `var(--bs-tertiary-bg)` | Hover background |
| `--bs-pagination-hover-border-color` | `var(--bs-border-color)` | Hover border |
| `--bs-pagination-focus-color` | `var(--bs-link-hover-color)` | Focus text |
| `--bs-pagination-focus-bg` | `var(--bs-secondary-bg)` | Focus background |
| `--bs-pagination-focus-box-shadow` | `0 0 0 0.25rem rgba(...)` | Focus ring |
| `--bs-pagination-active-color` | `#fff` | Active text |
| `--bs-pagination-active-bg` | `var(--bs-primary)` | Active background |
| `--bs-pagination-active-border-color` | `var(--bs-primary)` | Active border |
| `--bs-pagination-disabled-color` | `var(--bs-secondary-color)` | Disabled text |
| `--bs-pagination-disabled-bg` | `var(--bs-secondary-bg)` | Disabled background |
| `--bs-pagination-disabled-border-color` | `var(--bs-border-color)` | Disabled border |

## Popover

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-popover-zindex` | `1070` | Stack order |
| `--bs-popover-max-width` | `276px` | Maximum width |
| `--bs-popover-font-size` | `0.875rem` | Font size |
| `--bs-popover-bg` | `var(--bs-body-bg)` | Background |
| `--bs-popover-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-popover-border-color` | `var(--bs-border-color-translucent)` | Border color |
| `--bs-popover-border-radius` | `var(--bs-border-radius-lg)` | Border radius |
| `--bs-popover-inner-border-radius` | Calculated | Inner radius |
| `--bs-popover-box-shadow` | `0 0.5rem 1rem rgba(...)` | Shadow |
| `--bs-popover-header-padding-x` | `1rem` | Header horizontal padding |
| `--bs-popover-header-padding-y` | `0.5rem` | Header vertical padding |
| `--bs-popover-header-font-size` | `1rem` | Header font size |
| `--bs-popover-header-color` | None | Header text color |
| `--bs-popover-header-bg` | `var(--bs-secondary-bg)` | Header background |
| `--bs-popover-body-padding-x` | `1rem` | Body horizontal padding |
| `--bs-popover-body-padding-y` | `1rem` | Body vertical padding |
| `--bs-popover-body-color` | `var(--bs-body-color)` | Body text color |
| `--bs-popover-arrow-width` | `1rem` | Arrow width |
| `--bs-popover-arrow-height` | `0.5rem` | Arrow height |
| `--bs-popover-arrow-border` | `var(--bs-popover-border-color)` | Arrow border |

## Progress

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-progress-height` | `1rem` | Bar height |
| `--bs-progress-font-size` | `0.75rem` | Label font size |
| `--bs-progress-bg` | `var(--bs-secondary-bg)` | Track background |
| `--bs-progress-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-progress-box-shadow` | `inset 0 1px 2px rgba(...)` | Inner shadow |
| `--bs-progress-bar-color` | `#fff` | Bar text color |
| `--bs-progress-bar-bg` | `var(--bs-primary)` | Bar background |
| `--bs-progress-bar-transition` | `width .6s ease` | Animation |

**Example:**

```css
.progress {
  --bs-progress-height: 1.5rem;
  --bs-progress-bar-bg: #198754;
}
```

## Spinner

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-spinner-width` | `2rem` | Size |
| `--bs-spinner-height` | `2rem` | Size |
| `--bs-spinner-vertical-align` | `-0.125em` | Alignment |
| `--bs-spinner-border-width` | `0.25em` | Border spinner width |
| `--bs-spinner-animation-speed` | `0.75s` | Animation duration |
| `--bs-spinner-animation-name` | `spinner-border` | Animation keyframes |

## Table

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-table-color-type` | Initial | Color type |
| `--bs-table-bg-type` | Initial | Background type |
| `--bs-table-color-state` | Initial | Color state |
| `--bs-table-bg-state` | Initial | Background state |
| `--bs-table-color` | `var(--bs-emphasis-color)` | Text color |
| `--bs-table-bg` | `var(--bs-body-bg)` | Background |
| `--bs-table-border-color` | `var(--bs-border-color)` | Border color |
| `--bs-table-accent-bg` | `transparent` | Striped/hover bg |
| `--bs-table-striped-color` | `var(--bs-table-color)` | Striped text |
| `--bs-table-striped-bg` | `rgba(var(--bs-emphasis-color-rgb), 0.05)` | Striped background |
| `--bs-table-active-color` | `var(--bs-table-color)` | Active text |
| `--bs-table-active-bg` | `rgba(var(--bs-emphasis-color-rgb), 0.1)` | Active background |
| `--bs-table-hover-color` | `var(--bs-table-color)` | Hover text |
| `--bs-table-hover-bg` | `rgba(var(--bs-emphasis-color-rgb), 0.075)` | Hover background |

## Toast

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-toast-zindex` | `1090` | Stack order |
| `--bs-toast-padding-x` | `0.75rem` | Horizontal padding |
| `--bs-toast-padding-y` | `0.5rem` | Vertical padding |
| `--bs-toast-spacing` | `1.5rem` | Container spacing |
| `--bs-toast-max-width` | `350px` | Maximum width |
| `--bs-toast-font-size` | `0.875rem` | Font size |
| `--bs-toast-color` | None | Text color |
| `--bs-toast-bg` | `rgba(var(--bs-body-bg-rgb), 0.85)` | Background |
| `--bs-toast-border-width` | `var(--bs-border-width)` | Border width |
| `--bs-toast-border-color` | `var(--bs-border-color-translucent)` | Border color |
| `--bs-toast-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-toast-box-shadow` | `0 0.5rem 1rem rgba(...)` | Shadow |
| `--bs-toast-header-color` | `var(--bs-secondary-color)` | Header text |
| `--bs-toast-header-bg` | `rgba(var(--bs-body-bg-rgb), 0.85)` | Header background |
| `--bs-toast-header-border-color` | `var(--bs-border-color-translucent)` | Header border |

**Example:**

```css
.toast {
  --bs-toast-bg: rgba(33, 37, 41, 0.95);
  --bs-toast-color: #f8f9fa;
  --bs-toast-border-color: rgba(255, 255, 255, 0.1);
}
```

## Tooltip

| Variable | Default | Description |
|----------|---------|-------------|
| `--bs-tooltip-zindex` | `1080` | Stack order |
| `--bs-tooltip-max-width` | `200px` | Maximum width |
| `--bs-tooltip-padding-x` | `0.5rem` | Horizontal padding |
| `--bs-tooltip-padding-y` | `0.25rem` | Vertical padding |
| `--bs-tooltip-margin` | Deprecated | Use offset option |
| `--bs-tooltip-font-size` | `0.875rem` | Font size |
| `--bs-tooltip-color` | `var(--bs-body-bg)` | Text color |
| `--bs-tooltip-bg` | `var(--bs-emphasis-color)` | Background |
| `--bs-tooltip-border-radius` | `var(--bs-border-radius)` | Border radius |
| `--bs-tooltip-opacity` | `0.9` | Opacity |
| `--bs-tooltip-arrow-width` | `0.8rem` | Arrow width |
| `--bs-tooltip-arrow-height` | `0.4rem` | Arrow height |

## Dark Mode Considerations

CSS variables automatically adapt to dark mode when using `data-bs-theme="dark"`:

```html
<html data-bs-theme="dark">
  <!-- All component variables use dark mode values -->
</html>
```

Override for specific components in dark mode:

```css
[data-bs-theme="dark"] .card {
  --bs-card-bg: #1a1d20;
  --bs-card-border-color: #495057;
}
```

## Resources

- [Bootstrap CSS Variables Documentation](https://getbootstrap.com/docs/5.3/customize/css-variables/)
- [Bootstrap Sass Variables](https://getbootstrap.com/docs/5.3/customize/sass/)
- [Color Modes](https://getbootstrap.com/docs/5.3/customize/color-modes/)
