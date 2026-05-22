# Registration Form Accessibility and Keyboard Navigation

Verifies that the registration form is fully interactive using only the keyboard, ensuring accessibility and rapid entry for power users.

## Metadata

- **Test ID:** TC_ACC_010
- **Version:** 1.3
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** ♿ Accessibility
- **Requirements:**
  - WCAG Compliance
- **Tags:** `registration`, `accessibility`, `keyboard`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - Antigravity (AI) (2026-05-21)

## Comments

> [!CAUTION]
> The "Password Visibility" toggle (eye icon) must be reachable via the `Tab` key and triggerable via `Space` or `Enter`. If it was implemented as an untabbable raw `<i>` tag without a button, this test will fail.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.
- Focus is set to the browser address bar.

## Test Data

- Standard valid registration data to type:
  - First Name: `Taro`
  - Last Name: `Yamada`
  - Email: `taro@posters.com`
  - Password: `S3cureP@ss!`

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE
- [x] JA-JP

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. Verify Logical Tab Sequence and Focus Outlines
- **Action:** Press `Tab` continuously to cycle through page elements into the registration form.
- **Verify:** Focus cycles into the form in logical DOM order:
  1. **First Name** input field
  2. **Last Name** input field
  3. **Email** input field
  4. **Password** input field
  5. **Password visibility toggle** (eye icon button)
  6. **Create Account** submit button
  7. **Already have an account? Login** link
- **Verify [Visual]:** As each element receives focus, a highly visible, contrasting focus outline/ring is displayed (WCAG 2.1 Success Criterion 2.4.7 Focus Visible).

### 2. Enter Form Data and Toggle Password Masking via Keyboard
- **Action:** Focus on "First Name" and type `Taro`. Press `Tab` to navigate to "Last Name" and type `Yamada`. Press `Tab` to navigate to "Email" and type `taro@posters.com`. Press `Tab` to navigate to "Password" and type `S3cureP@ss!`.
- **Verify:** Data is entered correctly. Focus indicator behaves expectedly.
- **Action:** Press `Tab` to focus on the Password visibility toggle (eye icon). Press `Space` or `Enter`.
- **Verify:** The password changes from dots (`••••••••`) to readable text `S3cureP@ss!`.
- **Action:** Press `Space` or `Enter` again.
- **Verify:** The password changes back to masked dots.

### 3. Submit Form via Keyboard
- **Action:** Press `Tab` to navigate focus to the "Create Account" button. Press `Enter`.
- **Verify:** The form is successfully submitted, and the user is redirected to the home page with a successful registration session.

---

## Pass/Fail Criteria

- **Pass:** The entire registration workflow can be completed successfully without using a mouse, all interactive controls receive focus in logical order, focus indicators are highly visible, and the password visibility toggle is fully operable via space/enter keys.
- **Fail:** Elements are skipped in the tab index, interactive controls cannot be triggered via keyboard, or focus indicators are missing/invisible (violating WCAG 2.1 AA).

---

## Postconditions

- A test account is created successfully.

---

## Related Cases

- [TC_ACC_005: Password Visibility Toggle](./TC_ACC_005.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-05-21 | 1.1 | Antigravity (AI) | Refined with precise DOM focus sequence, added explicit WCAG 2.1 AA focus visible highlights and interactive keyboard rules, added 'tobeautomated' tag, and promoted to Active. |
| 2026-05-21 | 1.2 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.3 | Gemini 3.5 Flash | Added JA-JP locale |

