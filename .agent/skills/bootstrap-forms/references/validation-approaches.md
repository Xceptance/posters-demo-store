# Form Validation Approaches

Compare Bootstrap's three validation approaches to choose the right one for your use case.

## Approach Comparison

| Aspect | Browser Default | Bootstrap Custom | Server-Side |
|--------|-----------------|------------------|-------------|
| **Feedback appearance** | Browser-native popups | Bootstrap-styled inline messages | Bootstrap-styled inline messages |
| **When validates** | On submit | On submit (after `was-validated`) | After server response |
| **JavaScript required** | No | Yes (to add `was-validated`) | Yes (AJAX) or full page reload |
| **Customizable messages** | Limited (via `setCustomValidity`) | Full control | Full control |
| **Works without JS** | Yes | Partial (native fallback) | Yes (with page reload) |
| **Best for** | Simple forms, progressive enhancement | Most production forms | Complex validation, database checks |

## Browser Default Validation

Uses HTML5 constraint validation with browser-native feedback UI.

```html
<form>
  <input type="email" required>
  <input type="text" pattern="[A-Za-z]{3,}" title="3+ letters">
  <button type="submit">Submit</button>
</form>
```

**Pros:**

- Zero JavaScript required
- Accessible by default
- Consistent with platform conventions

**Cons:**

- Inconsistent styling across browsers
- Limited message customization
- Cannot style the validation popups

## Bootstrap Custom Validation

Uses Bootstrap's `.is-valid`/`.is-invalid` classes with styled feedback messages.

```html
<form class="needs-validation" novalidate>
  <div class="mb-3">
    <label for="email" class="form-label">Email</label>
    <input type="email" class="form-control" id="email" required>
    <div class="valid-feedback">Looks good!</div>
    <div class="invalid-feedback">Please enter a valid email.</div>
  </div>
  <button type="submit" class="btn btn-primary">Submit</button>
</form>

<script>
(function () {
  'use strict';
  document.querySelectorAll('.needs-validation').forEach(function (form) {
    form.addEventListener('submit', function (event) {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add('was-validated');
    }, false);
  });
})();
</script>
```

**Key points:**

- Add `novalidate` to `<form>` to disable browser validation UI
- Add `.needs-validation` for JavaScript targeting
- JavaScript adds `.was-validated` on submit to show feedback
- Validation feedback only shows after `.was-validated` is applied

**Pros:**

- Consistent styling across browsers
- Full control over messages
- Matches Bootstrap design system

**Cons:**

- Requires JavaScript
- Must handle accessibility manually (`aria-invalid`, `aria-describedby`)

> **Accessibility warning:** Bootstrap's custom validation styles and tooltips (`.valid-tooltip` / `.invalid-tooltip`) are **not accessible** to assistive technologies. Validation tooltips rely on CSS positioning and are not announced by screen readers. Always pair visual feedback with `aria-invalid="true"` and `aria-describedby` pointing to a visible `.valid-feedback` / `.invalid-feedback` element to ensure all users receive error information.

## Server-Side Validation

Validates on the server and returns results via AJAX or page reload.

### With AJAX

```html
<form id="serverForm">
  <div class="mb-3">
    <label for="username" class="form-label">Username</label>
    <input type="text" class="form-control" id="username" name="username" required>
    <div class="invalid-feedback">Username already taken.</div>
  </div>
  <button type="submit" class="btn btn-primary">Submit</button>
</form>

<script>
document.getElementById('serverForm').addEventListener('submit', async function(event) {
  event.preventDefault();
  const form = event.target;
  const formData = new FormData(form);

  try {
    const response = await fetch('/api/validate', {
      method: 'POST',
      body: formData
    });
    const result = await response.json();

    // Clear previous validation states
    form.querySelectorAll('.is-invalid, .is-valid').forEach(el => {
      el.classList.remove('is-invalid', 'is-valid');
      el.removeAttribute('aria-invalid');
    });

    if (result.errors) {
      // Apply validation errors
      Object.entries(result.errors).forEach(([field, message]) => {
        const input = form.querySelector(`[name="${field}"]`);
        const feedback = input.nextElementSibling;
        input.classList.add('is-invalid');
        input.setAttribute('aria-invalid', 'true');
        if (feedback) feedback.textContent = message;
      });
    } else {
      // Success - submit or redirect
      form.submit();
    }
  } catch (error) {
    console.error('Validation failed:', error);
  }
});
</script>
```

### With Page Reload (PHP example)

```php
<?php
$errors = [];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (empty($_POST['email'])) {
        $errors['email'] = 'Email is required.';
    }
}
?>

<form method="POST">
  <div class="mb-3">
    <label for="email" class="form-label">Email</label>
    <input type="email"
           class="form-control <?= isset($errors['email']) ? 'is-invalid' : '' ?>"
           id="email"
           name="email"
           value="<?= htmlspecialchars($_POST['email'] ?? '') ?>"
           <?= isset($errors['email']) ? 'aria-invalid="true" aria-describedby="emailError"' : '' ?>>
    <?php if (isset($errors['email'])): ?>
      <div id="emailError" class="invalid-feedback"><?= $errors['email'] ?></div>
    <?php endif; ?>
  </div>
  <button type="submit" class="btn btn-primary">Submit</button>
</form>
```

**Pros:**

- Validates business logic (uniqueness, authorization)
- Cannot be bypassed by users
- Required for security-critical validation

**Cons:**

- Slower feedback (network round-trip)
- More complex implementation
- Requires backend integration

## Combining Approaches

Best practice: use client-side validation for immediate feedback, server-side for security.

```html
<form class="needs-validation" novalidate>
  <!-- Client-side: immediate feedback -->
  <input type="email" class="form-control" id="email" required>

  <!-- Server-side: applied after AJAX response -->
  <input type="text" class="form-control" id="username">
</form>
```

1. Client-side catches obvious errors instantly
2. Server-side validates business rules and security
3. Both update the same `.is-valid`/`.is-invalid` classes

## Validation Patterns

### Real-Time Validation (On Input)

```javascript
document.getElementById('email').addEventListener('input', function(event) {
  const input = event.target;
  if (input.checkValidity()) {
    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
  } else {
    input.classList.remove('is-valid');
    input.classList.add('is-invalid');
  }
});
```

### Validate on Blur

```javascript
document.querySelectorAll('.form-control').forEach(function(input) {
  input.addEventListener('blur', function() {
    if (this.value) {
      this.classList.toggle('is-valid', this.checkValidity());
      this.classList.toggle('is-invalid', !this.checkValidity());
    }
  });
});
```

### Clear Validation on Focus

```javascript
document.querySelectorAll('.form-control').forEach(function(input) {
  input.addEventListener('focus', function() {
    this.classList.remove('is-valid', 'is-invalid');
  });
});
```

## Accessibility Requirements

All validation approaches must include:

| Requirement | Implementation |
|-------------|----------------|
| Error announcement | `aria-invalid="true"` on invalid inputs |
| Error description | `aria-describedby` pointing to feedback |
| Focus management | Move focus to first error on submit |
| Color independence | Don't rely solely on red/green colors |

See `accessibility-checklist.md` for complete requirements.
