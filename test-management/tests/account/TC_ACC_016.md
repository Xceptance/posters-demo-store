# Password Visibility Toggle on Login

Verifies that clicking the eye icon in the password field toggles the input visibility between masked and plain text.

## Metadata

- **Test ID:** TC_ACC_016
- **Version:** 1.2
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟢 Low
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🧪 Full, ♿ Accessibility
- **Requirements:**
  - Login UI/UX Options
- **Tags:** `login`, `ui`, `usability`, `password-toggle`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - AI Personas (BA, QA, Manager, Consumer) (2026-04-09)

## Comments

> [!TIP]
> Ensure that a screen reader announces the state of the toggle (e.g., 'Show password', 'Hide password').

## Preconditions

- The Posters Demo Store is running.
- The user is on the Login page.

## Test Data

| Field | Value |
| :--- | :--- |
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
- [x] Tablet (Medium)
- [x] Mobile (Small)

---

## Steps

### 1. Verify Initial State

- **Action:** Locate the password field. Type a password.
- **Data:** `Password` = `S3cur3!P@ss`
- **Verify:** The characters are masked (shown as dots or asterisks). The eye icon is an open eye icon (visual).

### 2. Toggle Visibility On

- **Action:** Click the password visibility toggle icon.
- **Verify:** The password characters become visible in plain text as `S3cur3!P@ss`. The toggle icon visually changes to a strike-through eye icon (visual).

### 3. Toggle Visibility Off

- **Action:** Click the password visibility toggle icon again.
- **Verify:** The password characters are masked again. The toggle icon reverts to an open eye icon (visual).

---

## Pass/Fail Criteria

- **Pass:** The toggle successfully switches the input between type `password` and `text` without altering the actual value.
- **Fail:** The toggle does nothing, deletes the password, or does not change visually.

---

## Postconditions

- None

---

## Related Cases

- [TC_ACC_005: Password Visibility Toggle](./TC_ACC_005.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-05-21 | 1.1 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.2 | Gemini 3.5 Flash | Added JA-JP locale and visual eye icon validation steps |
