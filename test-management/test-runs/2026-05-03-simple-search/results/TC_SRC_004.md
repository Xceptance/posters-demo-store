# Multi-Word Search (AND Behavior)

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| Antigravity (AI) | 2026-05-03 | `❌ FAILED` | Localhost / Chrome 144 | 'grizzly' and 'bear grizzly' not found. Full description paste fails. 'north owl great' works. |


Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms. Also verify that term order does not affect results, that queries with 3+ terms work correctly, and that pasting a full product description returns the matching product.

## Metadata

- **Test ID:** TC_SRC_004
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `multi-word`, `AND-operator`, `term-order`, `description-paste`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!IMPORTANT]
> The search engine escapes all Lucene special characters and uses AND as the default operator. This means every word in the query must appear in at least one of the indexed fields (name, short description, long description) for a product to match. The wildcard `*` is appended for prefix matching.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.
- The tester has access to a product's full long description text for Scenario D (available in the catalog data).

## Test Data

### Scenario A — Two-Word Query (AND Narrowing)

| Field | en-US | de-DE | sv-SE | ja-JP |
| :--- | :--- | :--- | :--- | :--- |
| Search Term | `grizzly bear` | `Europäische Hornisse` | `svensk älg` | `ブルーモルフォ蝶` |
| Expected Product | `Grizzly Bear` | `Europäische Hornisse` | `Svensk älg` | `ブルーモルフォ蝶` |

### Scenario B — Reversed Term Order

| Field | en-US | de-DE |
| :--- | :--- | :--- |
| Search Term | `bear grizzly` | `Hornisse Europäische` |
| Expected | Same results as Scenario A | Same results as Scenario A |

### Scenario C — Three or More Terms

| Field | en-US |
| :--- | :--- |
| Search Term | `grey owl north` |
| Expected Product | `Great Grey Owl` (long description mentions "North America") |

### Scenario D — Full Description Paste

| Field | en-US |
| :--- | :--- |
| Search Term | `Grizzly bears are severe predators and shouldn't be underestimated only because of their harmless and cute looks like this picture displays them` |
| Expected Product | `Grizzly Bear` |

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
- **Verify:** The store loads correctly.

### 2. Scenario A — Two-Word Query

- **Action:** Type the two-word **Search Term** from Scenario A into the header search input and submit.
- **Data:** Scenario A search term for the current locale.
- **Verify:** The search results page displays results. The **Expected Product** from Scenario A appears in the results. The result count should be **equal to or less than** the count returned by searching for either single word alone (AND narrowing effect).

### 3. Verify AND Narrowing

- **Action:** Perform a separate search for just the first word of the Scenario A term (e.g., `grizzly` in en-US).
- **Verify:** The single-word search returns **more or equal** results compared to the two-word search from Step 2. This confirms the AND operator narrows the result set.

### 4. Scenario B — Reversed Term Order

- **Action:** Type the **reversed** search term from Scenario B into the search input and submit.
- **Data:** Scenario B search term for the current locale.
- **Verify:** The results are **identical** to Scenario A — same products, same count. Term order does not affect results.

### 5. Scenario C — Three or More Terms

- **Action:** Type the three-word search term from Scenario C into the search input and submit.
- **Data:** `grey owl north`
- **Verify:** The **Expected Product** (`Great Grey Owl`) appears in the results. The result count is further narrowed compared to a two-word search.

### 6. Scenario D — Full Description Paste

- **Action:** Copy the full long description text from Scenario D and paste it into the search input. Submit the search.
- **Data:** Full description text from Scenario D.
- **Verify:** The search completes without errors. The **Expected Product** (`Grizzly Bear`) appears in the results. The long input does not cause a crash or timeout.

---

## Pass/Fail Criteria

- **Pass:** Multi-word queries return results matching ALL terms (AND behavior). Reversed term order produces identical results. 3+ term queries further narrow results. Full description paste returns the matching product without errors.
- **Fail:** Multi-word queries return results matching only one term (OR behavior). Reversed terms produce different results. 3+ term queries fail or error. Full description paste causes a crash, timeout, or server error.

---

## Postconditions

- The browser is on the search results page. No application state has been modified.

---

## Related Cases

- [TC_SRC_001: Simple Search — Single Term (Happy Path)](./TC_SRC_001.md)
- [TC_SRC_005: Partial / Prefix Search](./TC_SRC_005.md)
- [TC_SRC_006: Case-Insensitive Search](./TC_SRC_006.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
