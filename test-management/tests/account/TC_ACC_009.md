# Registration Input Boundary Limits

Verifies that the registration form fields enforce maximum character length limits to prevent buffer truncation or database insertion errors.

## Metadata

- **Test ID:** TC_ACC_009
- **Version:** 1.3
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🔄 Regression, 🔒 Security
- **Requirements:**
  - Registration boundaries
- **Tags:** `registration`, `validation`, `security`, `boundary`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - Antigravity (AI) (2026-05-21)

## Comments

> [!NOTE]
> Database columns for `firstName`, `lastName`, and `email` default to VARCHAR(255). This test verifies that input lengths are limited gracefully at the boundary to prevent database truncation errors or system crashes.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.

## Test Data

| Field | Value |
| :--- | :--- |
| **Name Pass Boundary** | String of exactly 255 characters |
| **Name Fail Boundary** | String of exactly 256 characters |
| **Email Pass Boundary** | Total 255 characters (e.g. prefix `a...a` [243 chars] + `@posters.com` [12 chars]) |
| **Email Fail Boundary** | Total 256 characters (e.g. prefix `a...a` [244 chars] + `@posters.com` [12 chars]) |

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

### 1. Test First/Last Name Boundary Limits
- **Action:** Enter a string of exactly 255 characters into the First Name and Last Name fields, fill other fields with valid data, and submit.
- **Verify:** Registration is successful without truncation or database exceptions.
- **Action:** Repeat registration using a string of exactly 256 characters in either the First Name or Last Name field.
- **Verify:** The UI prevents typing the 256th character (if `maxlength="255"` is present), OR upon submission, the UI displays a clean validation error. The server must not return an HTTP 500 error page.

### 2. Test Email Address Boundary Limits
- **Action:** Register with a valid email address that is exactly 255 characters long.
- **Verify:** Registration completes successfully.
- **Action:** Attempt to register with an email address that is exactly 256 characters long.
- **Verify:** The UI blocks typing past 255 characters OR rejects the submission with a clean validation error indicating the email is too long. The application must not crash or display SQL errors.

---

## Pass/Fail Criteria

- **Pass:** The system gracefully handles maximum boundaries (255 chars) and blocks or rejects inputs exceeding 255 characters without database exceptions or HTTP 500 errors.
- **Fail:** System crashes, SQL truncation errors are exposed, or inputs exceeding 255 characters are saved literally.

---

## Postconditions

- Test accounts are created for passing cases.

---

## Related Cases

- [TC_ACC_002: Mandatory Fields Validation](./TC_ACC_002.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-05-21 | 1.1 | Antigravity (AI) | Refined with precise 255/256-character BVA inputs, specified graceful error requirements, added 'tobeautomated' tag, and promoted to Active. |
| 2026-05-21 | 1.2 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.3 | Gemini 3.5 Flash | Added JA-JP locale |

