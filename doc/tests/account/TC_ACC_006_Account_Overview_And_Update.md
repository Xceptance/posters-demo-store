# Account Modification and State Management

This test verifies the user ability to retrieve active profile data from the database securely and modify those attributes locally while logged in.

## Metadata

- **Test ID:** TC_ACC_006
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `account`, `profile`, `update`
- **Author:** Antigravity (AI) (2026-04-08)
- **Reviewers:**
  - [Human]

## Comments

> [!NOTE]
> Address capabilities and Password resets are commonly externalized. This checks the fundamental user profile properties only.

## Preconditions

- The Posters Demo Store is running.
- A user account exists (`johndoe.test@example.com`).
- The tester actively holds a logged-in browser session matching the test account.

## Test Data

| Data ID | New First Name | New Last Name | New Email |
| :--- | :--- | :--- | :--- |
| `Valid Update` | Johnathon | Doe-Smith | `j.doe.updated@example.com` |

## Execution Targets

**Target Locales:**
- [x] EN-US

**Target Viewports:**
- [x] Desktop (1920x1080)

## Steps

### 1. View Profile Data
- **Action:** Open the global header and navigate to `Account Overview` (targeting `/{locale}/accountOverview`).
- **Verify:** The form rendered successfully prepopulates the `firstName`, `name`, and `email` bindings reflecting the current Database state `(John, Doe, johndoe.test@example.com)`.

### 2. Submit Mutated Profile Data
- **Action:** Alter the active input textboxes to strictly match the data outlined in `Valid Update`.
- **Action:** Click "Update" (Submitting POST to `/{locale}/updateAccount`).
- **Verify:** The application safely catches the submission, executes the Database query, and refreshes the `accountOverview` screen.
- **Verify:** A flash message indicating "Account updated." accurately displays.

### 3. Verify Mutation Resistance
- **Action:** Hard refresh the browser using F5 or Ctrl+R.
- **Verify:** The input fields continue to statically output the data from `Valid Update`, proving the database transaction succeeded.

---

## Pass/Fail Criteria

- **Pass:** Display values directly echo the true backend properties; submission guarantees successful data mutation without breaking the session state.
- **Fail:** If changing the email instantly kills the session incorrectly or creates a second ghost account item.

---

## Postconditions

- The User's email string has definitively changed causing all subsequent logins to demand `j.doe.updated@example.com`.

---

## Related Cases

- [TC_ACC_001: Account Creation](./TC_ACC_001_Registration_Happy_Path.md)
