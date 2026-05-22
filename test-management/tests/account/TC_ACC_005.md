# Password Visibility Toggle

Verifies that the eye icon correctly toggles the password input masking.

## Metadata

- **Test ID:** TC_ACC_005
- **Version:** 1.4
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟢 Low
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🧪 Full
- **Requirements:**
  - Registration
- **Tags:** `registration`, `ui`, `accessibility`
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
| Email | test_TC_ACC_005@posters.com |
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

### 1. Enter Password
- **Action:** Type into the password field.
- **Verify:** Text is masked (e.g., as bullets). Icon next to the password field is an open eye icon (visual).

### 2. Toggle Visibility On
- **Action:** Click the "eye" icon button inside the password field.
- **Verify:** The password text becomes visible in plain text. Icon visually changes to a strike-through eye icon (visual).

### 3. Toggle Visibility Off
- **Action:** Click the "eye" icon button again.
- **Verify:** The password text returns to the masked state. Icon is an open eye icon (visual).

---

## Pass/Fail Criteria

- **Pass:** Expected behavior matches the actual outcome for the scenario.
- **Fail:** System crashes, unexpected error pages, or validation bypassed.

---

## Postconditions

- Session state may be authenticated depending on success.

---

## Related Cases

- [TC_ACC_010: Form Accessibility and Keyboard Nav](./TC_ACC_010.md)
- [TC_ACC_016: Password Visibility Toggle on Login](./TC_ACC_016.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-04-09 | 1.1 | Antigravity (AI) | Added EN-GB and SV-SE to target locales |
| 2026-05-21 | 1.2 | Antigravity (AI) | Linked related cases and promoted to Active. |
| 2026-05-21 | 1.3 | Gemini 2.5 Pro | Automated the test case |
| 2026-05-21 | 1.4 | Gemini 3.5 Flash | Added JA-JP locale and visual eye icon validation steps |
