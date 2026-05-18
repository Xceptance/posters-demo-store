# Form Accessibility Checklist

Complete accessibility requirements for Bootstrap forms following WCAG 2.1 guidelines.

## Required Attributes Checklist

### Labels

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Every input has a label | Required | `<label for="id">` or `aria-label` |
| `for` matches input `id` | Required | `<label for="email">` + `id="email"` |
| Placeholder is not the only label | Required | Use `<label>` even with placeholder |
| Hidden labels use `.visually-hidden` | Required | Not `display: none` or `visibility: hidden` |

```html
<!-- Correct: visible label -->
<label for="email" class="form-label">Email</label>
<input type="email" class="form-control" id="email">

<!-- Correct: visually hidden label -->
<label for="search" class="visually-hidden">Search</label>
<input type="search" class="form-control" id="search" placeholder="Search...">

<!-- Incorrect: placeholder as only label -->
<input type="email" class="form-control" placeholder="Email">
```

### Help Text

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Help text connected to input | Required | `aria-describedby="helpId"` |
| Help text has unique `id` | Required | `id="passwordHelp"` |
| Multiple descriptions space-separated | Optional | `aria-describedby="help1 help2"` |

```html
<input type="password" class="form-control" id="password"
       aria-describedby="passwordHelp">
<div id="passwordHelp" class="form-text">
  Must be 8+ characters with one uppercase letter.
</div>
```

### Validation

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Invalid inputs marked | Required | `aria-invalid="true"` |
| Error messages connected | Required | `aria-describedby="errorId"` |
| Required fields indicated | Required | `required` + `aria-required="true"` |
| Focus moves to first error | Recommended | JavaScript focus management |

```html
<!-- Invalid input with error -->
<input type="email" class="form-control is-invalid" id="email"
       aria-invalid="true" aria-describedby="emailError" required aria-required="true">
<div id="emailError" class="invalid-feedback">
  Please enter a valid email address.
</div>
```

### Combined Help Text and Validation

When an input has both help text and validation feedback, reference both:

```html
<input type="email" class="form-control is-invalid" id="email"
       aria-describedby="emailHelp emailError" aria-invalid="true">
<div id="emailHelp" class="form-text">We'll never share your email.</div>
<div id="emailError" class="invalid-feedback">Please enter a valid email.</div>
```

Order matters: list IDs in reading order (help text first, then error).

## ARIA Attributes Reference

| Attribute | Purpose | When to Use |
|-----------|---------|-------------|
| `aria-describedby` | Connect descriptive text | Help text, error messages, instructions |
| `aria-invalid` | Indicate validation state | When input fails validation |
| `aria-required` | Indicate required field | All required inputs |
| `aria-label` | Provide accessible name | When no visible label exists |
| `aria-labelledby` | Reference visible label | Complex label scenarios |
| `aria-errormessage` | Point to error (alternative) | Instead of aria-describedby for errors |
| `aria-live` | Announce dynamic changes | Real-time validation feedback |

### aria-describedby Patterns

```html
<!-- Single description -->
<input aria-describedby="desc1">
<div id="desc1">Description text</div>

<!-- Multiple descriptions (space-separated) -->
<input aria-describedby="hint error">
<div id="hint">Format: MM/DD/YYYY</div>
<div id="error">Invalid date format</div>

<!-- Description order affects announcement order -->
<input aria-describedby="format requirements error">
```

### aria-live for Real-Time Feedback

```html
<div class="mb-3">
  <input type="password" class="form-control" id="password"
         aria-describedby="passwordStrength">
  <div id="passwordStrength" class="form-text" aria-live="polite">
    Password strength: weak
  </div>
</div>
```

Use `aria-live="polite"` for non-urgent updates, `aria-live="assertive"` for critical errors.

## Form Control Requirements

### Checkboxes and Radios

| Requirement | Implementation |
|-------------|----------------|
| Label associated with input | `<label for="checkId">` |
| Group labeled with fieldset | `<fieldset>` + `<legend>` |
| Radio groups share `name` | `name="radioGroup"` |

```html
<fieldset>
  <legend>Notification preferences</legend>
  <div class="form-check">
    <input class="form-check-input" type="checkbox" id="emailNotif">
    <label class="form-check-label" for="emailNotif">Email notifications</label>
  </div>
  <div class="form-check">
    <input class="form-check-input" type="checkbox" id="smsNotif">
    <label class="form-check-label" for="smsNotif">SMS notifications</label>
  </div>
</fieldset>
```

### Switches

```html
<div class="form-check form-switch">
  <input class="form-check-input" type="checkbox" role="switch" id="darkMode">
  <label class="form-check-label" for="darkMode">Enable dark mode</label>
</div>
```

The `role="switch"` ensures screen readers announce it as a switch, not a checkbox.

### Select Elements

```html
<label for="country" class="form-label">Country</label>
<select class="form-select" id="country" aria-describedby="countryHelp">
  <option value="">Choose a country</option>
  <option value="us">United States</option>
  <option value="ca">Canada</option>
</select>
<div id="countryHelp" class="form-text">Select your country of residence.</div>
```

### File Inputs

```html
<label for="resume" class="form-label">Upload resume</label>
<input class="form-control" type="file" id="resume"
       aria-describedby="resumeHelp" accept=".pdf,.doc,.docx">
<div id="resumeHelp" class="form-text">Accepted formats: PDF, DOC, DOCX. Max 5MB.</div>
```

## Focus Management

### On Validation Error

```javascript
form.addEventListener('submit', function(event) {
  if (!form.checkValidity()) {
    event.preventDefault();

    // Find first invalid input and focus it
    const firstInvalid = form.querySelector(':invalid');
    if (firstInvalid) {
      firstInvalid.focus();

      // Scroll into view if needed
      firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  }
});
```

### Announce Errors to Screen Readers

```html
<div id="formErrors" role="alert" aria-live="assertive" class="visually-hidden">
  <!-- Populated by JavaScript when errors occur -->
</div>

<script>
function announceErrors(errors) {
  const errorRegion = document.getElementById('formErrors');
  errorRegion.textContent = errors.length + ' errors found. ' +
    errors.map(e => e.field + ': ' + e.message).join('. ');
}
</script>
```

## Testing Procedures

### Keyboard Navigation

1. Tab through all form controls in logical order
2. Verify all controls are reachable via keyboard
3. Check that focus indicators are visible
4. Ensure no keyboard traps exist
5. Test Enter key submits form

### Screen Reader Testing

1. Each input announces its label
2. Help text is announced when input is focused
3. Required fields are announced as required
4. Invalid fields announce their error messages
5. Form errors are announced on submit

### Color Contrast

1. Labels meet 4.5:1 contrast ratio
2. Error messages meet 4.5:1 contrast ratio
3. Validation states don't rely solely on color
4. Focus indicators are clearly visible

### Automated Testing

```bash
# Using axe-core
npx @axe-core/cli https://your-site.com/form-page

# Using pa11y
npx pa11y https://your-site.com/form-page

# Using Lighthouse
npx lighthouse https://your-site.com/form-page --only-categories=accessibility
```

## Common Anti-Patterns

| Anti-Pattern | Problem | Solution |
|--------------|---------|----------|
| Placeholder as label | Not persistent, low contrast | Always use `<label>` |
| Validation on color only | Inaccessible to colorblind users | Add icons, text, or patterns |
| Missing `aria-invalid` | Screen readers don't know input is invalid | Add `aria-invalid="true"` |
| Generic error messages | "Invalid input" doesn't help users | Be specific: "Email must include @" |
| No focus management | Users don't know where errors are | Focus first error on submit |
| Timeout without warning | Users may lose form data | Warn before timeout, extend on activity |

## Quick Reference Card

```html
<!-- Fully accessible input pattern -->
<div class="mb-3">
  <label for="email" class="form-label">
    Email address <span class="text-danger">*</span>
  </label>
  <input type="email"
         class="form-control"
         id="email"
         required
         aria-required="true"
         aria-describedby="emailHelp">
  <div id="emailHelp" class="form-text">
    We'll never share your email with anyone.
  </div>
</div>

<!-- With validation error -->
<div class="mb-3">
  <label for="email" class="form-label">
    Email address <span class="text-danger">*</span>
  </label>
  <input type="email"
         class="form-control is-invalid"
         id="email"
         required
         aria-required="true"
         aria-invalid="true"
         aria-describedby="emailHelp emailError">
  <div id="emailHelp" class="form-text">
    We'll never share your email with anyone.
  </div>
  <div id="emailError" class="invalid-feedback">
    Please enter a valid email address.
  </div>
</div>
```
