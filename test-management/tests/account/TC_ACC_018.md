# Successful Account Logout

Verifies that clicking "Logout" successfully terminates the user session and prevents access to authenticated pages.

## Metadata

- **Test ID:** TC_ACC_018
- **Version:** 1.2
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🚀 Smoke, 🔄 Regression, 🧪 Full, 🔒 Security
- **Requirements:**
  - Logout Flow
- **Tags:** `logout`, `session`, `security`, `happy-path`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - AI Personas (BA, QA, Manager, Consumer) (2026-04-09)

## Comments

> [!CAUTION]
> Ensure you test the browser's "Back" button after logging out to confirm pages aren't cached or still accessible!

## Preconditions

- The Posters Demo Store is running.
- The user is fully authenticated and has an active session.

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

### 1. Initiate Logout

- **Action:** In the main navigation/header, click on the profile icon or menu and select "Logout".
- **Verify:** The user is redirected to the Application Homepage or Login page. The UI updates to show "Login" or "Sign In" options instead of the user profile.

### 2. Verify Session Termination (Back Button Check)

- **Action:** Click the browser's "Back" button to attempt to return to the authenticated account dashboard.
- **Verify:** The browser either prevents navigation or the system immediately intercepts the request and redirects the user to the Login page, requiring re-authentication. 

---

## Pass/Fail Criteria

- **Pass:** The session is completely destroyed on the backend, and the user cannot access authenticated areas.
- **Fail:** The user remains logged in, or the "Back" button successfully loads a functional, authenticated account page.

---

## Postconditions

- The user session is terminated and they are unauthenticated.

---

## Related Cases

- [TC_ACC_011: Successful Account Login](./TC_ACC_011.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-05-21 | 1.1 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.2 | Gemini 3.5 Flash | Added JA-JP locale |
