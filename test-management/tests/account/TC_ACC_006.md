# Login Navigation Link

Verifies the 'Already have an account?' link directs the user to the login flow.

## Metadata

- **Test ID:** TC_ACC_006
- **Version:** 1.4
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟢 Low
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🧪 Full
- **Requirements:**
  - Registration
- **Tags:** `registration`, `navigation`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - Antigravity (AI) (2026-05-21)

## Comments

> [!TIP]
> *Example Screenshot:*
> <!-- ![Create Account Form](../images/account/create-account.png) -->

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.
- The browser cart can be empty or have items.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | Jane |
| Last Name | Doe |
| Email | test_TC_ACC_006@posters.com |
| Password | `S3cur3!P@ss` |

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

### 1. Click Login Link
- **Action:** Open the create account page. Locate the active "Already have an account?" link below the button/form layout, and click it.
- **Verify:** The user is successfully redirected to the Login page (URL path `/login`).
- **Verify:** The header navigation displays the correct active navigation context and the Login form is visible.

---

## Pass/Fail Criteria

- **Pass:** Expected behavior matches the actual outcome for the scenario.
- **Fail:** System crashes, unexpected error pages, or validation bypassed.

---

## Postconditions

- Session state may be authenticated depending on success.

---

## Related Cases



---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-04-09 | 1.1 | Antigravity (AI) | Added EN-GB and SV-SE to target locales |
| 2026-05-21 | 1.2 | Antigravity (AI) | Refined steps to check specific login redirection path (/login), and promoted to Active. |
| 2026-05-21 | 1.3 | Gemini 2.5 Pro | Automated the test case |
| 2026-05-21 | 1.4 | Gemini 3.5 Flash | Added JA-JP locale |
