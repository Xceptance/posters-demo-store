# Sass Customization for Bootstrap Layout

Bootstrap's layout system is fully customizable through Sass variables and mixins. This reference covers the tools available when compiling Bootstrap from source.

## Sass Variables

### Grid System Variables

```scss
// Core grid configuration
$grid-columns: 12;                // Number of columns in the grid
$grid-gutter-width: 1.5rem;       // Gap between columns (24px default)
$grid-row-columns: 6;             // Maximum row-cols-* value

// Enable/disable CSS Grid
$enable-cssgrid: false;           // Set to true to enable CSS Grid classes
```

### Breakpoint Variables

```scss
// Default breakpoint map
$grid-breakpoints: (
  xs: 0,
  sm: 576px,
  md: 768px,
  lg: 992px,
  xl: 1200px,
  xxl: 1400px
);
```

### Container Variables

```scss
// Maximum container widths at each breakpoint
$container-max-widths: (
  sm: 540px,
  md: 720px,
  lg: 960px,
  xl: 1140px,
  xxl: 1320px
);

// Container padding
$container-padding-x: $grid-gutter-width;
```

### Gutter Variables

```scss
// Gutter utility classes use the spacers map
$spacers: (
  0: 0,
  1: $spacer * .25,    // 0.25rem = 4px
  2: $spacer * .5,     // 0.5rem = 8px
  3: $spacer,          // 1rem = 16px
  4: $spacer * 1.5,    // 1.5rem = 24px
  5: $spacer * 3       // 3rem = 48px
);

// Gutter classes (g-*, gx-*, gy-*) map to these values
$gutters: $spacers;
```

## Media Query Mixins

Bootstrap provides mixins for writing responsive styles that align with the breakpoint system.

### media-breakpoint-up

Apply styles at a breakpoint and above (mobile-first):

```scss
// Styles apply at md (768px) and wider
@include media-breakpoint-up(md) {
  .sidebar {
    display: block;
    width: 250px;
  }
}

// Styles apply at lg (992px) and wider
@include media-breakpoint-up(lg) {
  .container-custom {
    max-width: 1000px;
  }
}
```

### media-breakpoint-down

Apply styles below a breakpoint:

```scss
// Styles apply below md (up to 767.98px)
@include media-breakpoint-down(md) {
  .mobile-nav {
    display: block;
  }
}

// Hide element on small screens
@include media-breakpoint-down(sm) {
  .desktop-only {
    display: none;
  }
}
```

### media-breakpoint-between

Apply styles between two breakpoints:

```scss
// Styles apply only between sm and lg (576px to 991.98px)
@include media-breakpoint-between(sm, lg) {
  .tablet-layout {
    padding: 1rem 2rem;
  }
}
```

### media-breakpoint-only

Apply styles at exactly one breakpoint:

```scss
// Styles apply only at md (768px to 991.98px)
@include media-breakpoint-only(md) {
  .md-specific {
    font-size: 1.125rem;
  }
}
```

## Grid Mixins

Bootstrap provides mixins for creating semantic grid layouts without adding grid classes to your HTML.

### make-row

Creates a row with proper flexbox and negative margins:

```scss
.article-list {
  @include make-row();
}
```

### make-col-ready

Prepares an element to be a grid column (padding, width basics):

```scss
.article-item {
  @include make-col-ready();
}
```

### make-col

Sets column width (number of columns out of total):

```scss
// Fixed 6-column width (50% of 12-column grid)
.article-item {
  @include make-col-ready();
  @include make-col(6);
}

// Responsive column widths
.sidebar {
  @include make-col-ready();
  @include make-col(12);  // Full width by default

  @include media-breakpoint-up(md) {
    @include make-col(4);  // 1/3 width on md+
  }

  @include media-breakpoint-up(lg) {
    @include make-col(3);  // 1/4 width on lg+
  }
}
```

### make-col-auto

Sets column to auto width based on content:

```scss
.tag {
  @include make-col-ready();
  @include make-col-auto();
}
```

### make-col-offset

Adds left margin to offset a column:

```scss
.centered-content {
  @include make-col-ready();
  @include make-col(6);
  @include make-col-offset(3);  // Centered in 12-column grid
}
```

## Container Mixins

### make-container

Creates a container with responsive max-widths:

```scss
.my-container {
  @include make-container();
}

// Custom padding
.wide-container {
  @include make-container($padding-x: 2rem);
}
```

## Customizing the Grid

### Changing Column Count

To use a different number of columns (e.g., 16 or 24):

```scss
// Before importing Bootstrap
$grid-columns: 16;

// Now .col-8 is 50% width instead of 66.67%
```

### Adding Custom Breakpoints

Add or modify breakpoints before importing Bootstrap:

```scss
$grid-breakpoints: (
  xs: 0,
  sm: 576px,
  md: 768px,
  lg: 992px,
  xl: 1200px,
  xxl: 1400px,
  xxxl: 1600px  // Custom breakpoint
);

// Also update container max-widths
$container-max-widths: (
  sm: 540px,
  md: 720px,
  lg: 960px,
  xl: 1140px,
  xxl: 1320px,
  xxxl: 1500px  // Container width for xxxl
);
```

### Removing Breakpoints

Remove breakpoints you don't need:

```scss
$grid-breakpoints: (
  xs: 0,
  sm: 576px,
  lg: 992px,
  xl: 1200px
  // Removed md and xxl
);

$container-max-widths: (
  sm: 540px,
  lg: 960px,
  xl: 1140px
);
```

### Customizing Gutters

Change the default gutter width:

```scss
// Smaller gutters (16px instead of 24px)
$grid-gutter-width: 1rem;

// Larger gutters (32px)
$grid-gutter-width: 2rem;
```

## Enabling CSS Grid

Bootstrap's CSS Grid support is disabled by default:

```scss
// Enable CSS Grid classes (.grid, .g-col-*, etc.)
$enable-cssgrid: true;

@import "bootstrap";
```

Once enabled, you can use CSS Grid classes and customize its variables:

```scss
// CSS Grid specific variables
$grid-columns: 12;      // Used by both flexbox and CSS Grid
$grid-gutter-width: 1.5rem;

// CSS Grid gap utilities use the same $gutters map
```

## Import Order

When customizing, always set variables before importing Bootstrap:

```scss
// 1. Include functions first (required for variable manipulation)
@import "bootstrap/scss/functions";

// 2. Override default variables
$grid-columns: 16;
$grid-gutter-width: 1rem;
$enable-cssgrid: true;

// 3. Include remainder of Bootstrap
@import "bootstrap/scss/variables";
@import "bootstrap/scss/variables-dark";
@import "bootstrap/scss/maps";
@import "bootstrap/scss/mixins";
@import "bootstrap/scss/grid";
// ... other imports as needed
```

## Related Resources

- See `examples/sass-customization.scss` for complete examples
- See `references/grid-reference.md` for utility class reference
- Bootstrap docs: [Grid Sass](https://getbootstrap.com/docs/5.3/layout/grid/#sass)
- Bootstrap docs: [Breakpoints Sass](https://getbootstrap.com/docs/5.3/layout/breakpoints/#sass)
