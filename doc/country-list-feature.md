# Country List Feature — Change Documentation

## What Changed

Replaced the free-text country `<input>` on both checkout address pages with a `<select>` dropdown backed by a reusable Java utility. All ~250 ISO 3166-1 countries are listed alphabetically with "United States" pre-selected.

## Files

| Action | File |
|--------|------|
| **NEW** | `src/main/java/com/xceptance/posters/util/CountryList.java` — utility providing sorted country list |
| **NEW** | `src/main/resources/templates/fragments/countrySelectFragment.html` — reusable Thymeleaf fragment |
| **NEW** | `src/test/java/com/xceptance/posters/util/CountryListTest.java` — 5 unit tests |
| MODIFY | `src/main/java/com/xceptance/posters/controller/CheckoutController.java` — adds `countries` model attribute |
| MODIFY | `src/main/resources/templates/checkout/shippingAddress.html` — uses fragment |
| MODIFY | `src/main/resources/templates/checkout/billingAddress.html` — uses fragment |
| MODIFY | `src/main/resources/messages.properties`, `messages_de_DE.properties`, `messages_sv_SE.properties` — `labelSelectCountry` |

## Verification

- ✅ `mvn clean compile` — success
- ✅ `CountryListTest` — 5/5 tests passed (non-empty, sorted, known countries, ISO codes, unmodifiable)

## Reuse

To use the country dropdown on other pages, add `model.addAttribute("countries", CountryList.getCountries())` in the controller and include:
```html
<div th:replace="~{fragments/countrySelectFragment :: countrySelect(${countries}, 'US')}"></div>
```
