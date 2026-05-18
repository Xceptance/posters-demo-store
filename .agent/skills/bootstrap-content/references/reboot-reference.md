# Reboot Reference

Complete reference for Bootstrap 5.3 Reboot - the normalized baseline styles that provide cross-browser consistency.

## CSS Variables

Bootstrap Reboot sets CSS custom properties on the `:root` element:

### Body Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `--bs-body-font-family` | Body font family | System font stack |
| `--bs-body-font-size` | Body font size | `1rem` |
| `--bs-body-font-weight` | Body font weight | `400` |
| `--bs-body-line-height` | Body line height | `1.5` |
| `--bs-body-color` | Body text color | `#212529` |
| `--bs-body-bg` | Body background | `#fff` |

### Emphasis Variables

| Variable | Description | Light Mode | Dark Mode |
|----------|-------------|------------|-----------|
| `--bs-emphasis-color` | High emphasis text | `#000` | `#fff` |
| `--bs-secondary-color` | Secondary text | `rgba(33, 37, 41, 0.75)` | `rgba(222, 226, 230, 0.75)` |
| `--bs-tertiary-color` | Tertiary text | `rgba(33, 37, 41, 0.5)` | `rgba(222, 226, 230, 0.5)` |
| `--bs-secondary-bg` | Secondary background | `#e9ecef` | `#343a40` |
| `--bs-tertiary-bg` | Tertiary background | `#f8f9fa` | `#2b3035` |

### Link Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `--bs-link-color` | Link color | `#0d6efd` |
| `--bs-link-hover-color` | Link hover color | `#0a58ca` |
| `--bs-link-decoration` | Link text decoration | `underline` |
| `--bs-link-opacity` | Link color opacity | `1` |

Customize link opacity inline:

```html
<a href="#" style="--bs-link-opacity: .5">Link with 50% opacity</a>
<a href="#" style="--bs-link-opacity: .75">Link with 75% opacity</a>
```

### Border Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `--bs-border-width` | Default border width | `1px` |
| `--bs-border-style` | Default border style | `solid` |
| `--bs-border-color` | Default border color | `#dee2e6` |
| `--bs-border-radius` | Default border radius | `0.375rem` |

## Page Defaults

### Box Sizing

Bootstrap sets `box-sizing: border-box` globally:

```css
*,
*::before,
*::after {
  box-sizing: border-box;
}
```

### Root Font Size

The `:root` element uses the browser default (typically 16px), enabling `rem` units:

```css
:root {
  font-size: var(--bs-root-font-size);
}

@media (prefers-reduced-motion: no-preference) {
  :root {
    scroll-behavior: smooth;
  }
}
```

### Body Defaults

```css
body {
  margin: 0;
  font-family: var(--bs-body-font-family);
  font-size: var(--bs-body-font-size);
  font-weight: var(--bs-body-font-weight);
  line-height: var(--bs-body-line-height);
  color: var(--bs-body-color);
  background-color: var(--bs-body-bg);
  -webkit-text-size-adjust: 100%;
  -webkit-tap-highlight-color: transparent;
}
```

## Native Font Stack

Bootstrap uses a "native font stack" for optimal text rendering on every device:

```scss
$font-family-sans-serif:
  system-ui,
  -apple-system,
  "Segoe UI",
  Roboto,
  "Helvetica Neue",
  "Noto Sans",
  "Liberation Sans",
  Arial,
  sans-serif,
  "Apple Color Emoji",
  "Segoe UI Emoji",
  "Segoe UI Symbol",
  "Noto Color Emoji";

$font-family-monospace:
  SFMono-Regular,
  Menlo,
  Monaco,
  Consolas,
  "Liberation Mono",
  "Courier New",
  monospace;
```

**Benefits:**

- Faster page load (no web font downloads)
- Native look and feel on each OS
- Proper emoji rendering across platforms
- Optimal accessibility (users see familiar fonts)

## Element Resets

### Headings

| Element | margin-top | margin-bottom | font-weight |
|---------|------------|---------------|-------------|
| `h1`-`h6` | `0` | `0.5rem` | `500` |

### Paragraphs

```css
p {
  margin-top: 0;
  margin-bottom: 1rem;
}
```

### Links

```css
a {
  color: var(--bs-link-color);
  text-decoration: underline;
}

a:hover {
  color: var(--bs-link-hover-color);
}

a:not([href]):not([class]) {
  color: inherit;
  text-decoration: none;
}
```

### Lists

```css
ol, ul {
  padding-left: 2rem;
}

ol, ul, dl {
  margin-top: 0;
  margin-bottom: 1rem;
}

ol ol, ul ul, ol ul, ul ol {
  margin-bottom: 0;
}

dt {
  font-weight: 700;
}

dd {
  margin-bottom: 0.5rem;
  margin-left: 0;
}
```

### Blockquotes

```css
blockquote {
  margin: 0 0 1rem;
}
```

### Horizontal Rules

```css
hr {
  margin: 1rem 0;
  color: inherit;
  border: 0;
  border-top: var(--bs-border-width) solid;
  opacity: 0.25;
}
```

### Code Elements

| Element | Font Family | Font Size |
|---------|-------------|-----------|
| `code` | Monospace stack | `0.875em` |
| `kbd` | Monospace stack | `0.875em` |
| `samp` | Monospace stack | `0.875em` |
| `pre` | Monospace stack | `0.875em` |

```css
pre {
  display: block;
  margin-top: 0;
  margin-bottom: 1rem;
  overflow: auto;
  font-size: 0.875em;
}

code {
  font-size: 0.875em;
  color: var(--bs-code-color);
  word-wrap: break-word;
}
```

### Tables

```css
table {
  caption-side: bottom;
  border-collapse: collapse;
}

caption {
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
  color: var(--bs-secondary-color);
  text-align: left;
}

th {
  text-align: inherit;
  text-align: -webkit-match-parent;
}
```

### Forms

```css
label {
  display: inline-block;
}

button {
  border-radius: 0;
}

input, button, select, optgroup, textarea {
  margin: 0;
  font-family: inherit;
  font-size: inherit;
  line-height: inherit;
}

button, select {
  text-transform: none;
}

fieldset {
  min-width: 0;
  padding: 0;
  margin: 0;
  border: 0;
}

legend {
  float: left;
  width: 100%;
  padding: 0;
  margin-bottom: 0.5rem;
  font-size: calc(1.275rem + 0.3vw);
  font-weight: inherit;
  line-height: inherit;
}

textarea {
  resize: vertical;
}
```

### Images

```css
img, svg {
  vertical-align: middle;
}
```

### Summary

```css
summary {
  display: list-item;
  cursor: pointer;
}
```

### Progress

```css
progress {
  vertical-align: baseline;
}
```

### Hidden Attribute

```css
[hidden] {
  display: none !important;
}
```

## Sass Variables

Key Sass variables for customizing Reboot:

### Typography

| Variable | Default | Description |
|----------|---------|-------------|
| `$font-family-base` | `$font-family-sans-serif` | Base font family |
| `$font-family-code` | `$font-family-monospace` | Code font family |
| `$font-size-root` | `null` | Root font size |
| `$font-size-base` | `1rem` | Base font size |
| `$font-weight-base` | `400` | Base font weight |
| `$line-height-base` | `1.5` | Base line height |

### Body

| Variable | Default | Description |
|----------|---------|-------------|
| `$body-bg` | `$white` | Body background color |
| `$body-color` | `$gray-900` | Body text color |
| `$body-emphasis-color` | `$black` | Emphasis text color |
| `$body-secondary-color` | `rgba($body-color, .75)` | Secondary text |
| `$body-tertiary-color` | `rgba($body-color, .5)` | Tertiary text |

### Links

| Variable | Default | Description |
|----------|---------|-------------|
| `$link-color` | `$primary` | Link color |
| `$link-decoration` | `underline` | Link decoration |
| `$link-hover-color` | `$link-color` shifted | Hover color |
| `$link-hover-decoration` | `null` | Hover decoration |

### Paragraphs

| Variable | Default | Description |
|----------|---------|-------------|
| `$paragraph-margin-bottom` | `1rem` | Paragraph margin |

### Horizontal Rules

| Variable | Default | Description |
|----------|---------|-------------|
| `$hr-margin-y` | `1rem` | HR vertical margin |
| `$hr-color` | `inherit` | HR color |
| `$hr-border-color` | `null` | HR border color |
| `$hr-border-width` | `var(--#{$prefix}border-width)` | HR border width |
| `$hr-opacity` | `.25` | HR opacity |

## Customization Examples

### Custom Body Styling

```scss
// In your custom _variables.scss
$body-bg: #f5f5f5;
$body-color: #333;
$font-family-base: "Helvetica Neue", Arial, sans-serif;
$font-size-base: 1rem;
$line-height-base: 1.6;
```

### Custom Link Styling

```scss
$link-color: #007bff;
$link-decoration: none;
$link-hover-color: darken($link-color, 15%);
$link-hover-decoration: underline;
```

### Disable Reboot Features

To prevent Reboot from affecting specific elements, override the CSS:

```css
/* Keep native list styling */
ul.keep-native,
ol.keep-native {
  padding-left: revert;
  margin: revert;
}
```
