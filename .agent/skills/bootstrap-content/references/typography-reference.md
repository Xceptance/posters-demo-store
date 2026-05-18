# Typography Reference

Complete reference for Bootstrap 5.3 typography classes and styles.

## Heading Classes

| Element/Class | Size | Line Height |
|---------------|------|-------------|
| `h1` / `.h1` | 2.5rem (40px) | 1.2 |
| `h2` / `.h2` | 2rem (32px) | 1.2 |
| `h3` / `.h3` | 1.75rem (28px) | 1.2 |
| `h4` / `.h4` | 1.5rem (24px) | 1.2 |
| `h5` / `.h5` | 1.25rem (20px) | 1.2 |
| `h6` / `.h6` | 1rem (16px) | 1.2 |

## Display Classes

| Class | Size | Weight |
|-------|------|--------|
| `.display-1` | 5rem (80px) | 300 |
| `.display-2` | 4.5rem (72px) | 300 |
| `.display-3` | 4rem (64px) | 300 |
| `.display-4` | 3.5rem (56px) | 300 |
| `.display-5` | 3rem (48px) | 300 |
| `.display-6` | 2.5rem (40px) | 300 |

## Customizing Headings

Add secondary text within headings using `<small>` with `.text-body-secondary`:

```html
<h1>
  Main heading
  <small class="text-body-secondary">Secondary text</small>
</h1>

<h1 class="display-4">
  Display heading
  <small class="text-body-secondary">With subtitle</small>
</h1>
```

## Font Size Classes

| Class | Size |
|-------|------|
| `.fs-1` | 2.5rem (40px) |
| `.fs-2` | 2rem (32px) |
| `.fs-3` | 1.75rem (28px) |
| `.fs-4` | 1.5rem (24px) |
| `.fs-5` | 1.25rem (20px) |
| `.fs-6` | 1rem (16px) |

## Responsive Font Sizes (RFS)

Bootstrap 5 uses RFS (Responsive Font Sizes) by default. RFS automatically scales `font-size` based on viewport width, ensuring text remains readable across all devices.

**Key points:**

- Enabled by default in Bootstrap 5
- Applies to headings, display headings, and lead text
- Prevents text from becoming too large on big screens or too small on mobile
- Uses a combination of `calc()` and viewport units
- Can be customized via Sass variables: `$enable-rfs`, `$rfs-base-value`, `$rfs-breakpoint`

## Font Weight Classes

| Class | Value |
|-------|-------|
| `.fw-lighter` | lighter |
| `.fw-light` | 300 |
| `.fw-normal` | 400 |
| `.fw-medium` | 500 |
| `.fw-semibold` | 600 |
| `.fw-bold` | 700 |
| `.fw-bolder` | bolder |

## Font Style Classes

| Class | Style |
|-------|-------|
| `.fst-italic` | italic |
| `.fst-normal` | normal |

## Inline Text Utility Classes

Apply element-equivalent styles without semantic meaning:

| Class | Equivalent Element | Description |
|-------|-------------------|-------------|
| `.mark` | `<mark>` | Highlighted/marked text |
| `.small` | `<small>` | Smaller, secondary text |

```html
<!-- Use element when semantics matter -->
<p>This is <mark>semantically highlighted</mark> text.</p>
<p>This is <small>semantically small</small> text.</p>

<!-- Use class when only visual styling is needed -->
<span class="mark">Highlighted without semantic meaning</span>
<span class="small">Small without semantic meaning</span>
```

## Line Height Classes

| Class | Value |
|-------|-------|
| `.lh-1` | 1 |
| `.lh-sm` | 1.25 |
| `.lh-base` | 1.5 |
| `.lh-lg` | 2 |

## Text Alignment

| Class | Alignment |
|-------|-----------|
| `.text-start` | Left (LTR) / Right (RTL) |
| `.text-center` | Center |
| `.text-end` | Right (LTR) / Left (RTL) |

### Responsive Alignment

```
.text-{breakpoint}-start
.text-{breakpoint}-center
.text-{breakpoint}-end
```

## Text Transform

| Class | Transform |
|-------|-----------|
| `.text-lowercase` | lowercase |
| `.text-uppercase` | UPPERCASE |
| `.text-capitalize` | Capitalize Each Word |

## Text Decoration

| Class | Decoration |
|-------|------------|
| `.text-decoration-underline` | Underline |
| `.text-decoration-line-through` | Line-through |
| `.text-decoration-none` | None |

## Text Wrapping

| Class | Behavior |
|-------|----------|
| `.text-wrap` | Normal wrapping |
| `.text-nowrap` | No wrapping |
| `.text-break` | Break long words |
| `.text-truncate` | Ellipsis (requires display: block/inline-block + width) |

## Text Colors

| Class | Description |
|-------|-------------|
| `.text-primary` | Primary color |
| `.text-secondary` | Secondary color |
| `.text-success` | Success color |
| `.text-danger` | Danger color |
| `.text-warning` | Warning color |
| `.text-info` | Info color |
| `.text-light` | Light color |
| `.text-dark` | Dark color |
| `.text-body` | Body color |
| `.text-body-secondary` | Secondary body (muted) |
| `.text-body-tertiary` | Tertiary body |
| `.text-body-emphasis` | Emphasized body |
| `.text-muted` | Muted gray (deprecated, use .text-body-secondary) |
| `.text-white` | White |
| `.text-black` | Black |
| `.text-black-50` | 50% black |
| `.text-white-50` | 50% white |

### Opacity Modifiers

```html
<p class="text-primary text-opacity-75">75% opacity</p>
<p class="text-primary text-opacity-50">50% opacity</p>
<p class="text-primary text-opacity-25">25% opacity</p>
```

## Monospace

```html
<p class="font-monospace">Monospace text</p>
```

## Reset Color

```html
<a href="#" class="text-reset">Link with inherited color</a>
```

## Horizontal Rules

```html
<hr>
<hr class="border border-danger border-2 opacity-50">
```

## Abbreviations

```html
<abbr title="attribute">attr</abbr>
<abbr title="HyperText Markup Language" class="initialism">HTML</abbr>
```
