# Search Result Count & Heading

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| Antigravity (AI) | 2026-05-03 | `❌ FAILED` | Localhost / Chrome 144 | 'Swedish Moose' 0 results (test data wrong?). 'cat' returns false positives via stemming ('catch'→'cat'). 'a' returns 95 not 124 (stop word filtering?). DE heading not localized. |


Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar. This test requires specific queries that produce exactly 1 result, a small number of results, and a larger number of results to validate the count display across cardinalities.

## Metadata

- **Test ID:** TC_SRC_008
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search
- **Priority:** 🟡 Medium
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `result-count`, `heading`, `singular-plural`, `UI`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> The heading format is: `Results for '{searchTerm}' (N posters)` for plural, `Results for '{searchTerm}' (1 poster)` for singular. Pay close attention to the singular/plural of the word "poster"/"posters".

> [!CAUTION]
> The exact result count may change if catalog data is modified. Before running this test, verify the expected counts by performing the searches and counting the actual results. Update the Test Data table if counts have changed.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.

## Test Data

| Scenario | Locale | Search Term | Expected Count | Expected Heading Format |
| :--- | :--- | :--- | :--- | :--- |
| A — Singular (1 result) | en-US | `Swedish Moose` | 1 | `Results for 'Swedish Moose' (1 poster)` |
| B — Few results | en-US | `cat` | 2+ | `Results for 'cat' (N posters)` where N ≥ 2 |
| C — Many results | en-US | `a` | Many | `Results for 'a' (N posters)` where N is a larger number |
| D — Singular (de-DE) | de-DE | `Schwedischer Elch` | 1 | `Results for 'Schwedischer Elch' (1 poster)` |
| E — Few results (de-DE) | de-DE | `Katze` | 2+ | `Results for 'Katze' (N posters)` where N ≥ 2 |

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

### 1. Navigate to the Store in the Target Locale

- **Action:** Open the Posters Demo Store homepage in the target locale.
- **Verify:** The store loads correctly.

### 2. Scenario A — Singular Result Count

- **Action:** Type the Scenario A search term into the header search input and submit.
- **Data:** `Swedish Moose` (en-US)
- **Verify:** The heading displays: `Results for 'Swedish Moose' (1 poster)`. The word **"poster"** is singular. Exactly 1 product tile is visible.

### 3. Verify Count Matches Visible Tiles

- **Action:** Count the visible product tiles on the page.
- **Verify:** The number of visible tiles matches the number shown in the heading (1).

### 4. Scenario B — Few Results (Plural)

- **Action:** Clear the search input. Type the Scenario B search term and submit.
- **Data:** `cat` (en-US)
- **Verify:** The heading displays: `Results for 'cat' (N posters)` where N ≥ 2. The word **"posters"** is plural. Count the visible tiles — the count matches N.

### 5. Scenario C — Many Results

- **Action:** Clear the search input. Type the Scenario C search term and submit.
- **Data:** `a` (en-US)
- **Verify:** The heading displays: `Results for 'a' (N posters)` where N is a larger number. Count the visible tiles — the count matches N.

### 6. Repeat Singular/Plural Check in de-DE

- **Action:** Switch to the de-DE locale. Repeat Scenarios D and E using the German search terms from the Test Data table.
- **Verify:** The heading format is correct with the German search term. Singular/plural distinction is maintained. Visible tile count matches the heading count.

---

## Pass/Fail Criteria

- **Pass:** The heading correctly displays the search term in quotes, the exact result count, and proper singular ("poster") / plural ("posters") grammar. The visible tile count matches the count in the heading across all scenarios.
- **Fail:** The heading shows an incorrect count. The singular/plural grammar is wrong (e.g., "1 posters" or "3 poster"). The visible tile count does not match the heading count. The search term in the heading does not match the input.

---

## Postconditions

- The browser is on the search results page. No application state has been modified.

---

## Related Cases

- [TC_SRC_001: Simple Search — Single Term (Happy Path)](./TC_SRC_001.md)
- [TC_SRC_007: Search Result Correctness & Navigation](./TC_SRC_007.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
