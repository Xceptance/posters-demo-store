# Utilities API Customization

Advanced Sass customization examples for Bootstrap 5.3 Utilities API.

## Setup

Always import Bootstrap's required files before customizing utilities:

```scss
@import "bootstrap/scss/functions";
@import "bootstrap/scss/variables";
@import "bootstrap/scss/variables-dark";
@import "bootstrap/scss/maps";
@import "bootstrap/scss/mixins";
@import "bootstrap/scss/utilities";

// Your customizations here

@import "bootstrap/scss/utilities/api";
```

## Modifying Existing Utilities

### Enable Responsive Variants

Many utilities don't generate responsive classes by default. Enable them with `map-merge`:

```scss
// Make overflow responsive
$utilities: map-merge(
  $utilities,
  (
    "overflow": map-merge(
      map-get($utilities, "overflow"),
      (responsive: true)
    )
  )
);

// Result: overflow-auto, overflow-md-auto, overflow-lg-hidden, etc.
```

### Enable Print Variants

```scss
// Make visibility printable
$utilities: map-merge(
  $utilities,
  (
    "visibility": map-merge(
      map-get($utilities, "visibility"),
      (print: true)
    )
  )
);

// Result: visible, invisible, visible-print, invisible-print
```

## Adding Values to Existing Utilities

### Extend Width Utilities

```scss
// Add w-10 and w-33 to existing width utilities
$utilities: map-merge(
  $utilities,
  (
    "width": map-merge(
      map-get($utilities, "width"),
      (
        values: map-merge(
          map-get(map-get($utilities, "width"), "values"),
          (10: 10%, 33: 33.333333%)
        )
      )
    )
  )
);

// Result: w-10, w-25, w-33, w-50, w-75, w-100, w-auto
```

### Add Custom Spacing Values

```scss
// Add size 6 (4rem) to spacing scale
$spacer: 1rem;
$spacers: map-merge(
  $spacers,
  (6: $spacer * 4)
);

// Result: m-6, p-6, mt-6, pb-6, etc.
```

## Renaming Utility Classes

### Change Class Prefix

```scss
// Rename ms-* (margin-start) to ml-* (margin-left)
$utilities: map-merge(
  $utilities,
  (
    "margin-start": map-merge(
      map-get($utilities, "margin-start"),
      (class: ml)
    )
  )
);

// Result: ml-0, ml-1, ml-2, ml-3, ml-4, ml-5, ml-auto
```

### Rename Multiple Logical Properties

```scss
// For projects preferring physical properties over logical
$utilities: map-merge(
  $utilities,
  (
    "margin-start": map-merge(
      map-get($utilities, "margin-start"),
      (class: ml)
    ),
    "margin-end": map-merge(
      map-get($utilities, "margin-end"),
      (class: mr)
    ),
    "padding-start": map-merge(
      map-get($utilities, "padding-start"),
      (class: pl)
    ),
    "padding-end": map-merge(
      map-get($utilities, "padding-end"),
      (class: pr)
    )
  )
);
```

## State Variants

### Add Hover States

```scss
// Create hover variants for opacity
$utilities: map-merge(
  $utilities,
  (
    "opacity": map-merge(
      map-get($utilities, "opacity"),
      (state: hover)
    )
  )
);

// Result: opacity-50, opacity-50-hover (applies on :hover)
```

### Multiple State Variants

```scss
// Add hover and focus states to background colors
$utilities: map-merge(
  $utilities,
  (
    "background-color": map-merge(
      map-get($utilities, "background-color"),
      (state: hover focus)
    )
  )
);

// Result: bg-primary, bg-primary-hover, bg-primary-focus
```

## RTL Configuration

### Exclude from RTL

```scss
// Don't flip this utility in RTL layouts
$utilities: map-merge(
  $utilities,
  (
    "text-align": map-merge(
      map-get($utilities, "text-align"),
      (rtl: false)
    )
  )
);
```

## CSS Variables Output

### Generate as CSS Variables

```scss
// Output as CSS variables instead of direct properties
$utilities: map-merge(
  $utilities,
  (
    "opacity": map-merge(
      map-get($utilities, "opacity"),
      (
        css-var: true,
        css-variable-name: opacity
      )
    )
  )
);

// Result: --bs-opacity: 0.5; instead of opacity: 0.5;
```

### Local CSS Variables

```scss
// Add local CSS variables to utility
$utilities: map-merge(
  $utilities,
  (
    "background-color": map-merge(
      map-get($utilities, "background-color"),
      (
        local-vars: (
          "bg-opacity": 1
        )
      )
    )
  )
);
```

## Creating Custom Utilities

### Basic Custom Utility

```scss
$utilities: map-merge(
  $utilities,
  (
    "cursor": (
      property: cursor,
      class: cursor,
      values: auto pointer grab grabbing not-allowed wait
    )
  )
);

// Result: cursor-auto, cursor-pointer, cursor-grab, etc.
```

### Responsive Custom Utility

```scss
$utilities: map-merge(
  $utilities,
  (
    "cursor": (
      property: cursor,
      class: cursor,
      responsive: true,
      values: auto pointer grab
    )
  )
);

// Result: cursor-pointer, cursor-md-grab, cursor-lg-auto, etc.
```

### Custom Utility with Map Values

```scss
$utilities: map-merge(
  $utilities,
  (
    "rotate": (
      property: transform,
      class: rotate,
      values: (
        0: rotate(0deg),
        45: rotate(45deg),
        90: rotate(90deg),
        180: rotate(180deg),
        270: rotate(270deg)
      )
    )
  )
);

// Result: rotate-0, rotate-45, rotate-90, rotate-180, rotate-270
```

## Removing Utilities

### Remove Entirely

```scss
// Remove float utilities
$utilities: map-remove($utilities, "float");
```

### Set to Null

```scss
// Alternative: set to null
$utilities: map-merge(
  $utilities,
  (
    "float": null
  )
);
```

## Negative Margins

By default, Bootstrap disables negative margin utilities. Enable them via the `$enable-negative-margins` Sass variable:

```scss
// Before importing Bootstrap
$enable-negative-margins: true;
```

This generates negative margin classes using the `n` prefix:

| Class | Value |
|-------|-------|
| `mt-n1` | margin-top: -0.25rem |
| `mt-n2` | margin-top: -0.5rem |
| `mt-n3` | margin-top: -1rem |
| `mt-n4` | margin-top: -1.5rem |
| `mt-n5` | margin-top: -3rem |

All sides are supported: `m-n{size}`, `mt-n{size}`, `mb-n{size}`, `ms-n{size}`, `me-n{size}`, `mx-n{size}`, `my-n{size}`.

Responsive variants are also generated: `mt-md-n3`, `ms-lg-n2`, etc.

## Global Configuration

### Disable !important

```scss
// Before importing Bootstrap
$enable-important-utilities: false;

// All utilities will now omit !important
```

### Configure RFS (Responsive Font Sizing)

```scss
// Enable fluid rescaling for a utility
$utilities: map-merge(
  $utilities,
  (
    "font-size": map-merge(
      map-get($utilities, "font-size"),
      (rfs: true)
    )
  )
);
```

## Complete Example

```scss
// _utilities-custom.scss
@import "bootstrap/scss/functions";
@import "bootstrap/scss/variables";
@import "bootstrap/scss/variables-dark";
@import "bootstrap/scss/maps";
@import "bootstrap/scss/mixins";
@import "bootstrap/scss/utilities";

// Extend spacing scale
$spacers: map-merge($spacers, (6: 4rem, 7: 5rem));

// Custom utilities
$utilities: map-merge(
  $utilities,
  (
    // Add cursor utility
    "cursor": (
      property: cursor,
      class: cursor,
      values: auto pointer grab grabbing not-allowed
    ),
    // Make overflow responsive
    "overflow": map-merge(
      map-get($utilities, "overflow"),
      (responsive: true)
    ),
    // Add hover state to opacity
    "opacity": map-merge(
      map-get($utilities, "opacity"),
      (state: hover)
    ),
    // Remove float (not commonly needed)
    "float": null
  )
);

@import "bootstrap/scss/utilities/api";
```
