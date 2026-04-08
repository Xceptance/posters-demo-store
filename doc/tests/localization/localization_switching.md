# Storefront Localization & Currency Switching

## Metadata

- **Test ID:** TC_LOC_001
- **Domain:** Localization
- **Priority:** Medium
- **Status:** Draft
- **Execution Type:** Manual
- **Tags:** `localization`, `currency`, `language`, `header`

### Execution Targets

**Target Locales:**
- [x] EN-US
- [x] DE-DE
- [x] SV-SE
- [x] EN-GB

**Target Viewports:**
- [x] Desktop (Large)

## Description

This test verifies that switching the language/locale via the header dropdown accurately changes the store's textual language, currency symbols, and completely recalculates prices based on regional settings.

## Tester Notes

> [!NOTE]
> The locale switcher resets the user's session in some architecture designs. Observe if the cart is maintained or cleared when switching locales (current intended behavior is to **keep** the cart but convert the currency).

## Preconditions

- The Posters Demo Store is running.
- The user has at least `1` item in their browsing cart with a known base price.

## Test Data

- **EN-US:** Language is English, Currency is `$` (e.g., `$19.99`), Decimal separator is `.`
- **EN-GB:** Language is English, Currency is `£` (e.g., `£19.99`)
- **DE-DE:** Language is German, Currency is `€` (e.g., `19,99 €`), Decimal separator is `,`
- **SV-SE:** Language is Swedish, Currency is `kr` (e.g., `19,99 kr`)

---

## Steps

| Step # | Action | Expected Result |
| :---: | :--- | :--- |
| 1 | Navigate to any Product Detail page (PDP) with the locale set to **EN-US**. | The page renders in English. The product price is rendered as `$XX.XX`. |
| 2 | Click the Locale Dropdown in the header and select **"Deutsch" (DE-DE)**. | The page immediately reloads. The text is now in German. |
| 3 | Verify the Price rendering on the PDP. | The price is formatted as `XX,XX €` including the comma separator. |
| 4 | Click on the **Cart** icon. | The Cart overview loads in German. |
| 5 | Verify the calculations in the Cart. | The subtotal, tax, and order total are correctly recalculated and formatted using the `€` currency rules. |
| 6 | Select **"Svenska" (SV-SE)** from the Locale Dropdown. | The Cart reloads in Swedish and formats totals as `kr`. |

---

## Postconditions

- The user remains with the last selected locale.
