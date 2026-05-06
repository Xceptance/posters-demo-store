# Test Run: Smoke Tests

**Goal:** Execute all critical smoke tests to ensure base application functionality.
**Context:** Local Environment
**Tester:** AI Copilot
**Started:** 2026-05-06 10:00 | **Finished:** 2026-05-06 10:20 | **Status:** `✅ Completed`

## 📊 Live Statistics

| Status | Count | Percentage |
| :--- | :--- | :--- |
| **Total Scope** | 6 | 100% |
| ✅ **Passed** | 3 | 50% |
| ❌ **Failed** | 3 | 50% |
| 🚧 **Blocked**| 0 | 0% |
| ⏳ **Pending** | 0 | 0% |

## 📋 Execution Checklist

- [x] `TC_ACC_001` - Successful Account Registration (`✅ PASSED`)
- [x] `TC_ACC_011` - Successful Account Login (`✅ PASSED`)
- [x] `TC_ACC_018` - Successful Account Logout (`❌ FAILED`)
- [x] `TC_CHK_001` - Guest Checkout (Happy Path) (`✅ PASSED`)
- [x] `TC_SRC_001` - Simple Search — Single Term (Happy Path) (`❌ FAILED`)
- [x] `TC_SRC_007` - Search Result Correctness & Navigation (`❌ FAILED`)

## 📝 Execution Log

> [!TIP]
> Append entries here as the run progresses. Each entry should note the test case, what happened, and any follow-up actions taken. This section is the source of truth for anything that deviated from the original plan.

### TC_ACC_001 — ✅ PASSED
- **Observation:** Landed on homepage after registration.
- **Action:** Noted that an improvement ticket is already open for this redirect behavior.

### TC_ACC_011 — ✅ PASSED
- **Observation:** Landed on homepage after login.
- **Action:** Added this observation to the related improvement request.

### TC_ACC_018 — ❌ FAILED
- **Observation:** Back button correctly reloads the account overview page after logout. While a subsequent refresh triggers a login redirect, the initial backward navigation reveals an incorrect application state.
- **Action:** Logged a critical defect as **TECH-BUG-2** in `TECHNICAL_BACKLOG.md` to investigate potential architectural or caching issues.

### TC_CHK_001 — ✅ PASSED
- **Observation:** End-to-end guest checkout is functional. However, noticed a usability bug where holding down a key (e.g. '1') in the CC number field autofills and overflows into adjacent fields without stopping at the character limit.
- **Action:** Logged **BUS-BUG-21** in `BUSINESS_BACKLOG.md` as a low priority usability issue.

### TC_SRC_001 — ❌ FAILED
- **Observation:** de-DE locale search for "Hornisse" failed. All other locales passed. Additionally, the ja-JP locale correctly uses Yen (not USD as previously documented).
- **Action:** Logged **BUS-BUG-22** for the DE search failure. Updated the TC_SRC_001 template and snapshot to expect the Yen symbol (`¥`) and removed the invalid caution note about USD.

### TC_SRC_007 — ❌ FAILED
- **Observation:** Searching for "cat" on US locale produced too many results due to "catch" being stemmed to "cat". Also noted that the mini cart closes too fast and doesn't stay open when hovered.
- **Action:** Linked search failure to existing defect **BUS-BUG-18**. Linked mini cart observation to existing improvement request **BUS-IMPR-6**.

