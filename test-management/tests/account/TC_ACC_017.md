# Registration Navigation Link from Login

Verifies that clicking the 'Don't have an account?' link successfully navigates the user to the registration page.

## Metadata

- **Test ID:** TC_ACC_017
- **Version:** 1.2
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟢 Low
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🧪 Full
- **Requirements:**
  - Login Navigation
- **Tags:** `login`, `navigation`, `registration`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - AI Personas (BA, QA, Manager, Consumer) (2026-04-09)

## Comments

> [!TIP]
> The wording of the link might differ based on locale, but the navigation destination should remain consistent.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Login page.

## Test Data

- None

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE
- [x] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [x] Tablet (Medium)
- [x] Mobile (Small)

---

## Steps

### 1. Click Registration Link

- **Action:** Scroll to the bottom of the login form and click the "Don't have an account?" text or equivalent navigation link.
- **Verify:** The user is immediately navigated to the Account Registration page.

---

## Pass/Fail Criteria

- **Pass:** The user successfully lands on down the registration page.
- **Fail:** A 404 error occurs, nothing happens, or the link directs the user to an incorrect destination.

---

## Postconditions

- The user is on the Account Registration page.

---

## Related Cases

- [TC_ACC_001: Successful Account Registration](./TC_ACC_001.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-05-21 | 1.1 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.2 | Gemini 3.5 Flash | Added JA-JP locale |
