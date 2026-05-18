# Utilities Reference

Quick reference for all Bootstrap 5.3 utility classes.

## Spacing

### Margin Classes

| Property | Sides | Sizes |
|----------|-------|-------|
| `m` | All | 0, 1, 2, 3, 4, 5, auto |
| `mt` | Top | 0, 1, 2, 3, 4, 5, auto |
| `mb` | Bottom | 0, 1, 2, 3, 4, 5, auto |
| `ms` | Start | 0, 1, 2, 3, 4, 5, auto |
| `me` | End | 0, 1, 2, 3, 4, 5, auto |
| `mx` | Horizontal | 0, 1, 2, 3, 4, 5, auto |
| `my` | Vertical | 0, 1, 2, 3, 4, 5, auto |

### Padding Classes

| Property | Sides | Sizes |
|----------|-------|-------|
| `p` | All | 0, 1, 2, 3, 4, 5 |
| `pt` | Top | 0, 1, 2, 3, 4, 5 |
| `pb` | Bottom | 0, 1, 2, 3, 4, 5 |
| `ps` | Start | 0, 1, 2, 3, 4, 5 |
| `pe` | End | 0, 1, 2, 3, 4, 5 |
| `px` | Horizontal | 0, 1, 2, 3, 4, 5 |
| `py` | Vertical | 0, 1, 2, 3, 4, 5 |

### Gap Classes

| Class | Description |
|-------|-------------|
| `gap-{0-5}` | Both directions |
| `row-gap-{0-5}` | Vertical only |
| `column-gap-{0-5}` | Horizontal only |

## Display

| Class | Value |
|-------|-------|
| `d-none` | none |
| `d-inline` | inline |
| `d-inline-block` | inline-block |
| `d-block` | block |
| `d-grid` | grid |
| `d-inline-grid` | inline-grid |
| `d-table` | table |
| `d-table-cell` | table-cell |
| `d-table-row` | table-row |
| `d-flex` | flex |
| `d-inline-flex` | inline-flex |

Responsive: `d-{breakpoint}-{value}`
Print: `d-print-{value}`

## Text

### Alignment

| Class | Alignment |
|-------|-----------|
| `text-start` | Left (LTR) |
| `text-center` | Center |
| `text-end` | Right (LTR) |

Responsive: `text-{breakpoint}-{start\|center\|end}`

### Transform

| Class | Transform |
|-------|-----------|
| `text-lowercase` | lowercase |
| `text-uppercase` | UPPERCASE |
| `text-capitalize` | Capitalize |

### Decoration

| Class | Decoration |
|-------|------------|
| `text-decoration-none` | None |
| `text-decoration-underline` | Underline |
| `text-decoration-line-through` | Strikethrough |

### Wrapping

| Class | Behavior |
|-------|----------|
| `text-wrap` | Normal wrapping |
| `text-nowrap` | No wrapping |
| `text-break` | Break long words |
| `text-truncate` | Truncate with ellipsis |

### Font Weight

| Class | Weight |
|-------|--------|
| `fw-bold` | 700 |
| `fw-bolder` | Bolder (relative) |
| `fw-semibold` | 600 |
| `fw-medium` | 500 |
| `fw-normal` | 400 |
| `fw-light` | 300 |
| `fw-lighter` | Lighter (relative) |

### Font Style

| Class | Style |
|-------|-------|
| `fst-italic` | Italic |
| `fst-normal` | Normal |

### Line Height

| Class | Line Height |
|-------|-------------|
| `lh-1` | 1 |
| `lh-sm` | 1.25 |
| `lh-base` | 1.5 |
| `lh-lg` | 2 |

### Font Size

| Class | Size |
|-------|------|
| `fs-1` | 2.5rem (h1) |
| `fs-2` | 2rem (h2) |
| `fs-3` | 1.75rem (h3) |
| `fs-4` | 1.5rem (h4) |
| `fs-5` | 1.25rem (h5) |
| `fs-6` | 1rem (h6) |

### Font Family

| Class | Font |
|-------|------|
| `font-monospace` | Monospace stack |

### Miscellaneous

| Class | Description |
|-------|-------------|
| `text-reset` | Reset color to inherit from parent |

## Flexbox

### Direction

| Class | Direction |
|-------|-----------|
| `flex-row` | row |
| `flex-row-reverse` | row-reverse |
| `flex-column` | column |
| `flex-column-reverse` | column-reverse |

### Justify Content

| Class | Alignment |
|-------|-----------|
| `justify-content-start` | Start |
| `justify-content-end` | End |
| `justify-content-center` | Center |
| `justify-content-between` | Space between |
| `justify-content-around` | Space around |
| `justify-content-evenly` | Space evenly |

### Align Items

| Class | Alignment |
|-------|-----------|
| `align-items-start` | Start |
| `align-items-end` | End |
| `align-items-center` | Center |
| `align-items-baseline` | Baseline |
| `align-items-stretch` | Stretch |

### Align Self

| Class | Alignment |
|-------|-----------|
| `align-self-start` | Start |
| `align-self-end` | End |
| `align-self-center` | Center |
| `align-self-baseline` | Baseline |
| `align-self-stretch` | Stretch |

### Align Content

| Class | Alignment |
|-------|-----------|
| `align-content-start` | Start |
| `align-content-end` | End |
| `align-content-center` | Center |
| `align-content-between` | Space between |
| `align-content-around` | Space around |
| `align-content-stretch` | Stretch |

### Wrap

| Class | Behavior |
|-------|----------|
| `flex-wrap` | Wrap |
| `flex-nowrap` | No wrap |
| `flex-wrap-reverse` | Wrap reverse |

### Grow & Shrink

| Class | Value |
|-------|-------|
| `flex-fill` | flex: 1 1 auto |
| `flex-grow-0` | flex-grow: 0 |
| `flex-grow-1` | flex-grow: 1 |
| `flex-shrink-0` | flex-shrink: 0 |
| `flex-shrink-1` | flex-shrink: 1 |

### Order

| Class | Order |
|-------|-------|
| `order-first` | -1 |
| `order-0` | 0 |
| `order-1` | 1 |
| `order-2` | 2 |
| `order-3` | 3 |
| `order-4` | 4 |
| `order-5` | 5 |
| `order-last` | 6 |

## Colors

### Text

`text-{color}` where color is:
primary, secondary, success, danger, warning, info, light, dark, body, body-secondary, body-tertiary, body-emphasis, black, white, muted

### Background

`bg-{color}` where color is:
primary, secondary, success, danger, warning, info, light, dark, body, body-secondary, body-tertiary, white, black, transparent

### Opacity

- `text-opacity-{25|50|75|100}`
- `bg-opacity-{10|25|50|75|100}`

## Borders

### Width

| Class | Width |
|-------|-------|
| `border` | 1px (default) |
| `border-{1-5}` | 1px - 5px |
| `border-0` | None |
| `border-{side}-0` | Remove side |

### Color

`border-{color}` where color is:
primary, secondary, success, danger, warning, info, light, dark, black, white

### Opacity

| Class | Opacity |
|-------|---------|
| `border-opacity-10` | 10% |
| `border-opacity-25` | 25% |
| `border-opacity-50` | 50% |
| `border-opacity-75` | 75% |
| `border-opacity-100` | 100% |

### Radius

| Class | Radius |
|-------|--------|
| `rounded` | Default |
| `rounded-0` | None |
| `rounded-{1-5}` | Size levels |
| `rounded-circle` | 50% |
| `rounded-pill` | 50rem |
| `rounded-{top\|end\|bottom\|start}` | Specific corners |

## Shadows

| Class | Shadow |
|-------|--------|
| `shadow-none` | None |
| `shadow-sm` | Small |
| `shadow` | Regular |
| `shadow-lg` | Large |

## Sizing

### Width

| Class | Width |
|-------|-------|
| `w-25` | 25% |
| `w-50` | 50% |
| `w-75` | 75% |
| `w-100` | 100% |
| `w-auto` | auto |
| `mw-100` | max-width: 100% |
| `vw-100` | 100vw |
| `min-vw-100` | min-width: 100vw |

### Height

| Class | Height |
|-------|--------|
| `h-25` | 25% |
| `h-50` | 50% |
| `h-75` | 75% |
| `h-100` | 100% |
| `h-auto` | auto |
| `mh-100` | max-height: 100% |
| `vh-100` | 100vh |
| `min-vh-100` | min-height: 100vh |

## Position

| Class | Position |
|-------|----------|
| `position-static` | static |
| `position-relative` | relative |
| `position-absolute` | absolute |
| `position-fixed` | fixed |
| `position-sticky` | sticky |

### Placement

| Class | Value |
|-------|-------|
| `top-0` | top: 0 |
| `top-50` | top: 50% |
| `top-100` | top: 100% |
| `start-0` | left: 0 |
| `start-50` | left: 50% |
| `start-100` | left: 100% |
| `end-0` | right: 0 |
| `end-50` | right: 50% |
| `end-100` | right: 100% |
| `bottom-0` | bottom: 0 |
| `bottom-50` | bottom: 50% |
| `bottom-100` | bottom: 100% |

### Transform

| Class | Transform |
|-------|-----------|
| `translate-middle` | translate(-50%, -50%) |
| `translate-middle-x` | translateX(-50%) |
| `translate-middle-y` | translateY(-50%) |

## Overflow

| Class | Overflow |
|-------|----------|
| `overflow-auto` | auto |
| `overflow-hidden` | hidden |
| `overflow-visible` | visible |
| `overflow-scroll` | scroll |
| `overflow-x-auto` | X auto |
| `overflow-x-hidden` | X hidden |
| `overflow-y-auto` | Y auto |
| `overflow-y-hidden` | Y hidden |

## Visibility

| Class | Visibility |
|-------|------------|
| `visible` | visible |
| `invisible` | hidden |

## Z-Index

| Class | Z-Index |
|-------|---------|
| `z-n1` | -1 |
| `z-0` | 0 |
| `z-1` | 1 |
| `z-2` | 2 |
| `z-3` | 3 |

## Object Fit

| Class | Fit |
|-------|-----|
| `object-fit-contain` | contain |
| `object-fit-cover` | cover |
| `object-fit-fill` | fill |
| `object-fit-scale` | scale-down |
| `object-fit-none` | none |

## Opacity

| Class | Opacity |
|-------|---------|
| `opacity-0` | 0 |
| `opacity-25` | 0.25 |
| `opacity-50` | 0.5 |
| `opacity-75` | 0.75 |
| `opacity-100` | 1 |

## Interaction

| Class | Description |
|-------|-------------|
| `user-select-all` | Select all |
| `user-select-auto` | Default |
| `user-select-none` | No select |
| `pe-none` | No pointer events |
| `pe-auto` | Auto pointer events |

## Float

| Class | Float |
|-------|-------|
| `float-start` | Left (LTR) |
| `float-end` | Right (LTR) |
| `float-none` | None |

Responsive: `float-{breakpoint}-{start|end|none}`

## Vertical Align

| Class | Alignment |
|-------|-----------|
| `align-baseline` | Baseline |
| `align-top` | Top |
| `align-middle` | Middle |
| `align-bottom` | Bottom |
| `align-text-top` | Text top |
| `align-text-bottom` | Text bottom |

## Link

### Opacity

| Class | Opacity |
|-------|---------|
| `link-opacity-10` | 10% |
| `link-opacity-25` | 25% |
| `link-opacity-50` | 50% |
| `link-opacity-75` | 75% |
| `link-opacity-100` | 100% |
| `link-opacity-{value}-hover` | On hover |

### Underline Color

`link-underline-{color}` where color is:
primary, secondary, success, danger, warning, info, light, dark

### Underline Offset

| Class | Offset |
|-------|--------|
| `link-offset-1` | 1 |
| `link-offset-2` | 2 |
| `link-offset-3` | 3 |

### Underline Opacity

| Class | Opacity |
|-------|---------|
| `link-underline-opacity-0` | 0% |
| `link-underline-opacity-10` | 10% |
| `link-underline-opacity-25` | 25% |
| `link-underline-opacity-50` | 50% |
| `link-underline-opacity-75` | 75% |
| `link-underline-opacity-100` | 100% |
