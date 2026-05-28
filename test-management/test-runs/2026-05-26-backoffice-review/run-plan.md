# Test Run: Backoffice Review

**Goal:** Review and manually execute the newly designed Backoffice Users and Customers test cases to ensure correct behavior and coverage.
**Context:** Localhost | Chrome | Desktop (Large)
**Tester:** AI Agent & User
**Started:** 2026-05-26 22:23 | **Finished:** — | **Status:** `🔄 In Progress`

## 📊 Live Statistics

| Status | Count | Percentage |
| :--- | :--- | :--- |
| **Total Scope** | 5 | 100% |
| ✅ **Passed** | 1 | 20% |
| ❌ **Failed** | 1 | 20% |
| 🚧 **Blocked**| 0 | 0% |
| ⏳ **Pending** | 3 | 60% |

## 📋 Execution Checklist

- [x] `TC_BFC_001` - Backoffice Login and Logout (`✅ PASSED`)
- [x] `TC_BFC_002` - Role-Based Access for Customers Module (`❌ FAILED`)
- [ ] `TC_BFC_003` - Create Customer with Valid Fields (`⏳ PENDING`)
- [ ] `TC_BFC_004` - Create Customer Validation Errors (`⏳ PENDING`)
- [ ] `TC_BFC_005` - Customer Search and Filtering (`⏳ PENDING`)

## 📝 Execution Log

> [!TIP]
> Append entries here as the run progresses. Each entry should note the test case, what happened, and any follow-up actions taken. This section is the source of truth for anything that deviated from the original plan.

[Entries are added during execution in reverse chronological order:]

### TC_BFC_002 — ❌ FAILED
- **Observation:** 
  1. The "Add User" button in Security -> Users is labeled **"New User"**.
  2. Logged in users with restricted roles (such as Customer Admin or Catalog User) landing on `/backoffice/` see the welcome page with **all four** module cards shown, even though they only have permission to access one.
- **Action:** 
  1. Updated the template and execution snapshot in `results/` to v1.3 to correct the button label to "New User".
  2. Registered bug **BUS-BUG-26** in `specifications/backlog/BUSINESS_BACKLOG.md` to restrict dashboard welcome cards by user permissions.

### TC_BFC_001 — ✅ PASSED
- **Observation:** Logout redirects to `/backoffice/login?logout` rather than `/backoffice/login`.
- **Action:** Updated the test template and execution snapshot in `results/` to v1.5 to document `/backoffice/login?logout` as the expected redirect URL and removed Target Locales from Execution Targets as they are not applicable.

