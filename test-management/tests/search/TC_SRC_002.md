# Search — No Results

Verify that searching for a term that does not match any product in the catalog displays a user-friendly empty state with an appropriate message and a link to continue shopping.

## Metadata

- **Test ID:** TC_SRC_002
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `no-results`, `empty-state`, `localization`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> Use a clearly nonsensical term that could never appear in any product name, description, or indexed field to guarantee zero results.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.

## Test Data

| Field | Value |
| :--- | :--- |
| Search Term | `xyzzyplugh` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [x] DE-DE
- [x] SV-SE
- [x] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

---

## Steps

### 1. Navigate to the Store in the Target Locale

- **Action:** Open the Posters Demo Store homepage in the target locale.
- **Verify:** The store loads correctly in the expected locale.

### 2. Enter a Nonsensical Search Term

- **Action:** Type `xyzzyplugh` into the header search input field (`#header-search-text`).
- **Data:** `Search Term` = `xyzzyplugh`
- **Verify:** The input field accepts the text.

### 3. Submit the Search

- **Action:** Click the search button or press Enter.
- **Verify:** The browser navigates to the search results page.

### 4. Verify Empty State

- **Action:** Observe the search results page content.
- **Verify:** The following elements are visible:
  - A large search icon (magnifying glass, muted/faded style).
  - A heading with text similar to "No products found."
  - A descriptive message such as "We couldn't find anything matching your search. Try different keywords or browse our categories."
  - A **"Continue Shopping"** link/button that navigates back to the homepage.

### 5. Verify No Product Tiles Are Displayed

- **Action:** Inspect the product results area.
- **Verify:** The `#no-results` element is visible. The `#search-results` element is either absent or empty. No product tiles, images, or prices are rendered.

---

## Pass/Fail Criteria

- **Pass:** The empty state is displayed with the search icon, "No products found" message, explanatory text, and a "Continue Shopping" link. No product tiles appear.
- **Fail:** Product tiles are displayed for a nonsensical term; the empty state is missing any of its expected elements; the page returns an error or blank page; or the "Continue Shopping" link is broken.

---

## Postconditions

- The browser is on the search results page showing the empty state. No application state has been modified.

---

## Related Cases

- [TC_SRC_001: Simple Search — Single Term (Happy Path)](./TC_SRC_001.md)
- [TC_SRC_003: Search — Whitespace & Empty Query Handling](./TC_SRC_003.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
