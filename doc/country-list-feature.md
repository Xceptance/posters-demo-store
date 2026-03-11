# Country List Feature — Change Documentation

## What Changed

Replaced the free-text country `<input>` on both checkout address pages with a `<select>` dropdown backed by a reusable Java utility. All ~250 ISO 3166-1 countries are listed alphabetically with "United States" pre-selected. Country names are **localized** based on the user's language (English, German, Swedish).

## Localization

Country names are displayed in the user's current language via `java.util.Locale.getDisplayCountry()`:

| Locale | Example countries |
|--------|------------------|
| English (en-US, en-GB) | United States, Germany, Sweden |
| German (de-DE) | Vereinigte Staaten, Deutschland, Schweden |
| Swedish (sv-SE) | Förenta staterna, Tyskland, Sverige |

Lists are cached per language using a `ConcurrentHashMap` for performance.

## Files

| Action | File |
|--------|------|
| **NEW** | `src/main/java/com/xceptance/posters/util/CountryList.java` — utility providing localized, sorted country list |
| **NEW** | `src/main/resources/templates/fragments/countrySelectFragment.html` — reusable Thymeleaf fragment |
| **NEW** | `src/test/java/com/xceptance/posters/util/CountryListTest.java` — 8 unit tests |
| MODIFY | `src/main/java/com/xceptance/posters/controller/CheckoutController.java` — passes locale-aware countries to model |
| MODIFY | `src/main/resources/templates/checkout/shippingAddress.html` — uses fragment |
| MODIFY | `src/main/resources/templates/checkout/billingAddress.html` — uses fragment |
| MODIFY | `src/main/resources/messages.properties`, `messages_de_DE.properties`, `messages_sv_SE.properties` — `labelSelectCountry` |

## Verification

- ✅ `mvn clean compile` — success
- ✅ `CountryListTest` — 8/8 tests passed (non-empty, sorted, known countries, ISO codes, unmodifiable, German names, Swedish names, localized sort order)

## Reuse

To use the country dropdown on other pages:

1. In the controller, pass the localized country list:
   ```java
   model.addAttribute("countries", CountryList.getCountries(LocaleContextHolder.getLocale()));
   ```

2. In the template, include the fragment:
   ```html
   <div th:replace="~{fragments/countrySelectFragment :: countrySelect(${countries}, 'US')}"></div>
   ```
