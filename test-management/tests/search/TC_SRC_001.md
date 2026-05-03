# Simple Search — Single Term (Happy Path)

Verify that entering a single known product-related search term returns relevant results with correctly localized product names, descriptions, images, prices, and a "Buy Here" action link. This is the primary search validation across all supported locales.

## Metadata

- **Test ID:** TC_SRC_001
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search, Catalog
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `happy-path`, `localization`, `stemming`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> This test implicitly verifies that per-locale Lucene analyzers and stemmers are working correctly. If results appear in one locale but not another for the equivalent term, it indicates a language-specific indexing or analyzer issue.

> [!CAUTION]
> The ja-JP locale uses USD as its currency (it runs on the "Posters US" site). Do not expect Yen formatting.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built (occurs automatically on startup after data import).
- The user is on the homepage of the store in the target locale.

## Test Data

| Field | en-US | de-DE | sv-SE | ja-JP |
| :--- | :--- | :--- | :--- | :--- |
| Search Term | `bear` | `Hornisse` | `björn` | `ダリア` |
| Expected Product (contains) | `Grizzly Bear` | `Europäische Hornisse` | `Grizzlybjörn` | `ピンクのダリア` |
| Expected Currency Symbol | `$` | `€` | `kr` | `$` |
| Expected Description Language | English | German | Swedish | Japanese |

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

- **Action:** Open the Posters Demo Store homepage in the target locale (e.g., `/{locale}/`).
- **Verify:** The store loads in the correct locale. The header, navigation, and disclaimer banner are displayed in the expected language.

### 2. Enter a Search Term

- **Action:** Locate the search input field in the header navigation bar (`#header-search-text`). Type the **Search Term** from the Test Data table for the current locale.
- **Data:** Use the `Search Term` column for the current locale.
- **Verify:** The search input field accepts the text without errors. Ignore any search suggestion dropdown — it is out of scope for this test.

### 3. Submit the Search

- **Action:** Click the search button (`#header-search-button`) or press Enter to submit the search form.
- **Verify:** The browser navigates to the search results page (`/{locale}/search?q=...`).

### 4. Verify Search Results Heading

- **Action:** Observe the heading area on the search results page.
- **Verify:** The heading displays `Results for '{Search Term}' (N posters)` where N is greater than 0.

### 5. Verify Product Tiles

- **Action:** Examine the product tiles in the results grid.
- **Verify:** At least one product tile is visible. Each tile contains:
  - A product **image** that loads correctly (no broken image icon).
  - A product **name** displayed in the expected locale language.
  - A **description** text in the expected locale language.
  - A **price** displayed with the expected currency symbol from the Test Data table.
  - A **"Buy Here"** button/link.

### 6. Verify Expected Product is Present

- **Action:** Scan the result tiles for the **Expected Product** from the Test Data table.
- **Verify:** The expected product appears in the search results with its localized name matching the Test Data value.

---

## Pass/Fail Criteria

- **Pass:** Searching with a known single term returns at least one relevant result with correctly localized name, description, image, price (in the correct currency), and a "Buy Here" link, across all target locales.
- **Fail:** No results are returned for a known valid term; results show the wrong language or currency; images are broken; the "Buy Here" link is missing; or the page returns an error.

---

## Postconditions

- The browser is on the search results page. No application state has been modified.

---

## Related Cases

- [TC_SRC_002: Search — No Results](./TC_SRC_002.md)
- [TC_SRC_004: Multi-Word Search (AND Behavior)](./TC_SRC_004.md)
- [TC_SRC_007: Search Result Correctness & Navigation](./TC_SRC_007.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
