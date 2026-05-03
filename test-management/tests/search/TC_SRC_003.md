# Search — Whitespace & Empty Query Handling

Verify that the search gracefully handles various forms of empty, whitespace-only, and whitespace-padded input without errors or unexpected results.

## Metadata

- **Test ID:** TC_SRC_003
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search
- **Priority:** 🟡 Medium
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `whitespace`, `empty-input`, `edge-case`, `robustness`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!CAUTION]
> Some whitespace characters (non-breaking space, tab, zero-width space) may be difficult to type directly into a browser input field. Use copy-paste from a text editor or browser developer tools to inject these characters.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.

## Test Data

| Scenario | Input Value | Expected Behavior |
| :--- | :--- | :--- |
| A — Empty input | *(empty, no characters)* | Empty results page, no errors |
| B — Single space | ` ` (one space) | Empty results page, no errors |
| C — Leading whitespace | `  bear` (spaces + term) | Same results as searching `bear` |
| D — Trailing whitespace | `bear  ` (term + spaces) | Same results as searching `bear` |
| E — Multiple spaces between words | `grizzly    bear` | Same results as searching `grizzly bear` |
| F — Non-breaking space (U+00A0) | `bear` with U+00A0 instead of regular space | Empty results or same as `bear`, no errors |
| G — Tab character (U+0009) | `bear` with tab instead of space | Empty results or same as `bear`, no errors |
| H — Zero-width space (U+200B) | `be​ar` with U+200B inserted | Empty results or same as `bear`, no errors |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [x] DE-DE
- [x] SV-SE
- [x] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [ ] Mobile (Small)

---

## Steps

### 1. Navigate to the Store

- **Action:** Open the Posters Demo Store homepage in the target locale.
- **Verify:** The store loads correctly.

### 2. Test Scenario A — Empty Input

- **Action:** Leave the search input field empty and click the search button or press Enter.
- **Verify:** The search results page loads without errors. Either the empty state is shown or a generic "Search Results" heading is displayed with no product tiles.

### 3. Test Scenario B — Single Space

- **Action:** Type a single space character into the search input and submit.
- **Verify:** Same behavior as Scenario A — empty results page, no errors, no product tiles.

### 4. Test Scenario C — Leading Whitespace

- **Action:** Type `  bear` (two leading spaces followed by `bear`) into the search input and submit.
- **Verify:** The search results page displays results for `bear`. The leading whitespace is trimmed. Results match what a clean `bear` search returns.

### 5. Test Scenario D — Trailing Whitespace

- **Action:** Type `bear  ` (the word `bear` followed by two trailing spaces) into the search input and submit.
- **Verify:** Results match a clean `bear` search. Trailing whitespace is trimmed.

### 6. Test Scenario E — Multiple Spaces Between Words

- **Action:** Type `grizzly    bear` (multiple spaces between words) into the search input and submit.
- **Verify:** Results match a clean `grizzly bear` search. Extra internal whitespace does not break the query or change the result set.

### 7. Test Scenario F — Non-Breaking Space (U+00A0)

- **Action:** Using copy-paste or developer tools, inject a non-breaking space (U+00A0) into the search input (e.g., `bear` preceded by U+00A0) and submit.
- **Verify:** No server error or crash. The page either returns empty results or treats the non-breaking space as a regular space.

### 8. Test Scenario G — Tab Character

- **Action:** Using copy-paste or developer tools, inject a tab character (U+0009) into the search input and submit.
- **Verify:** No server error or crash. The page either returns empty results or handles the tab gracefully.

### 9. Test Scenario H — Zero-Width Space (U+200B)

- **Action:** Using copy-paste or developer tools, inject a zero-width space (U+200B) inside a valid term (e.g., `be[U+200B]ar`) and submit.
- **Verify:** No server error or crash. The page either returns results for `bear` (if the zero-width space is stripped) or returns empty results.

---

## Pass/Fail Criteria

- **Pass:** All 8 scenarios complete without server errors, 500 pages, or application crashes. Empty/whitespace-only inputs return empty results gracefully. Whitespace-padded valid terms return correct results.
- **Fail:** Any scenario causes a server error, exception stack trace, or application crash. Whitespace-padded terms return different results than clean terms.

---

## Postconditions

- The browser is on the search results page. No application state has been modified.

---

## Related Cases

- [TC_SRC_001: Simple Search — Single Term (Happy Path)](./TC_SRC_001.md)
- [TC_SRC_009: Search — Input Boundary & Stress](./TC_SRC_009.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
