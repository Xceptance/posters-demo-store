# Create Customer with Valid Fields

Verifies that an administrator can successfully create a new active customer with valid inputs in the backoffice, and that the newly created customer can log into the storefront immediately.

## Metadata

- **Test ID:** TC_BFC_003
- **Version:** 1.1
- **Software Version:** >= 1.0.0
- **Domains:** Backoffice
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression
- **Requirements:**
  - BUS-EPIC-1: Backoffice
- **Tags:** `backoffice`, `customers`, `storefront-integration`, `tobeautomated`
- **Author:** Gemini (AI) (2026-05-25)
- **Reviewers:**
  - AI Agent (2026-05-25)

## Preconditions

- The Posters Demo Store is running.
- An administrative user exists (e.g., username `admin`, password `admin-2026!`).

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | `John` |
| Middle Name | `M.` |
| Last Name | `Doe` |
| Email | `new_customer_${TIMESTAMP}@example.com` (tester must substitute a unique timestamp or random ID to prevent email collision) |
| Password | `SecurePassword123!` |

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

- **Action:** Navigate to `/backoffice/login`, enter `admin` for Username and `admin-2026!` for Password, and click "Sign In".
- **Verify:** The user is successfully authenticated and redirected to the global backoffice dashboard at `/backoffice`.

### 2. Navigate to Customer Creation Form

- **Action:** In the sidebar navigation, expand the **Customers** menu and click on **Customers** (which links to `/backoffice/customers/list`). Then, click the **Add Customer** button (which links to `/backoffice/customers/new`).
- **Verify:** The "Add New Customer" form is displayed, showing input fields for First Name, Middle Name, Last Name, Email, and Initial Password.

### 3. Fill Out Form and Submit with Unique Data

- **Action:** Enter First Name = `John`, Middle Name = `M.`, Last Name = `Doe`, Email = `new_customer_${TIMESTAMP}@example.com` (using a unique suffix or the current timestamp to ensure uniqueness), and Initial Password = `SecurePassword123!`. Click the **Create Customer** button.
- **Verify:** The form is successfully submitted. The application creates the customer in the database and redirects the browser to the customer's detail view at `/backoffice/customers/{id}`. The profile summary card shows `John M. Doe` and the unique email entered.

### 4. Verify Customer Appears in Customer List

- **Action:** Click the **Back to List** button (which links back to `/backoffice/customers`).
- **Verify:** The new customer `John Doe` with the unique email is successfully listed in the customers table.

### 5. Verify Storefront Login with New Account

- **Action:** Open a private/incognito window (or log out of the backoffice), navigate to the storefront home page at `/`, click the **Sign In** button in the header, enter the unique email created in Step 3 and password `SecurePassword123!`, and submit the login form.
- **Verify:** Storefront login is successful, and the customer's name `John Doe` is correctly displayed in the storefront header.

---

## Pass/Fail Criteria

- **Pass:** The administrator can successfully create a new customer using unique dynamic data, and the customer can immediately log into the storefront using those credentials.
- **Fail:** Form submission errors out, customer is not saved in the database, or the customer is unable to log into the storefront.

---

## Postconditions

- The newly created customer exists in the database.

---

## Related Cases

- [TC_BFC_001: Backoffice Login and Logout](../users/TC_BFC_001_Login_Logout.md)
- [TC_BFC_004: Create Customer Validation Errors](./TC_BFC_004_Create_Customer_Validation_Errors.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-25 | 1.0 | Gemini (AI) | Initial creation with dynamic data instructions |
| 2026-05-26 | 1.1 | Antigravity (AI) | Relocated to nested backoffice/customers directory and updated relative link |
