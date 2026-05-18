# Bootstrap Grid Class Reference

Complete reference for all Bootstrap 5.3 grid and layout classes.

## Container Classes

| Class | Description |
|-------|-------------|
| `.container` | Fixed-width, responsive container |
| `.container-fluid` | Full-width container |
| `.container-{breakpoint}` | Full-width until specified breakpoint |

## Row Classes

| Class | Description |
|-------|-------------|
| `.row` | Flex container for columns |
| `.row-cols-{n}` | Set number of columns per row (1-6) |
| `.row-cols-{breakpoint}-{n}` | Responsive column count |
| `.row-cols-auto` | Auto-width columns |

## Column Classes

### Width Classes

| Class | Description |
|-------|-------------|
| `.col` | Equal-width column |
| `.col-auto` | Width based on content |
| `.col-{n}` | Specific width (1-12) |
| `.col-{breakpoint}` | Equal-width from breakpoint |
| `.col-{breakpoint}-{n}` | Specific width from breakpoint |
| `.col-{breakpoint}-auto` | Content-based width from breakpoint |

### Available Breakpoints

- (none) - Extra small, <576px
- `sm` - Small, ≥576px
- `md` - Medium, ≥768px
- `lg` - Large, ≥992px
- `xl` - Extra large, ≥1200px
- `xxl` - Extra extra large, ≥1400px

## Gutter Classes

| Class | Description |
|-------|-------------|
| `.g-{n}` | Horizontal and vertical gutter (0-5) |
| `.gx-{n}` | Horizontal gutter only |
| `.gy-{n}` | Vertical gutter only |
| `.g-{breakpoint}-{n}` | Responsive gutters |

### Gutter Values

| Class | Value |
|-------|-------|
| `.g-0` | 0 |
| `.g-1` | 0.25rem (4px) |
| `.g-2` | 0.5rem (8px) |
| `.g-3` | 1rem (16px) |
| `.g-4` | 1.5rem (24px) |
| `.g-5` | 3rem (48px) |

## Alignment Classes

### Row Alignment (align-items)

| Class | Description |
|-------|-------------|
| `.align-items-start` | Align columns to top |
| `.align-items-center` | Align columns to center |
| `.align-items-end` | Align columns to bottom |
| `.align-items-baseline` | Align columns to baseline |
| `.align-items-stretch` | Stretch columns (default) |

### Column Self-Alignment

| Class | Description |
|-------|-------------|
| `.align-self-start` | Align single column to top |
| `.align-self-center` | Align single column to center |
| `.align-self-end` | Align single column to bottom |
| `.align-self-baseline` | Align to baseline |
| `.align-self-stretch` | Stretch column |

### Horizontal Justification

| Class | Description |
|-------|-------------|
| `.justify-content-start` | Align left |
| `.justify-content-center` | Center columns |
| `.justify-content-end` | Align right |
| `.justify-content-around` | Equal space around |
| `.justify-content-between` | Equal space between |
| `.justify-content-evenly` | Equal space everywhere |

## Ordering Classes

| Class | Description |
|-------|-------------|
| `.order-{n}` | Set order (0-5) |
| `.order-first` | Order: -1 |
| `.order-last` | Order: 6 |
| `.order-{breakpoint}-{n}` | Responsive ordering |

## Offset Classes

| Class | Description |
|-------|-------------|
| `.offset-{n}` | Offset by n columns |
| `.offset-{breakpoint}-{n}` | Responsive offset |
| `.offset-{breakpoint}-0` | Remove offset at breakpoint |

## Display Utilities (Layout-Related)

| Class | Description |
|-------|-------------|
| `.d-none` | Hide element |
| `.d-block` | Display block |
| `.d-flex` | Display flex |
| `.d-grid` | Display grid |
| `.d-inline` | Display inline |
| `.d-inline-block` | Display inline-block |
| `.d-inline-flex` | Display inline-flex |
| `.d-{breakpoint}-none` | Hide at breakpoint |
| `.d-{breakpoint}-block` | Block at breakpoint |

## Flexbox Utilities

### Direction

| Class | Description |
|-------|-------------|
| `.flex-row` | Horizontal direction |
| `.flex-row-reverse` | Horizontal reversed |
| `.flex-column` | Vertical direction |
| `.flex-column-reverse` | Vertical reversed |

### Wrap

| Class | Description |
|-------|-------------|
| `.flex-wrap` | Allow wrapping |
| `.flex-nowrap` | Prevent wrapping |
| `.flex-wrap-reverse` | Wrap in reverse |

### Grow & Shrink

| Class | Description |
|-------|-------------|
| `.flex-grow-0` | Don't grow |
| `.flex-grow-1` | Grow to fill |
| `.flex-shrink-0` | Don't shrink |
| `.flex-shrink-1` | Allow shrink |
| `.flex-fill` | Fill available space |

## Gap Utilities

| Class | Description |
|-------|-------------|
| `.gap-{n}` | Set gap for flex/grid (0-5) |
| `.row-gap-{n}` | Vertical gap |
| `.column-gap-{n}` | Horizontal gap |

## CSS Grid Classes

| Class | Description |
|-------|-------------|
| `.grid` | CSS Grid container |
| `.g-col-{n}` | Grid column span (1-12) |
| `.g-col-{breakpoint}-{n}` | Responsive grid column |
| `.g-start-{n}` | Grid column start |
| `.g-start-{breakpoint}-{n}` | Responsive start |

### CSS Grid Variables

```css
.grid {
  --bs-columns: 12;
  --bs-gap: 1.5rem;
  --bs-rows: 1;
}
```

## Z-Index Classes

| Class | Value |
|-------|-------|
| `.z-n1` | -1 |
| `.z-0` | 0 |
| `.z-1` | 1 |
| `.z-2` | 2 |
| `.z-3` | 3 |

## Position Utilities

| Class | Description |
|-------|-------------|
| `.position-static` | Static positioning |
| `.position-relative` | Relative positioning |
| `.position-absolute` | Absolute positioning |
| `.position-fixed` | Fixed positioning |
| `.position-sticky` | Sticky positioning |
| `.fixed-top` | Fixed to top |
| `.fixed-bottom` | Fixed to bottom |
| `.sticky-top` | Sticky to top |
| `.sticky-{breakpoint}-top` | Responsive sticky |

## Sizing Utilities

### Width

| Class | Description |
|-------|-------------|
| `.w-25` | 25% width |
| `.w-50` | 50% width |
| `.w-75` | 75% width |
| `.w-100` | 100% width |
| `.w-auto` | Auto width |
| `.mw-100` | Max-width 100% |
| `.vw-100` | 100vw |
| `.min-vw-100` | Min 100vw |

### Height

| Class | Description |
|-------|-------------|
| `.h-25` | 25% height |
| `.h-50` | 50% height |
| `.h-75` | 75% height |
| `.h-100` | 100% height |
| `.h-auto` | Auto height |
| `.mh-100` | Max-height 100% |
| `.vh-100` | 100vh |
| `.min-vh-100` | Min 100vh |

## Common Layout Patterns

### Centered Content

```html
<div class="d-flex justify-content-center align-items-center min-vh-100">
  <div>Centered content</div>
</div>
```

### Holy Grail Layout

```html
<div class="container-fluid">
  <header class="row">Header</header>
  <div class="row flex-grow-1">
    <nav class="col-md-2">Sidebar</nav>
    <main class="col-md-8">Main</main>
    <aside class="col-md-2">Aside</aside>
  </div>
  <footer class="row">Footer</footer>
</div>
```

### Sticky Footer

```html
<body class="d-flex flex-column min-vh-100">
  <main class="flex-grow-1">Content</main>
  <footer>Sticky footer</footer>
</body>
```
