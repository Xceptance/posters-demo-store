# Keyboard Navigation & Accessibility

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| Antigravity (AI) | 2026-05-03 | `✅ PASSED` | Localhost / Chrome 144 | Tab into input, Enter submit, Tab to button + Space submit all work. Focus outlines visible. |


Verify that a user can fully utilize the search functionality using only a keyboard. This includes navigating to the search input, typing a term, submitting via the Enter key or tabbing to the search button and activating it with Space/Enter.

## Metadata

- **Test ID:** TC_SRC_010
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search, Accessibility
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - WCAG 2.1 - 2.1.1 Keyboard
- **Tags:** `search`, `keyboard`, `a11y`, `navigation`
- **Author:** Antigravity (AI) (2026-05-03)
- **Reviewers:**
  - N/A

## Preconditions

- The Posters Demo Store is running.
- The user is on the homepage.
- The browser focus is reset (e.g., click once on the browser's address bar).

## Test Data

| Field | Value |
| :--- | :--- |
| Search Term | `bear` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE
- [x] JA-JP

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. Tab into the Search Input

- **Action:** Using only the `Tab` key (and `Shift+Tab` if needed), navigate forward through the page elements until the search input field is focused.
- **Verify:** The search input (`#header-search-text`) receives visible focus (e.g., a focus ring or outline).

### 2. Enter Search Term

- **Action:** Type the **Search Term** into the focused input field.
- **Verify:** The text is correctly entered into the field.

### 3. Submit via Enter Key

- **Action:** Press the `Enter` key on the keyboard while the input is still focused.
- **Verify:** The search form submits, and the browser navigates to the search results page showing results for the term.

### 4. Tab to the Search Button

- **Action:** Return to the homepage. Use the `Tab` key to navigate to the search input field, type the search term, and then press `Tab` **once more**.
- **Verify:** The search button (magnifying glass icon, `#header-search-button`) receives visible focus.

### 5. Submit via Spacebar / Enter on Button

- **Action:** While the search button is focused, press the `Space` bar (or `Enter` key).
- **Verify:** The search form submits, and the browser navigates to the search results page.

---

## Pass/Fail Criteria

- **Pass:** The user can tab into the search field, type, and submit the search using *both* the Enter key in the input field AND by tabbing to the search button and pressing Space/Enter. Focus outlines must be visible.
- **Fail:** The search input or button cannot be reached via the Tab key. Submitting via the Enter key or Space bar fails. Focus is invisible or trapped.

---

## Postconditions

- The browser is on the search results page. No application state has been modified.

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-03 | 1.0 | Antigravity (AI) | Initial creation |
