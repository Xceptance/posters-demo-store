# Case-Insensitive Search

Verify that the search is case-insensitive: the same query in different letter cases (uppercase, lowercase, mixed case) returns identical results. This validates that the Lucene analyzers correctly fold case during both indexing and querying across all supported locales.

## Metadata

- **Test ID:** TC_SRC_006
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `case-insensitive`, `analyzer`, `localization`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> Japanese does not have letter case, so the ja-JP locale can be skipped for this test or tested with Latin characters (e.g., `XXL`).

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.

## Test Data

### en-US Variants

| Variant | Search Term | Expected Behavior |
| :--- | :--- | :--- |
| Lowercase | `bear` | Baseline results |
| Uppercase | `BEAR` | Identical to lowercase |
| Mixed case | `bEaR` | Identical to lowercase |
| Title case | `Bear` | Identical to lowercase |

### de-DE Variants

| Variant | Search Term | Expected Behavior |
| :--- | :--- | :--- |
| Original case | `Grizzlybär` | Baseline results |
| Uppercase | `GRIZZLYBÄR` | Identical to original |
| Lowercase | `grizzlybär` | Identical to original |

### sv-SE Variants

| Variant | Search Term | Expected Behavior |
| :--- | :--- | :--- |
| Original case | `Grizzlybjörn` | Baseline results |
| Uppercase | `GRIZZLYBJÖRN` | Identical to original |
| Lowercase | `grizzlybjörn` | Identical to original |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [x] DE-DE
- [x] SV-SE
- [ ] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [ ] Mobile (Small)

---

## Steps

### 1. Navigate to the Store in the Target Locale

- **Action:** Open the Posters Demo Store homepage in the target locale.
- **Verify:** The store loads correctly.

### 2. Establish Baseline — Search with Original Case

- **Action:** Type the baseline search term (first row from the Test Data table for the current locale) into the header search input and submit.
- **Data:** Use the first variant (lowercase or original case) from the Test Data table.
- **Verify:** Note the exact result count and the set of product names returned. This is the baseline for comparison.

### 3. Search with Uppercase Variant

- **Action:** Clear the search input. Type the **Uppercase** variant and submit.
- **Data:** Use the uppercase row from the Test Data table.
- **Verify:** The result count and product names are **identical** to the baseline from Step 2.

### 4. Search with Mixed Case Variant

- **Action:** Clear the search input. Type the **Mixed case** variant (if applicable) and submit.
- **Data:** Use the mixed-case row from the Test Data table.
- **Verify:** The result count and product names are **identical** to the baseline.

### 5. Search with Title Case Variant (en-US only)

- **Action:** Clear the search input. Type the **Title case** variant and submit.
- **Data:** Use the title-case row from the Test Data table.
- **Verify:** The result count and product names are **identical** to the baseline.

---

## Pass/Fail Criteria

- **Pass:** All case variants of the same search term return identical result counts and identical product sets across all target locales.
- **Fail:** Different letter cases produce different result counts or different product sets. Special characters with diacritics (ä, ö) behave differently in uppercase vs. lowercase.

---

## Postconditions

- The browser is on the search results page. No application state has been modified.

---

## Related Cases

- [TC_SRC_001: Simple Search — Single Term (Happy Path)](./TC_SRC_001.md)
- [TC_SRC_005: Partial / Prefix Search](./TC_SRC_005.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
