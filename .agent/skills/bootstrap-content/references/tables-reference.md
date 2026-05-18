# Tables Reference

Complete reference for Bootstrap 5.3 table classes and patterns.

## Base Table

| Class | Description |
|-------|-------------|
| `.table` | Base table styling with bottom borders and padding |

## Color Variants

Apply to `<table>`, `<tr>`, `<th>`, or `<td>`:

| Class | Description |
|-------|-------------|
| `.table-primary` | Primary theme color |
| `.table-secondary` | Secondary theme color |
| `.table-success` | Success (green) |
| `.table-danger` | Danger (red) |
| `.table-warning` | Warning (yellow) |
| `.table-info` | Info (cyan) |
| `.table-light` | Light background |
| `.table-dark` | Dark background |

> **Note:** Table color variants won't see color mode adaptive styling until Bootstrap v6. In v5.3, tables with color variants maintain their assigned colors regardless of light/dark mode settings.

## Style Modifiers

| Class | Description |
|-------|-------------|
| `.table-striped` | Zebra-striped rows |
| `.table-striped-columns` | Zebra-striped columns |
| `.table-hover` | Highlight row on hover |
| `.table-active` | Highlight specific row/cell |
| `.table-bordered` | Borders on all sides |
| `.table-borderless` | Remove all borders |
| `.table-sm` | Compact table with reduced padding |

## Table Head Variants

| Class | Description |
|-------|-------------|
| `.table-light` | Light gray header (on `<thead>`) |
| `.table-dark` | Dark header (on `<thead>`) |

```html
<thead class="table-light">...</thead>
<thead class="table-dark">...</thead>
```

## Table Group Dividers

| Class | Description |
|-------|-------------|
| `.table-group-divider` | Thicker top border to separate sections |

Apply to `<tbody>` or `<tfoot>` to create visual separation:

```html
<tbody class="table-group-divider">...</tbody>
<tfoot class="table-group-divider">...</tfoot>
```

## Vertical Alignment

Apply to `<table>`, `<tr>`, `<th>`, or `<td>`:

| Class | Alignment |
|-------|-----------|
| `.align-top` | Align to top |
| `.align-middle` | Align to middle |
| `.align-bottom` | Align to bottom |

## Responsive Tables

Wrap table in a responsive container:

| Class | Behavior |
|-------|----------|
| `.table-responsive` | Horizontal scroll on all viewports |
| `.table-responsive-sm` | Scroll below 576px |
| `.table-responsive-md` | Scroll below 768px |
| `.table-responsive-lg` | Scroll below 992px |
| `.table-responsive-xl` | Scroll below 1200px |
| `.table-responsive-xxl` | Scroll below 1400px |

```html
<div class="table-responsive">
  <table class="table">...</table>
</div>
```

## Captions

| Element/Class | Description |
|---------------|-------------|
| `<caption>` | Table caption (default: below table) |
| `.caption-top` | Position caption above table |

```html
<table class="table">
  <caption>List of users</caption>
  ...
</table>

<table class="table caption-top">
  <caption>List of users</caption>
  ...
</table>
```

## Table Sections

| Element | Description |
|---------|-------------|
| `<thead>` | Table header group |
| `<tbody>` | Table body group |
| `<tfoot>` | Table footer group |

## Scope Attributes

For accessibility, use `scope` on header cells:

| Attribute | Description |
|-----------|-------------|
| `scope="col"` | Header for a column |
| `scope="row"` | Header for a row |
| `scope="colgroup"` | Header for column group |
| `scope="rowgroup"` | Header for row group |

```html
<th scope="col">Column Header</th>
<th scope="row">Row Header</th>
```

## Combining Classes

Common combinations:

```html
<!-- Striped, hoverable, bordered -->
<table class="table table-striped table-hover table-bordered">

<!-- Dark theme with striped rows -->
<table class="table table-dark table-striped">

<!-- Compact, borderless -->
<table class="table table-sm table-borderless">

<!-- Responsive, striped columns -->
<div class="table-responsive">
  <table class="table table-striped-columns">
```

## Nested Tables

Nested tables do not inherit styles:

```html
<table class="table table-striped">
  <tbody>
    <tr>
      <td>
        <!-- Nested table needs its own .table class -->
        <table class="table">
          ...
        </table>
      </td>
    </tr>
  </tbody>
</table>
```

## Accessibility Best Practices

1. **Use semantic markup**: `<thead>`, `<tbody>`, `<tfoot>`
2. **Add scope attributes**: `scope="col"` and `scope="row"`
3. **Provide captions**: `<caption>` describes table purpose
4. **Use `<th>` for headers**: Not just `<td>` with bold text
5. **Avoid layout tables**: Use CSS Grid or Flexbox instead
6. **Consider responsive design**: Use `.table-responsive` for wide tables

## Sass Variables

Key variables for customizing tables:

```scss
$table-cell-padding-y: .5rem;
$table-cell-padding-x: .5rem;
$table-cell-padding-y-sm: .25rem;
$table-cell-padding-x-sm: .25rem;

$table-bg: transparent;
$table-striped-bg: rgba($black, .05);
$table-hover-bg: rgba($black, .075);
$table-active-bg: rgba($black, .1);

$table-border-width: var(--#{$prefix}border-width);
$table-border-color: var(--#{$prefix}border-color);

$table-group-separator-color: currentcolor;
```
