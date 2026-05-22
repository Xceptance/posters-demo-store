# Password Complexity Requirements Verification

Verifies that the registration form enforces minimum password complexity rules (e.g., minimum length, required characters) and displays appropriate error messages when those requirements are not met.

## Metadata

- **Test ID:** TC_ACC_007
- **Version:** 1.4
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟠 High
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🔄 Regression, 🔒 Security
- **Requirements:**
  - Registration
- **Tags:** `registration`, `password`, `validation`, `security`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - Antigravity (AI) (2026-05-21)

## Comments

> [!NOTE]
> Storefront password complexity is enforced client-side via the HTML5 `minlength="6"` attribute. Failing inputs are blocked natively by the browser user-agent before form submission.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.
- The browser cart can be empty or have items.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | Jane |
| Last Name | Doe |
| Email | `test_TC_ACC_007_${TIMESTAMP}@posters.com` |
| Valid Password | `S3cur3!` (exactly 7 characters) |
| Boundary Minimum Password | `Pass12` (exactly 6 characters) |
| Too Short Password | `pwd` (3 characters) |

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

### 1. Fill Valid Form Data
- **Action:** Enter valid First Name, Last Name, and Email from the Test Data table.
- **Verify:** Inputs are accepted without warnings.

### 2. Test Too Short Password (Below Minimum Boundary)
- **Action:** Enter `pwd` in the Password field and click "Create Account".
- **Verify:** Form submission is blocked natively by the browser.
- **Verify [Visual]:** The browser displays a native validation overlay stating: `"Please lengthen this text to 6 characters or more (you are currently using 3 characters)."` (or equivalent browser-specific minlength error message).

### 3. Test Boundary Minimum Password (Exactly 6 Characters)
- **Action:** Enter `Pass12` in the Password field and click "Create Account".
- **Verify:** The password meets the minimum requirement, and the form is submitted successfully.
- **Verify:** The user is redirected to the home page (URL path `/`) with a successful registration session.

---

## Pass/Fail Criteria

- **Pass:** The registration form successfully blocks passwords under 6 characters with a browser-native validation overlay, and accepts passwords of exactly 6 or more characters.
- **Fail:** System crashes, passwords under 6 characters are accepted, or no validation overlay is displayed.

---

## Postconditions

- A test account `test_TC_ACC_007_${TIMESTAMP}@posters.com` is created.

---

## Related Cases

- [TC_ACC_001: Successful Account Registration](./TC_ACC_001.md)
- [TC_ACC_002: Mandatory Fields Validation](./TC_ACC_002.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-04-09 | 1.1 | Antigravity (AI) | Added EN-GB and SV-SE to target locales |
| 2026-05-21 | 1.2 | Antigravity (AI) | Refined steps for HTML5 native minlength validation overlays, parameterized boundary data, added 'tobeautomated' tag, and promoted to Active. |
| 2026-05-21 | 1.3 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.4 | Gemini 3.5 Flash | Added JA-JP locale |

