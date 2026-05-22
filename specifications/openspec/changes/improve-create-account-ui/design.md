## Context

The current customer registration template `customer/register.html` uses a basic list of input fields inside a raw grid column layout without visual boundary isolation (such as a card/elevation background). It also places the registration submit button on the left, which deviates from standard checkout layout patterns (where actions are right-aligned). Finally, placing `th:text="#{labelHaveAccount}"` directly on the parent `<p>` container completely replaces its child elements, destroying the login redirection anchor `<a>` when localized property strings are applied in Swedish, German, and Japanese.

## Goals / Non-Goals

**Goals:**
- Wrap the registration form in a standard Bootstrap card component (`card shadow-sm border-0`).
- Center the registration container and restrict its size to `col-md-8 col-lg-6` to avoid stretching across wide viewports.
- Align the submit action button to the right of the actions row.
- Fix the localized "Already have an account?" text bug by isolating the text rendering block to keep the login link active.

**Non-Goals:**
- Changing database schemas, customer entities, or registration controller logic.
- Implementing custom CSS; we will rely solely on standard Bootstrap 5.x utility classes.

## Decisions

### 1. Unified Card Layout
- **Choice**: Wrap the form container in `col-md-8 col-lg-6` and style it with `card shadow-sm border-0` with padding `p-4 p-md-5`.
- **Alternative**: Keep the raw grid form.
- **Rationale**: A card-style container gives the page a premium, modern feel and aligns perfectly with other address/payment cards used in the checkout flow.

### 2. Actions Alignment via Flexbox
- **Choice**: Wrap the "Already have an account?" link/text block and the submit button in a container styled with `d-flex justify-content-between align-items-center mt-4`.
- **Alternative**: Right-align the button on its own line using `text-end`.
- **Rationale**: A flex row with space-between layout elegantly separates the redirection text/link on the left and the primary action button on the right, keeping them vertically aligned and easy to locate on both mobile and desktop.

### 3. Redirection Link Isolation
- **Choice**: Extract the translation hook from the parent `<p>` tag and put it on a child `<span>`:
  ```html
  <p class="mb-0">
      <span th:text="#{labelHaveAccount}">Already have an account?</span>
      <a th:href="@{'/' + ${urlLocale} + '/login'}" id="login-link" th:text="#{titleLogin}">Login</a>
  </p>
  ```
- **Alternative**: Embed HTML formatting directly in the properties files.
- **Rationale**: Storing HTML tags inside standard translation property files is a security risk (susceptible to HTML injection) and is highly prone to translation team editing errors. Separating the text node from the anchor element is clean and robust.

## Risks / Trade-offs

- **Risk**: Wide input fields look compressed in smaller card columns on intermediate tablet viewports.
- **Mitigation**: Using standard responsive columns `col-md-8 col-lg-6` ensures the card scales gracefully on medium to large screens, maintaining appropriate margins.
