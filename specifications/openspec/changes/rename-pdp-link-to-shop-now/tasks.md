- [ ] 1.1 Rename key `buttonBuyHere` to `buttonShopNow` in all `messages*.properties` files.
- [ ] 1.2 Update values for `buttonShopNow`:
    - English: `Shop Now`
    - German: `Jetzt shoppen`
    - Swedish: `Handla nu`
    - Japanese: `今すぐ購入`
- [ ] 1.3 Find and replace `#{buttonBuyHere}` with `#{buttonShopNow}` in all Thymeleaf templates (`.html` files).

## 2. Verify and Update Automated Tests

- [ ] 2.1 Update manual test case definitions in `specifications/features/` and other non-historical documentation to use "Shop Now".
- [ ] 2.2 Update test automation YAML data files (e.g., `TC_SRC_001_SingleTerm.yaml`) and Java test code to expect "Shop Now" and use the new selector/ID if changed.
- [ ] 2.3 **IMPORTANT**: Do NOT update already executed test cases, historical logs, or results in `allure-results` or `test-runs`.

## 3. Manual Verification

- [ ] 3.1 Run the application and verify the product tile button text on the home page.
- [ ] 3.2 Verify the product tile button text on the search results page.
- [ ] 3.3 Switch languages (DE, SV, JA) and verify the translations are correct.
