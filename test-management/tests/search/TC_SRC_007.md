# Search Result Correctness & Navigation

Verify that search results are valid and correct: each rendered product tile genuinely matches the search query, displays accurate product data (name, image, description, price), and that clicking a result navigates to the correct Product Detail Page (PDP) with consistent information.

## Metadata

- **Test ID:** TC_SRC_007
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Search, Catalog
- **Priority:** 🟠 High
- **Status:** ✅ Automated
- **Execution Type:** Automated
- **Automation:**
  - [Java Class](../../../../test-automation/src/test/java/com/xceptance/posters/search/TC_SRC_007_ResultCorrectness.java)
  - [YAML Data](../../../../test-automation/src/test/resources/posters/search/TC_SRC_007_ResultCorrectness.yaml)
- **Suite:** 🚀 Smoke, 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `search`, `result-correctness`, `navigation`, `PDP`, `data-integrity`
- **Author:** Antigravity (AI) (2026-05-02)
- **Reviewers:**
  - N/A

## Comments

> [!IMPORTANT]
> This test goes beyond simply checking that results appear — it validates that every rendered result is a **genuine match** and that the data shown on the search results page is consistent with the Product Detail Page. False positives (unrelated products appearing in results) are a critical failure.

## Preconditions

- The Posters Demo Store is running.
- The Lucene search index has been built.
- The user is on the homepage of the store in the target locale.

## Test Data

| Field | en-US | de-DE | sv-SE | ja-JP |
| :--- | :--- | :--- | :--- | :--- |
| Search Term | `cat` | `Katze` | `katt` | `猫` |
| Known Match 1 | `Red Domestic Cat` | `Hauskatze mit beige-braunem Fell` | `Röd huskatt` | `赤い飼い猫` |
| Known Match 2 | `Brown Domestic Cat` | `Hauskatze mit braun-schwarzem Fell` | `Brun huskatt` | `茶色の飼い猫` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
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

### 2. Search for the Test Term

- **Action:** Type the **Search Term** from the Test Data table into the header search input and submit.
- **Data:** Use the search term for the current locale.
- **Verify:** The search results page loads with at least 2 results.

### 3. Verify Each Result Tile Is a Genuine Match

- **Action:** For each product tile in the results, check whether the search term appears in the product name or description text.
- **Verify:** Every rendered product tile is a genuine match — the search term (or a stemmed/prefix variant) appears in the product's name, short description, or would logically appear in the long description. No unrelated products are present.

### 4. Verify Product Tile Data Completeness

- **Action:** Inspect each product tile for data completeness.
- **Verify:** Each tile displays:
  - A **product image** that loads (no broken image icon, `src` attribute is not empty).
  - A **product name** that is non-empty and in the expected locale language.
  - A **description text** that is non-empty and in the expected locale language.
  - A **price** that is a valid formatted number greater than $0.00 / €0.00 / kr 0.00.
  - A **"Shop Now"** button/link.

### 5. Note Product Name and Image from a Result Tile

- **Action:** Choose the first known match from the Test Data table (e.g., `Red Domestic Cat` in en-US). Note its exact displayed name, image, and price on the search results page.
- **Verify:** The product is visible and its data is noted for comparison in the next step.

### 6. Click the Product Tile to Navigate to PDP

- **Action:** Click on the product image or the "Shop Now" button of the noted product.
- **Verify:** The browser navigates to the Product Detail Page (PDP). The URL contains the product name and ID.

### 7. Verify PDP Consistency

- **Action:** On the PDP, compare the displayed product name, main image, and price with the values noted from the search results tile in Step 5.
- **Verify:**
  - The **product name** on the PDP matches the name displayed on the search results tile.
  - The **product image** on the PDP is the same image (or a larger version of the same image) shown on the tile.
  - The **price** on the PDP is consistent with (equal to or a variant of) the price shown on the tile.

---

## Pass/Fail Criteria

- **Pass:** All search result tiles are genuine matches for the query. Tile data (name, image, description, price) is complete and correctly localized. Clicking a tile navigates to the correct PDP with matching product information.
- **Fail:** Unrelated products appear in results (false positives). Any tile has a broken image, missing name, empty description, or zero/negative price. Clicking a tile navigates to the wrong PDP or a 404 page. PDP data does not match the tile data.

---

## Postconditions

- The browser is on a Product Detail Page. No application state has been modified (no items added to cart).

---

## Related Cases

- [TC_SRC_001: Simple Search — Single Term (Happy Path)](./TC_SRC_001.md)
- [TC_SRC_008: Search Result Count & Heading](./TC_SRC_008.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-02 | 1.0 | Antigravity (AI) | Initial creation |
