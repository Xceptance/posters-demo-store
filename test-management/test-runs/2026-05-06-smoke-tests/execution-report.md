# Test Execution Report: Smoke Tests

**Run Plan:** [run-plan.md](./run-plan.md)
**Generated:** 2026-05-06

---

## Summary

| Metric | Value |
| :--- | :--- |
| **Total Test Cases** | 6 |
| **Passed** | 3 (50%) |
| **Failed** | 3 (50%) |
| **Blocked** | 0 (0%) |
| **Tester** | AI Copilot / User |
| **Environment** | Local Environment |
| **Browser** | Chrome |
| **Software Version** | Current Branch |
| **Duration** | 2026-05-06 10:00 → 2026-05-06 10:20 |

## Scope Changes

> [!NOTE]
> Document any test cases that were added, removed, or modified during execution. If the run matched the original plan exactly, write "None."

None.

## Test Data Corrections

> [!WARNING]
> List any test case templates (in `test-management/tests/`) that were updated during execution due to incorrect data, wrong locale terms, or missing scenarios.

| Test Case | Field Changed | Old Value | New Value | Reason |
| :--- | :--- | :--- | :--- | :--- |
| `TC_SRC_001` | Expected Currency Symbol (JA) | `$` | `¥` | The store correctly uses Yen for the Japanese locale, so the test data was corrected to expect it. |

## Defects Found

| ID | Severity | Summary | Found In | Logged To |
| :--- | :--- | :--- | :--- | :--- |
| TECH-BUG-2 | Critical | Back button correctly reloads account overview page after logout, revealing incorrect application state. | `TC_ACC_018` | `TECHNICAL_BACKLOG.md` |
| BUS-BUG-21 | Low | Auto-fill/rapid entry in CC number field overflows into adjacent fields. | `TC_CHK_001` | `BUSINESS_BACKLOG.md` |
| BUS-BUG-22 | High | Search for "Hornisse" fails in de-DE locale (no results). | `TC_SRC_001` | `BUSINESS_BACKLOG.md` |
| BUS-BUG-18 | High | (Existing) Stemmer over-stemming causes false positives ("catch" -> "cat"). | `TC_SRC_007` | `BUSINESS_BACKLOG.md` |

## Improvements Identified

| ID | Summary | Found In | Logged To |
| :--- | :--- | :--- | :--- |
| BUS-IMPR-6 | (Existing) Cart should not close when mouse hovers over it and stay open longer. | `TC_SRC_007` (Observation) | `BUSINESS_BACKLOG.md` |

## Additional Observations

> [!TIP]
> Capture anything noteworthy that doesn't fit into defects or improvements — UX impressions, performance observations, unrelated bugs spotted during testing, etc.

- `TC_ACC_001` & `TC_ACC_011`: Landing on the homepage after registration and login is slightly jarring. There is an existing improvement ticket to address this redirect behavior.

## Verdict

The smoke test suite execution has surfaced multiple issues spanning functionality, UI, and critical security/state behavior. The core authentication flows (`TC_ACC_001` and `TC_ACC_011`) pass smoothly, and guest checkout (`TC_CHK_001`) works end-to-end, minus a minor usability bug.

However, three major areas require immediate attention:
1. **Critical:** Logout session caching (`TECH-BUG-2`) could expose sensitive account data to local user sessions.
2. **High:** Search stemmer tuning (`BUS-BUG-18`) is negatively impacting English search precision.
3. **High:** German locale search (`BUS-BUG-22`) is failing entirely for basic term verification.

**Recommendation:** The current state is NOT release-ready. `TECH-BUG-2` and `BUS-BUG-22` must be addressed.
