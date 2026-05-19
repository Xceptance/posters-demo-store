# Test Run: Guest Checkout — TC_CHK_001 Verification

**Goal:** Execute TC_CHK_001 (Guest Checkout Happy Path) against the running Posters Demo Store to verify the full guest checkout flow and confirm the test case can be moved to `✅ Active`.
**Context:** Desktop (Large) / EN-US / Local dev server
**Tester:** rschwietzke
**Started:** 2026-05-18 21:50 | **Finished:** 2026-05-18 22:23 | **Status:** `❌ Failed`

## 📊 Live Statistics

| Status | Count | Percentage |
| :--- | :--- | :--- |
| **Total Scope** | 1 | 100% |
| ✅ **Passed** | 0 | 0% |
| ❌ **Failed** | 1 | 100% |
| 🚧 **Blocked**| 0 | 0% |
| ⏳ **Pending** | 0 | 0% |

## 📋 Execution Checklist

- [x] `TC_CHK_001` - Guest Checkout (Happy Path) (`❌ FAILED`)

## 📝 Execution Log

> [!TIP]
> Append entries here as the run progresses.

### TC_CHK_001 — ❌ FAILED

- **Result:** Steps 1–6 passed. Step 7 (Place Order / Confirmation) failed due to incorrect tax calculation.
- **Order placed:** Yes — `ORD-1779135652864`
- **Defect filed:** `BUS-BUG-23` — Tax rounding error: $17.00 × 6.0% = $0.01 displayed, expected $1.02.
- **Test data correction:** Card number updated from `1111222233334444` (no valid BIN → "Unknown" vendor) to `4111111111111111` (Visa test card). Template updated to v1.3.
- **Test case correction:** Order ID description fixed from "UUID" to `ORD-XXXXXXXXXXXX` format (by design). Template updated.
- **Status:** TC_CHK_001 remains `👀 To Be Reviewed` pending BUS-BUG-23 fix and a re-run.
