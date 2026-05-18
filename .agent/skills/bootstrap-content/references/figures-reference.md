# Figures Reference

Complete reference for Bootstrap 5.3 figure classes and caption patterns.

## Class Reference

| Class | Element | Description |
|-------|---------|-------------|
| `.figure` | `<figure>` | Container wrapper with display: inline-block |
| `.figure-img` | `<img>` | Image styling with bottom margin |
| `.figure-caption` | `<figcaption>` | Caption styling with smaller font and muted color |

## Basic Usage

The figure component displays images with associated captions:

```html
<figure class="figure">
  <img src="..." class="figure-img img-fluid rounded" alt="...">
  <figcaption class="figure-caption">A caption for the image.</figcaption>
</figure>
```

**Key points:**

- `.figure` sets `display: inline-block` so the figure wraps its content
- `.figure-img` adds bottom margin to separate image from caption
- `.figure-caption` applies smaller font size and secondary color
- Combine with `.img-fluid` for responsive images
- Combine with `.rounded` or other border utilities as needed

## Caption Alignment

Use text utilities to align captions:

```html
<!-- Left aligned (default) -->
<figure class="figure">
  <img src="..." class="figure-img img-fluid rounded" alt="...">
  <figcaption class="figure-caption">Left-aligned caption.</figcaption>
</figure>

<!-- Center aligned -->
<figure class="figure">
  <img src="..." class="figure-img img-fluid rounded" alt="...">
  <figcaption class="figure-caption text-center">Centered caption.</figcaption>
</figure>

<!-- Right aligned -->
<figure class="figure">
  <img src="..." class="figure-img img-fluid rounded" alt="...">
  <figcaption class="figure-caption text-end">Right-aligned caption.</figcaption>
</figure>
```

## Centering Figures

Since `.figure` uses `display: inline-block`, center the figure using text alignment on the parent:

```html
<div class="text-center">
  <figure class="figure">
    <img src="..." class="figure-img img-fluid rounded" alt="...">
    <figcaption class="figure-caption">Centered figure.</figcaption>
  </figure>
</div>
```

Or convert to block and use auto margins:

```html
<figure class="figure d-block mx-auto" style="max-width: 400px;">
  <img src="..." class="figure-img img-fluid rounded" alt="...">
  <figcaption class="figure-caption text-center">Centered figure.</figcaption>
</figure>
```

## Multiple Images

Group related images in a single figure:

```html
<figure class="figure">
  <img src="before.jpg" class="figure-img img-fluid rounded" alt="Before">
  <img src="after.jpg" class="figure-img img-fluid rounded" alt="After">
  <figcaption class="figure-caption">Before and after comparison.</figcaption>
</figure>
```

## With Other Content

Figures can contain any content, not just images:

```html
<!-- Figure with code block -->
<figure class="figure">
  <pre class="bg-light p-3 rounded"><code>const greeting = "Hello";</code></pre>
  <figcaption class="figure-caption">Example JavaScript code.</figcaption>
</figure>

<!-- Figure with embedded content -->
<figure class="figure">
  <div class="ratio ratio-16x9">
    <iframe src="..." title="Video"></iframe>
  </div>
  <figcaption class="figure-caption">Video demonstration.</figcaption>
</figure>
```

## Combining with Utilities

### Styled Captions

```html
<figure class="figure">
  <img src="..." class="figure-img img-fluid rounded" alt="...">
  <figcaption class="figure-caption fst-italic">
    <small>Photo credit: Photographer Name</small>
  </figcaption>
</figure>
```

### Cards with Figures

```html
<div class="card">
  <figure class="figure mb-0">
    <img src="..." class="figure-img img-fluid card-img-top mb-0" alt="...">
    <figcaption class="figure-caption card-body">
      <p class="card-text">Caption as card body content.</p>
    </figcaption>
  </figure>
</div>
```

## Accessibility

### Semantic HTML

The `<figure>` and `<figcaption>` elements provide semantic meaning:

- Screen readers announce the relationship between image and caption
- Search engines understand the content structure
- No ARIA attributes needed when using semantic elements correctly

### Best Practices

```html
<!-- Good: Semantic structure with descriptive alt -->
<figure class="figure">
  <img src="chart.png" class="figure-img img-fluid"
       alt="Bar chart showing sales growth from 2020 to 2024">
  <figcaption class="figure-caption">
    Annual sales figures demonstrating 15% year-over-year growth.
  </figcaption>
</figure>

<!-- Good: Decorative image with empty alt -->
<figure class="figure">
  <img src="decorative-border.png" class="figure-img img-fluid" alt="">
  <figcaption class="figure-caption">Section divider</figcaption>
</figure>
```

**Guidelines:**

- Use descriptive `alt` text that complements (not duplicates) the caption
- Caption should add context or attribution, not repeat the alt text
- For complex images (charts, diagrams), provide detailed description in alt or nearby text
- Ensure sufficient color contrast for caption text

## Sass Variables

Key variables for customizing figures:

```scss
$figure-caption-font-size:    $small-font-size;
$figure-caption-color:        var(--#{$prefix}secondary-color);
```

### Customization Example

```scss
// Custom figure caption styling
$figure-caption-font-size: .875rem;
$figure-caption-color: var(--#{$prefix}gray-600);
```

### Additional Customization via CSS

For more control, override the generated CSS:

```css
.figure-caption {
  padding-top: 0.5rem;
  border-top: 1px solid var(--bs-border-color);
  font-style: italic;
}

.figure-img {
  margin-bottom: 1rem;
}
```
