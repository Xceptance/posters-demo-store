# Search — Input Boundary & Stress

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| Antigravity (AI) | 2026-05-03 | `✅ PASSED` | Localhost / Chrome 144 | 1700 chars OK (UI slightly off due to long heading). 7000 chars → 400 Bad Request (acceptable). XSS/SQLi properly escaped. |


Verify that the search handles extreme and unusual input gracefully without server errors, application crashes, or security vulnerabilities. This covers very long inputs, single characters, numeric-only input, emoji, and HTML/script injection attempts.

## Metadata

- **Test ID:** TC_SRC_009
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search
- **Priority:** 🟡 Medium
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `boundary`, `stress`, `security`, `edge-case`, `robustness`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!CAUTION]
> Scenario F (HTML/script injection) is a basic XSS smoke test. It does NOT replace a proper security audit. The goal is to verify the input is not rendered as executable HTML on the results page.

> [!TIP]
> For very long input scenarios, use a text editor to prepare the string and paste it into the search field. Copy a paragraph of Lorem Ipsum or repeat a known word many times.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.

## Test Data

| Scenario | Input Description | Input Value | Expected Behavior |
| :--- | :--- | :--- | :--- |
| A — Long input (500 chars) | 500+ character string | Repeat `bear ` (with space) 100 times | No errors; returns results or empty state |
| B — Very long input (1000+ chars) | 1000+ character string | Repeat `bear ` (with space) 250 times | No errors; no timeout; returns results or empty state |
| C — Single character | Minimum viable input | `a` | Returns results (many products likely match); no errors |
| D — Numeric-only input | Numbers only, no letters | `12345` | No errors; likely empty results (prices are not indexed) |
| E — Emoji input | Unicode emoji | `🐻` | No errors; empty results or matches if any description contains the emoji |
| F — HTML/script injection | Malicious HTML | `<script>alert(1)</script>` | No errors; input is escaped/sanitized; no JS execution; the literal text may appear in the heading but NOT as executable HTML |
| G — SQL injection attempt | SQL-like input | `'; DROP TABLE products; --` | No errors; input is treated as literal text; returns empty results |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [ ] DE-DE
- [ ] SV-SE
- [ ] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [ ] Mobile (Small)

---

## Steps

### 1. Navigate to the Store

- **Action:** Open the Posters Demo Store homepage in en-US locale.
- **Verify:** The store loads correctly.

### 2. Scenario A — Long Input (500+ Characters)

- **Action:** Prepare a string of 500+ characters (e.g., `bear ` repeated 100 times). Paste it into the header search input and submit.
- **Verify:** The search completes without a server error, 500 page, or timeout. The results page loads (showing results or empty state). The heading displays a truncated or full version of the query.

### 3. Scenario B — Very Long Input (1000+ Characters)

- **Action:** Prepare a string of 1000+ characters (e.g., `bear ` repeated 250 times). Paste it into the search input and submit.
- **Verify:** Same as Scenario A — no errors, no timeout. The application does not crash.

### 4. Scenario C — Single Character

- **Action:** Type `a` into the search input and submit.
- **Verify:** The search returns results (likely many products whose names or descriptions contain words starting with "a"). No errors.

### 5. Scenario D — Numeric-Only Input

- **Action:** Type `12345` into the search input and submit.
- **Verify:** The search completes without errors. Likely returns empty results since prices are not indexed and product names/descriptions rarely contain bare numbers.

### 6. Scenario E — Emoji Input

- **Action:** Type or paste `🐻` (bear emoji) into the search input and submit.
- **Verify:** The search completes without errors. The heading may display the emoji. Results are either empty or contain matches if any product text includes the emoji character.

### 7. Scenario F — HTML/Script Injection

- **Action:** Type `<script>alert(1)</script>` into the search input and submit.
- **Verify:**
  - **No JavaScript alert dialog appears.**
  - The page renders normally.
  - The heading displays the input as **escaped/sanitized text**, not as executable HTML.
  - View the page source or use developer tools to confirm the `<script>` tags are HTML-encoded (e.g., `&lt;script&gt;`).

### 8. Scenario G — SQL Injection Attempt

- **Action:** Type `'; DROP TABLE products; --` into the search input and submit.
- **Verify:** The search completes without errors. The application continues to function normally. The input is treated as literal text by the Lucene query parser (all special characters are escaped).

---

## Pass/Fail Criteria

- **Pass:** All 7 scenarios complete without server errors (500 page), application crashes, timeouts, or security vulnerabilities. Long inputs are handled gracefully. Injection attempts are neutralized.
- **Fail:** Any scenario causes a server error, stack trace, application crash, or timeout. HTML/script injection executes JavaScript. SQL injection affects the database.

---

## Postconditions

- The browser is on the search results page. No application state has been modified or corrupted.

---

## Related Cases

- [TC_SRC_003: Search — Whitespace & Empty Query Handling](./TC_SRC_003.md)
- [TC_SRC_002: Search — No Results](./TC_SRC_002.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
