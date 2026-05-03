# Posters Demo Store - Test Backlog

This file serves as the official holding ground for test concepts, scenarios, and domains that are currently out-of-scope for the active manual test suite.

Instead of discarding great ideas during the "Joint Refinement" phase of test creation, log them here. This ensures they don't leak into the active manual suite (violating our strict "existing features only" rule) but remain anchored for future implementation or automation.

*(Note: The categories below serve as a starting point. Feel free to introduce new zones or sub-categories as business needs evolve.)*

## 📥 Pending Automation
*Log manual scenarios here that are far too tedious for human execution and should only be authored once an automated framework (e.g. Playwright) is introduced.*
- *(Empty)*

## 🔍 Pending Manual Test Domains
*Log planned test domains that have been identified but not yet authored as full test suites.*
- **Search Suggestions (Type-Ahead):** The HTMX-powered search suggestion dropdown that appears as the user types in the header search bar. Should cover: suggestion appearance after typing delay, displaying up to 5 product results with thumbnails and prices, "View all N results →" link when more than 5 matches exist, suggestion disappearance on blur/clear, and navigation via suggestion click to PDP. (Deferred from Simple Search suite — will be its own test domain.)

## 🔮 Aspirational Capabilities
*Log scenarios for features that do not exist yet in the codebase, but are anticipated in the product roadmap.*
- **Checkout Error Handling (Payment Failure):** A user attempts to place an order with declined/invalid credit card details and receives appropriate error messaging. (Skipped for now as it's a demo store feature)
- **Promo Codes / Discounts:** A user enters a valid discount code during checkout and the order total is reduced appropriately. (Currently unimplemented)
- **Out of Stock Mid-Checkout:** A user attempts to purchase an item that goes out of stock while they are entering payment details. (Inventory management unimplemented)

## 🧩 Deferred Edge Cases
*Log extremely rare or complex edge cases that are technically possible now but deprioritized for initial manual testing velocity.*
- *(Empty)*

## 🗃️ Other Concepts & Exploratory Ideas
*Log any other unrefined testing ideas, exploratory charters, or cross-cutting concerns that don't comfortably fit above.*

### Session Handling
* **Session Expiry Timeout Checking:** Verify idle user experience (auto-logout after X minutes of inactivity).
* **Account Lockout / Rate Limiting:** Verify brute-force protection logic (e.g. system locks account or triggers CAPTCHA after 5 failed login attempts in a short timeframe).

### Account Domain (Pending Creation)
* **Authentication:** Login flows (Happy path, invalid credentials, locked accounts) and Logout.
* **Account Recovery:** Forgot Password / Password Reset flows.
* **Profile Management:** Changing First/Last name or email address from within the account dashboard.
* **Address Book:** Adding, editing, and deleting default shipping and billing addresses.
* **Order History:** Ensuring registered users can view their past orders.
* **Account Deletion:** Testing the ability for a user to delete their account (GDPR/privacy compliance).
