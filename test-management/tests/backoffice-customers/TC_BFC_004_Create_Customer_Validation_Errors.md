# Create Customer Validation Errors

Verifies that the customer creation form blocks submissions with invalid formats, missing mandatory fields, and duplicate email addresses.

## Metadata

- **Test ID:** TC_BFC_004
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Backoffice
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression
- **Requirements:**
  - BUS-EPIC-1: Backoffice
- **Tags:** `backoffice`, `customers`, `validation`
- **Author:** Gemini (AI) (2026-05-25)
- **Reviewers:**
  - AI Agent (2026-05-25)

## Preconditions

- The Posters Demo Store is running.
- An administrative user exists.
- A customer with email `existing_customer@example.com` already exists in the database.

## Test Data

| Field | Value |
| :--- | :--- |
| Existing Email | `existing_customer@example.com` |
| Invalid Email | `invalid-email-format` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [ ] DE-DE
- [ ] SV-SE
- [ ] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [ ] Tablet (Medium)
- [ ] Mobile (Small)

---

## Steps

### 1. Log in to Backoffice as Admin

- **Action:** Navigate to `/backoffice/login`, enter admin credentials, and log in.
- **Verify:** The user is logged in.

### 2. Navigate to Customer Creation Form

- **Action:** Navigate directly to `/backoffice/customers/new`.
- **Verify:** The "Add New Customer" form is displayed.

### 3. Submit Form with Missing Mandatory Fields

- **Action:** Leave all fields blank and click the **Create Customer** button.
- **Verify:** Browser-native HTML5 form validation blocks submission due to the `required` attributes on the input fields, and highlights the missing fields (First Name, Last Name, Email, Password).

### 4. Submit Form with Invalid Email Format

- **Action:** Enter First Name = `Jane`, Last Name = `Doe`, Email = `invalid-email-format`, Initial Password = `SecurePassword123!`. Click the **Create Customer** button.
- **Verify:** Browser-native HTML5 email validation blocks submission and displays an error message indicating that the email address is invalid.

### 5. Submit Form with Duplicate Email Address

- **Action:** Enter First Name = `Jane`, Last Name = `Doe`, Email = `existing_customer@example.com`, and Initial Password = `SecurePassword123!`. Click the **Create Customer** button.
- **Verify:** The form is submitted, but the server-side validation blocks creation. The page re-renders, displaying a red alert banner with the error message: `Email already exists: existing_customer@example.com`. The entered First Name, Last Name, and Email values are preserved in their respective input fields.

---

## Pass/Fail Criteria

- **Pass:** The form blocks customer creation when mandatory fields are missing, email format is invalid, or a duplicate email is entered. Server-side validation displays helpful error messages and retains input.
- **Fail:** Duplicate customers are created, form accepts empty fields, or server-side errors cause a 500 status code or unfriendly crash page.

---

## Postconditions

- No new customer is created in the database.

---

## Related Cases

- [TC_BFC_003: Create Customer with Valid Fields](./TC_BFC_003_Create_Customer_Success.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-25 | 1.0 | Gemini (AI) | Initial creation |
