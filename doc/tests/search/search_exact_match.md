# Exact Match Product Search

## Metadata

- **Test ID:** TC_SEA_001
- **Domain:** Search
- **Priority:** High
- **Status:** Draft
- **Execution Type:** Manual
- **Tags:** `search`, `lucene`, `happy-path`

### Execution Targets

**Target Locales:**
- [x] EN-US
- [x] DE-DE

**Target Viewports:**
- [ ] Desktop (Large)
- [ ] Mobile (Small)

## Description

This test validates the Lucene search engine integration by ensuring that a direct exact-match search correctly returns the targeted product item without breaking pagination or display rendering.

## Tester Notes

> [!NOTE]
> The search bar on mobile is hidden behind the hamburger menu. Make sure to toggle it properly when executing for Mobile Viewports.
> 
> *Example:* 
> ![Mobile Search Toggle](../../images/catalog/mobile-search-hint.png)

## Preconditions

- The Posters Demo Store is running.
- Catalog data has been successfully seeded.

## Test Data

| Locale | Search Term | Expected Result Count | Target Product verification |
| :--- | :--- | :--- | :--- |
| **EN-US** | "Dining Room" | >= 1 | Posters displaying dining scenes or related keywords |
| **DE-DE** | "Klavier" | >= 1 | Posters specifically displaying pianos / music |

---

## Steps

| Step # | Action | Expected Result |
| :---: | :--- | :--- |
| 1 | Navigate to the storefront homepage. | The homepage loads successfully. |
| 2 | Click on the search input field in the top navigation bar. | The input field receives focus. |
| 3 | Enter the localized search term from strictly matching the Test Data table. | Text appears correctly in the field. |
| 4 | Press `Enter` or click the search/magnifying glass icon. | The site routes to the Search Results page. |
| 5 | Verify the Results headline. | It reads "Results for [Search Term]" with the correct count. |
| 6 | Verify the Product Cards. | All returned products logically correlate to the search term (no completely random matches). |

---

## Postconditions

- None required. Search is non-mutating.
