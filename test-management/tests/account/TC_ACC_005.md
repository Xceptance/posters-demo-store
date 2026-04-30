# Password Visibility Toggle

Verifies that the eye icon correctly toggles the password input masking.

## Metadata

- **Test ID:** TC_ACC_005
- **Version:** 1.1
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟢 Low
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🧪 Full
- **Requirements:**
  - Registration
- **Tags:** `registration`, `ui`, `accessibility`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - 

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

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

---

## Steps

### 1. Enter Password
- **Action:** Type into the password field.
- **Verify:** Text is masked (e.g., as bullets).

### 2. Toggle Visibility On
- **Action:** Click the "eye" icon button inside the password field.
- **Verify:** The password text becomes visible in plain text. Icon visually changes to indicate "hide".

### 3. Toggle Visibility Off
- **Action:** Click the "eye" icon button again.
- **Verify:** The password text returns to the masked state.

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
