# Form Classes Reference

Complete reference for Bootstrap 5.3 form classes.

## Form Control Classes

| Class | Description |
|-------|-------------|
| `.form-control` | Style text inputs, textareas |
| `.form-control-lg` | Large input |
| `.form-control-sm` | Small input |
| `.form-control-plaintext` | Readonly plain text |
| `.form-control-color` | Color picker input |

## Form Label Classes

| Class | Description |
|-------|-------------|
| `.form-label` | Standard label styling |
| `.col-form-label` | Label for horizontal forms |
| `.col-form-label-lg` | Large horizontal label |
| `.col-form-label-sm` | Small horizontal label |

## Select Classes

| Class | Description |
|-------|-------------|
| `.form-select` | Style select elements |
| `.form-select-lg` | Large select |
| `.form-select-sm` | Small select |

## Checkbox & Radio Classes

| Class | Description |
|-------|-------------|
| `.form-check` | Wrapper for check/radio |
| `.form-check-input` | Checkbox/radio input |
| `.form-check-label` | Label for checkbox/radio |
| `.form-check-inline` | Inline checkboxes/radios |
| `.form-check-reverse` | Reverse order (label first) |

Set indeterminate state via JavaScript for "select all" patterns:

```javascript
document.getElementById('checkbox').indeterminate = true;
```

## Switch Classes

| Class | Description |
|-------|-------------|
| `.form-switch` | Add to `.form-check` for switch style |

For iOS 17.4+ Safari, add the `switch` attribute for native haptic feedback:

```html
<input class="form-check-input" type="checkbox" role="switch" switch>
```

## Toggle Button Classes

| Class | Description |
|-------|-------------|
| `.btn-check` | Hide input, enable toggle behavior |

Use with `.btn` and `.btn-*` classes on the associated `<label>`:

```html
<input type="checkbox" class="btn-check" id="toggle" autocomplete="off">
<label class="btn btn-primary" for="toggle">Toggle</label>
```

Outlined variants work with `.btn-outline-*` classes for a lighter appearance.

## Range Classes

| Class | Description |
|-------|-------------|
| `.form-range` | Style range inputs |

## Input Group Classes

| Class | Description |
|-------|-------------|
| `.input-group` | Wrapper for input groups |
| `.input-group-text` | Addon text styling |
| `.input-group-lg` | Large input group |
| `.input-group-sm` | Small input group |
| `.flex-nowrap` | Prevent input group wrapping |
| `.has-validation` | Enable validation feedback in input groups |

Hidden elements (`.d-none`) in first/last position break border-radius styling—a known Bootstrap limitation.

## Floating Label Classes

| Class | Description |
|-------|-------------|
| `.form-floating` | Wrapper for floating labels |

## Validation Classes

| Class | Description |
|-------|-------------|
| `.needs-validation` | Enable custom validation |
| `.was-validated` | Show validation state |
| `.is-valid` | Valid state on input |
| `.is-invalid` | Invalid state on input |
| `.valid-feedback` | Valid feedback message |
| `.invalid-feedback` | Invalid feedback message |
| `.valid-tooltip` | Valid tooltip message (positioned) |
| `.invalid-tooltip` | Invalid tooltip message (positioned) |

### Feedback vs. Tooltips

- **Feedback** (`.valid-feedback`, `.invalid-feedback`): Inline messages that flow with form layout
- **Tooltips** (`.valid-tooltip`, `.invalid-tooltip`): Positioned absolutely, require parent `position: relative`

Use tooltips when feedback messages would disrupt layout or for a more prominent visual indicator.

## Form Text

| Class | Description |
|-------|-------------|
| `.form-text` | Help text below inputs |

## Disabled States

```html
<fieldset disabled>
  <!-- All inputs inside are disabled -->
</fieldset>
```

## Input Types Supported

- `text`
- `password`
- `email`
- `number`
- `tel`
- `url`
- `search`
- `date`
- `datetime-local`
- `time`
- `week`
- `month`
- `color`
- `file`
- `range`

## Datalist

```html
<input class="form-control" list="options">
<datalist id="options">
  <option value="Option 1">
  <option value="Option 2">
</datalist>
```

## Form Layout Classes

### Row/Column Layout

```html
<div class="row mb-3">
  <div class="col-md-6">
    <input class="form-control">
  </div>
</div>
```

### Gutters

```html
<div class="row g-3">
  <!-- Form fields with gutters -->
</div>
```

## Accessibility Attributes

Always include for accessibility:

```html
<!-- Labels -->
<label for="inputId" class="form-label">Label</label>
<input id="inputId" class="form-control">

<!-- Hidden labels (screen readers only) -->
<label for="inputId" class="visually-hidden">Label</label>

<!-- aria-describedby for help text -->
<input aria-describedby="helpText">
<div id="helpText" class="form-text">Help text</div>

<!-- Required fields -->
<input required aria-required="true">

<!-- Invalid fields -->
<input class="is-invalid" aria-invalid="true">
<div class="invalid-feedback" id="error">Error message</div>
```

## Common Form Patterns

### Login Form

```html
<form>
  <div class="mb-3">
    <label for="email" class="form-label">Email</label>
    <input type="email" class="form-control" id="email" required>
  </div>
  <div class="mb-3">
    <label for="password" class="form-label">Password</label>
    <input type="password" class="form-control" id="password" required>
  </div>
  <div class="mb-3 form-check">
    <input type="checkbox" class="form-check-input" id="remember">
    <label class="form-check-label" for="remember">Remember me</label>
  </div>
  <button type="submit" class="btn btn-primary">Sign in</button>
</form>
```

### Search Form

```html
<form class="d-flex" role="search">
  <input class="form-control me-2" type="search" placeholder="Search">
  <button class="btn btn-outline-success" type="submit">Search</button>
</form>
```

### Contact Form

```html
<form>
  <div class="row g-3">
    <div class="col-md-6">
      <label for="name" class="form-label">Name</label>
      <input type="text" class="form-control" id="name" required>
    </div>
    <div class="col-md-6">
      <label for="email" class="form-label">Email</label>
      <input type="email" class="form-control" id="email" required>
    </div>
    <div class="col-12">
      <label for="subject" class="form-label">Subject</label>
      <input type="text" class="form-control" id="subject">
    </div>
    <div class="col-12">
      <label for="message" class="form-label">Message</label>
      <textarea class="form-control" id="message" rows="4" required></textarea>
    </div>
    <div class="col-12">
      <button type="submit" class="btn btn-primary">Send Message</button>
    </div>
  </div>
</form>
```
