# Color Modes Utility Classes

Bootstrap 5.3 includes utility classes that adapt automatically to the active color mode (`data-bs-theme="light"` or `data-bs-theme="dark"`).

## Background Subtle Variants

Lighter background colors designed for contextual sections. Automatically adjust for dark mode.

| Class | Light Mode | Dark Mode |
|-------|------------|-----------|
| `bg-primary-subtle` | Light blue | Dark blue |
| `bg-secondary-subtle` | Light gray | Dark gray |
| `bg-success-subtle` | Light green | Dark green |
| `bg-danger-subtle` | Light red | Dark red |
| `bg-warning-subtle` | Light yellow | Dark yellow |
| `bg-info-subtle` | Light cyan | Dark cyan |
| `bg-light-subtle` | Near white | Dark gray |
| `bg-dark-subtle` | Light gray | Near black |

```html
<div class="bg-primary-subtle p-3">
  Adapts to light/dark mode automatically
</div>
```

## Text Emphasis Variants

Stronger text colors for emphasis. Automatically adjust for dark mode.

| Class | Description |
|-------|-------------|
| `text-primary-emphasis` | Darker primary in light mode, lighter in dark |
| `text-secondary-emphasis` | Darker secondary in light mode, lighter in dark |
| `text-success-emphasis` | Darker success in light mode, lighter in dark |
| `text-danger-emphasis` | Darker danger in light mode, lighter in dark |
| `text-warning-emphasis` | Darker warning in light mode, lighter in dark |
| `text-info-emphasis` | Darker info in light mode, lighter in dark |
| `text-light-emphasis` | Adjusted for contrast in both modes |
| `text-dark-emphasis` | Adjusted for contrast in both modes |

```html
<p class="text-primary-emphasis">
  High-contrast primary text in any color mode
</p>
```

## Border Subtle Variants

Softer border colors that adapt to color modes.

| Class | Description |
|-------|-------------|
| `border-primary-subtle` | Soft primary border |
| `border-secondary-subtle` | Soft secondary border |
| `border-success-subtle` | Soft success border |
| `border-danger-subtle` | Soft danger border |
| `border-warning-subtle` | Soft warning border |
| `border-info-subtle` | Soft info border |
| `border-light-subtle` | Soft light border |
| `border-dark-subtle` | Soft dark border |

```html
<div class="border border-primary-subtle rounded p-3">
  Subtle bordered container
</div>
```

## Usage Pattern

These variants are commonly combined for contextual UI elements:

```html
<!-- Alert-like contextual section -->
<div class="bg-warning-subtle border border-warning-subtle text-warning-emphasis p-3 rounded">
  This is a warning message that adapts to color modes.
</div>

<!-- Info callout -->
<div class="bg-info-subtle border border-info-subtle text-info-emphasis p-3 rounded">
  Helpful information displayed with contextual styling.
</div>
```

## Color Mode Behavior

- In **light mode**: subtle backgrounds are lighter tints, emphasis text is darker shades
- In **dark mode**: subtle backgrounds are darker shades, emphasis text is lighter tints
- Switching `data-bs-theme` on any parent element automatically updates all nested subtle/emphasis classes

## Sass Customization

Override subtle and emphasis colors per theme:

```scss
// Light mode overrides
$theme-colors-bg-subtle: map-merge($theme-colors-bg-subtle, (
  "primary": #cfe2ff
));

$theme-colors-text: map-merge($theme-colors-text, (
  "primary": #052c65
));

$theme-colors-border-subtle: map-merge($theme-colors-border-subtle, (
  "primary": #9ec5fe
));

// Dark mode overrides
$theme-colors-bg-subtle-dark: map-merge($theme-colors-bg-subtle-dark, (
  "primary": #031633
));

$theme-colors-text-dark: map-merge($theme-colors-text-dark, (
  "primary": #6ea8fe
));

$theme-colors-border-subtle-dark: map-merge($theme-colors-border-subtle-dark, (
  "primary": #084298
));
```
