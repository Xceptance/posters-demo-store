# Charter: Search Localization

**Coverage Area:** search
**Tags:** localization, multi-language, encoding
**Status:** Unexplored

## 1. Mission
*What is the main goal of this session?*
Explore the search functionality across different supported languages to ensure queries are processed correctly and the UI remains culturally consistent.

## 2. Area / Scope
*Which specific features or components are to be tested?*
- Global search input and query processing across languages
- Search results page localization (messages, labels, currency formatting)

## 3. Setup / Environment / Data
*Any specific accounts, data payloads, or environment configs needed before starting?*
- Standard demo store environment with multiple languages enabled.
- Prepare translated product names and search terms in the target languages (e.g., German characters with umlauts, Spanish accents).

## 4. Test Focus / Strategy
*What specific angles or quality criteria will guide the exploration?*
- **Character Encoding:** Search using terms with special language characters (e.g., ö, ä, ü, ñ). Verify they are parsed and matched correctly.
- **Contextual Consistency:** Verify that results match the active language context and that UI elements (messages like "No results found", currency on product cards) localize properly.
- **Cross-Language Fallbacks:** Test what happens if a user searches for an English term while the site is set to German, or vice versa.
