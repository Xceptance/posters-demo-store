# Protected Route Authorization Guards

This test explicitly checks that URL endpoints governing sensitive account mutations refuse unauthenticated (Guest) requests by strictly enforcing session checks and redirecting cleanly.

## Metadata

- **Test ID:** TC_ACC_007
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔒 Security, 🔄 Regression
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `account`, `security`, `authorization`, `redirect`
- **Author:** Antigravity (AI) (2026-04-08)
- **Reviewers:**
  - [Human]

## Comments

> [!WARNING]
> This checks server-side checks. `SessionService` evaluates if `sessionService.isCustomerLoggedIn(session)` equates strictly.

## Preconditions

- The Posters Demo Store is running.
- The browser session contains NO active user data (clear cookies or open Incognito/Private mode).

## Test Data

- **Protected Target 1:** `/{locale}/accountOverview`
- **Protected Target 2:** `/{locale}/orderOverview`

## Execution Targets

**Target Locales:**
- [x] EN-US

**Target Viewports:**
- [x] Desktop (1920x1080)

## Steps

### 1. Directly Request Profile Modifications
- **Action:** In the browser URL bar, explicitly navigate to `Protected Target 1`.
- **Verify:** The server violently refuses rendering the account form context.
- **Verify:** The browser immediately experiences a 302/redirect into `/{locale}/login`.

### 2. Directly Request Order History
- **Action:** In the browser URL bar, explicitly navigate to `Protected Target 2`.
- **Verify:** The server identically refuses access.
- **Verify:** The browser redirects to `/{locale}/login`.

---

## Pass/Fail Criteria

- **Pass:** The server catches the empty sessions cleanly prior to executing any DB retrieval logic, returning the user instantly to the login prompt.
- **Fail:** An error 500 occurs, or the server actually attempts to render the skeleton of the view (exposing sensitive application design layers without authorization).

---

## Postconditions

- The browser maintains a purely unauthenticated session.

---

## Related Cases

- [TC_ACC_004: Valid Login Sequence](./TC_ACC_004_Login_Success_And_Logout.md)
