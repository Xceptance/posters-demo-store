# Customer Registration

## Metadata

- **Test ID:** TC_ACC_001
- **Domain:** Account
- **Priority:** High
- **Status:** Draft
- **Execution Type:** Manual
- **Tags:** `account`, `registration`, `security`, `happy-path`

### Execution Targets

**Target Locales:**
- [x] EN-US
- [x] DE-DE

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

## Description

This test asserts that a new customer can successfully create an account when providing valid details, and that the account is persisted properly enabling them to log in immediately thereafter.

## Tester Notes

> [!TIP]
> **Keep an eye out for:**
> - Ensure the password complexity tooltip displays and hides properly when focusing on the password field.
> - Verify that the email is validated (e.g., trying to register with a malformed email `test@test` should fail).
>
> *Example format of an error for existing users:*
> ![Email Exists Error](../../images/account/email-exists-hint.png)

## Preconditions

- The Posters Demo Store is running.
- The user is logged out.

## Test Data

- **First Name:** `Manual`
- **Last Name:** `Tester`
- **Email:** `new.tester_${RANDOM}@example.com` (Use a fake, unique email every time)
- **Password:** `Test1234!` (Must meet complexity requirements: 1 Uppercase, 1 Number, 1 Special Char)

---

## Steps

| Step # | Action | Expected Result |
| :---: | :--- | :--- |
| 1 | Navigate to the storefront homepage. | The homepage loads successfully. |
| 2 | Click on the **User / Login** icon in the header. | The Login / Register selection page is displayed. |
| 3 | Click on the **"Create new account"** button. | The Registration form is displayed. |
| 4 | Fill in the First Name, Last Name, and the unique Email address. | Text populates the fields correctly. |
| 5 | Enter the valid password into both the **Password** and **Confirm Password** fields. | The passwords entered are masked (dots/asterisks). |
| 6 | Click **"Create Account"**. | The user is redirected back to the Login screen with a "Success! Account created." notification. |
| 7 | Attempt to login with the newly created Email and Password. | User successfully logs in and is redirected to the homepage (header now shows "Account" instead of "Login"). |

---

## Postconditions

- A new customer record exists in the database.
- The user is actively logged into their new session.
