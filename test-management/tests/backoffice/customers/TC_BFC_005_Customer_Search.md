# Customer Search and Filtering

Verifies that administrators can search for customers by First Name, Last Name, Email, or Customer Number in the backoffice customer list, and that the search results filter dynamically and correctly.

## Metadata

- **Test ID:** TC_BFC_005
- **Version:** 1.1
- **Software Version:** >= 1.0.0
- **Domains:** Backoffice
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧠 Sanity
- **Requirements:**
  - BUS-EPIC-1: Backoffice
- **Tags:** `backoffice`, `customers`, `search`, `tobeautomated`
- **Author:** Gemini (AI) (2026-05-25)
- **Reviewers:**
  - AI Agent (2026-05-25)

## Preconditions

- The Posters Demo Store is running.
- An administrative user exists.
- The database contains a set of known customers for search matching, including:
  - Customer 1: Name = `John Doe`, Email = `john.doe@example.com`, Customer Number = `10001`
  - Customer 2: Name = `Jane Smith`, Email = `jane.smith@example.com`, Customer Number = `10002`

## Test Data

| Field | Search Query Value | Expected Match (Customer) |
| :--- | :--- | :--- |
| Query A (First Name) | `John` | Customer 1 (`John Doe`) |
| Query B (Last Name) | `Smith` | Customer 2 (`Jane Smith`) |
| Query C (Email) | `john.doe@example.com` | Customer 1 (`John Doe`) |
| Query D (Cust. Number) | `10001` | Customer 1 (`John Doe`) |
| Query E (No Results) | `nonexistent_user_xyz` | No matching rows |

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

### 2. Navigate to Customer List

- **Action:** Click on the **Customers** sidebar link (linking to `/backoffice/customers/list`).
- **Verify:** The full list of customers is displayed in a table with search and pagination options.

### 3. Search by First Name

- **Action:** Type `John` into the search field (`input[name="q"]`) and click the search button / press Enter.
- **Verify:** The table filters to show only customer rows containing "John" (such as `John Doe`). Customer `Jane Smith` is not shown.

### 4. Search by Last Name

- **Action:** Clear the search field, type `Smith`, and submit the search.
- **Verify:** The table filters to show only customer rows containing "Smith" (such as `Jane Smith`). Customer `John Doe` is not shown.

### 5. Search by Email Address

- **Action:** Clear the search field, type `john.doe@example.com`, and submit the search.
- **Verify:** The table filters to display exactly one row: Customer `John Doe` with the matching email.

### 6. Search by Customer Number

- **Action:** Clear the search field, type `10001` (or the customer number of John Doe), and submit the search.
- **Verify:** The table filters to display exactly one row: Customer `John Doe` matching the customer number.

### 7. Search for Non-Existent Customer

- **Action:** Clear the search field, type `nonexistent_user_xyz`, and submit the search.
- **Verify:** The table displays no customer rows. (A friendly "No results" message is shown or the table body remains empty).

---

## Pass/Fail Criteria

- **Pass:** The list filters correctly for queries matching First Name, Last Name, Email, and Customer Number, and handles empty result sets without errors.
- **Fail:** Search fails to return matching records, displays incorrect/unfiltered records, or errors out with a 500 error page.

---

## Postconditions

- None (read-only search query).

---

## Related Cases

- [TC_BFC_003: Create Customer with Valid Fields](./TC_BFC_003_Create_Customer_Success.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-25 | 1.0 | Gemini (AI) | Initial creation |
| 2026-05-26 | 1.1 | Antigravity (AI) | Relocated to nested backoffice/customers directory |
