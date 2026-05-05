- [x] 1.1 Rename key `buttonBuyHere` to `buttonShopNow` in all `messages*.properties` files.
- [x] 1.2 Update values for `buttonShopNow`:
    - English: `Shop Now`
    - German: `Jetzt shoppen`
    - Swedish: `Handla nu`
    - Japanese: `今すぐ購入`
- [x] 1.3 Find and replace `#{buttonBuyHere}` with `#{buttonShopNow}` in all Thymeleaf templates (`.html` files).

## 2. Verify and Update Automated Tests

- [x] 2.1 Update manual test case definitions in `specifications/features/` and other non-historical documentation to use "Shop Now".
- [x] 2.2 Update test automation YAML data files (e.g., `TC_SRC_001_SingleTerm.yaml`) and Java test code to expect "Shop Now" and use the new selector/ID if changed.
- [x] 2.3 **IMPORTANT**: Do NOT update already executed test cases, historical logs, or results in `allure-results` or `test-runs`.

## 3. Manual Verification

- [x] 3.1 Run the application and verify the product tile button text on the home page.
- [x] 3.2 Verify the product tile button text on the search results page.
- [x] 3.3 Switch languages (DE, SV, JA) and verify the translations are correct.
