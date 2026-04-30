# Registration Input Boundary Limits

Verifies that the registration form fields enforce maximum character length limits to prevent buffer truncation or database insertion errors.

## Metadata

- **Test ID:** TC_ACC_009
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🔒 Security
- **Requirements:**
  - Registration boundaries
- **Tags:** `registration`, `validation`, `security`, `boundary`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - 

## Comments

> [!TIP]
> Most standard e-commerce DB schemas limit Emails to 255 chars, and names to 50 or 100 chars. We test boundaries exceeding 255 chars.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.

## Test Data

| Field | Value |
| :--- | :--- |
| Extremely Long String | 300 character string of letter "A" |
| Extremely Long Email | 250 character prefix + `@posters.com` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. Exceed First/Last Name Limits
- **Action:** Paste an extremely long string (300+ chars) into the First Name and Last Name fields.
- **Verify:** The UI prevents entering all 300 characters (max-length attribute), or upon submission, an appropriate validation message prevents crashing the backend.

### 2. Exceed Email Limit
- **Action:** Paste an extremely long string into the Email field.
- **Verify:** The field enforces a max length, or the backend returns a clean validation error indicating the email is too long.

---

## Pass/Fail Criteria

- **Pass:** The application successfully stops oversized inputs gracefully without HTTP 500 errors or database exceptions.
- **Fail:** Entering long strings causes application crashes, blank error screens, or SQL truncation warnings.

---

## Postconditions

- No side-effects.

---

## Related Cases

- [TC_ACC_002: Mandatory Fields Validation](./TC_ACC_002.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
