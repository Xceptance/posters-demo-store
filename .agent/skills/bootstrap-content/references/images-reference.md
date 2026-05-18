# Images Reference

Complete reference for Bootstrap 5.3 image classes and responsive patterns.

## Class Reference

| Class | Description | CSS |
|-------|-------------|-----|
| `.img-fluid` | Responsive image that scales with parent | `max-width: 100%; height: auto;` |
| `.img-thumbnail` | Rounded border with padding | Border, padding, background, border-radius |

## Responsive Images

The `.img-fluid` class applies `max-width: 100%` and `height: auto` to scale images with their parent container:

```html
<img src="..." class="img-fluid" alt="Responsive image">
```

**Behavior:**

- Image scales down to fit container width
- Never scales larger than its native size
- Maintains aspect ratio automatically
- Works with any image format (JPG, PNG, SVG, WebP)

## Image Thumbnails

The `.img-thumbnail` class gives images a rounded 1px border appearance:

```html
<img src="..." class="img-thumbnail" alt="Thumbnail">
```

**Styling includes:**

- Padding around the image
- Background color
- Border with border-radius
- Box shadow

## Alignment Options

### Float Alignment

Use float utilities to align images within text content:

```html
<img src="..." class="float-start" alt="Left aligned">
<img src="..." class="float-end" alt="Right aligned">
```

### Block Centering

Center images using display and margin utilities:

```html
<!-- Method 1: Block with auto margins -->
<img src="..." class="d-block mx-auto" alt="Centered">

<!-- Method 2: Text-center on parent -->
<div class="text-center">
  <img src="..." alt="Centered">
</div>
```

### Responsive Float

Float at specific breakpoints:

```html
<!-- Float right on medium screens and up -->
<img src="..." class="float-md-end" alt="Responsive float">

<!-- Float left on large screens and up -->
<img src="..." class="float-lg-start" alt="Responsive float">
```

## Picture Element

Use the `<picture>` element with multiple sources for art direction or format selection:

```html
<picture>
  <source srcset="large.jpg" media="(min-width: 992px)">
  <source srcset="medium.jpg" media="(min-width: 768px)">
  <img src="small.jpg" class="img-fluid" alt="Responsive">
</picture>
```

**Key points:**

- Apply `.img-fluid` to the `<img>` fallback, not `<source>`
- Browser selects first matching `<source>` based on media query
- `<img>` element is required as fallback
- Useful for serving different crops at different sizes

### Format Selection

Serve modern formats with fallbacks:

```html
<picture>
  <source srcset="image.avif" type="image/avif">
  <source srcset="image.webp" type="image/webp">
  <img src="image.jpg" class="img-fluid" alt="Modern format">
</picture>
```

## Combining with Other Utilities

### Rounded Corners

```html
<img src="..." class="img-fluid rounded" alt="Rounded">
<img src="..." class="img-fluid rounded-circle" alt="Circle">
<img src="..." class="img-fluid rounded-pill" alt="Pill">
<img src="..." class="img-fluid rounded-3" alt="Larger radius">
```

### Shadows

```html
<img src="..." class="img-fluid shadow-sm" alt="Small shadow">
<img src="..." class="img-fluid shadow" alt="Regular shadow">
<img src="..." class="img-fluid shadow-lg" alt="Large shadow">
```

### Borders

```html
<img src="..." class="img-fluid border" alt="Default border">
<img src="..." class="img-fluid border border-primary border-3" alt="Styled border">
```

## Accessibility

- Always include meaningful `alt` text describing the image content
- Use empty `alt=""` for decorative images that screen readers should skip
- Ensure sufficient color contrast if text overlays images
- Consider users who disable images (alt text shows as fallback)

## Sass Variables

Key variables for customizing image thumbnails:

```scss
$thumbnail-padding:           .25rem;
$thumbnail-bg:                var(--#{$prefix}body-bg);
$thumbnail-border-width:      var(--#{$prefix}border-width);
$thumbnail-border-color:      var(--#{$prefix}border-color);
$thumbnail-border-radius:     var(--#{$prefix}border-radius);
$thumbnail-box-shadow:        var(--#{$prefix}box-shadow-sm);
```

### Customization Example

```scss
// Custom thumbnail styling
$thumbnail-padding: .5rem;
$thumbnail-border-radius: 1rem;
$thumbnail-box-shadow: 0 .5rem 1rem rgba(0, 0, 0, .15);
```
