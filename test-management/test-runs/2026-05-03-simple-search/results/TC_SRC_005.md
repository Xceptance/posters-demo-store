# Partial / Prefix Search

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| Antigravity (AI) | 2026-05-03 | `❌ FAILED` | Localhost / Chrome 144 | 'griz' (US) and 'モルフォ' (JP) failed. SE and DE worked. Inconsistent prefix matching across locales. |


Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix. This validates the wildcard (`*`) appending behavior of the search engine across all supported locales.

## Metadata

- **Test ID:** TC_SRC_005
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `prefix`, `partial`, `type-ahead`, `wildcard`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> The search engine appends a wildcard `*` to the escaped query before parsing, so `griz` becomes `griz*` internally. This enables partial matching. However, the wildcard is only appended to the **last** token, so in multi-word partial queries, only the last word gets prefix-expanded.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.

## Test Data

| Locale | Partial Term | Full Product Name Match | Minimum Expected Results |
| :--- | :--- | :--- | :--- |
| en-US | `griz` | `Grizzly Bear` | 1 |
| en-US | `but` | `Blue Morpho Butterfly` | 1 |
| en-US | `dah` | `Pink Dahlia`, `Cactus Dahlia Pink White` | 2 |
| de-DE | `Grizz` | `Grizzlybär` | 1 |
| de-DE | `Schmet` | `Blauer Morphofalter`, `Schmetterling auf blauer Blüte` | 1 |
| sv-SE | `björ` | `Grizzlybjörn` | 1 |
| ja-JP | `ダリ` | `ピンクのダリア`, `サボテン ダリア ピンク ホワイト` | 1 |

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

### 2. Enter a Partial Search Term

- **Action:** Type the **Partial Term** from the Test Data table for the current locale into the header search input and submit.
- **Data:** Use each row from the Test Data table for the current locale.
- **Verify:** The search results page loads without errors.

### 3. Verify Prefix Matching Returns Results

- **Action:** Observe the search results.
- **Verify:** At least the **Minimum Expected Results** count of product tiles are displayed. The **Full Product Name Match** column product(s) appear in the results.

### 4. Repeat for Each Partial Term in the Current Locale

- **Action:** Repeat Steps 2-3 for each partial term row in the Test Data table that applies to the current locale.
- **Verify:** Each partial term returns relevant results containing the expected products.

### 5. Verify Single-Character Prefix

- **Action:** Type a single character (e.g., `b` in en-US) into the search input and submit.
- **Verify:** The search returns results (potentially many). No errors occur. This validates that very short prefixes are handled.

---

## Pass/Fail Criteria

- **Pass:** All partial terms return results containing the expected product(s). Single-character prefixes are handled without errors. Results are relevant to the prefix typed.
- **Fail:** A partial term returns zero results despite matching products existing in the catalog. The search errors or crashes on short prefixes.

---

## Postconditions

- The browser is on the search results page. No application state has been modified.

---

## Related Cases

- [TC_SRC_001: Simple Search — Single Term (Happy Path)](./TC_SRC_001.md)
- [TC_SRC_004: Multi-Word Search (AND Behavior)](./TC_SRC_004.md)
- [TC_SRC_006: Case-Insensitive Search](./TC_SRC_006.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
