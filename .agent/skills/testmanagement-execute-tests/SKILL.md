---
name: testmanagement-execute-tests
description: Execute a manual functional test case, updating its status dynamically and managing the test session immutability.
license: MIT
metadata:
  author: AI
  version: "2.0"
---

# Test Execution Workflow

Follow these instructions when the user wants to execute test cases or plan a new test run session through the `/test-execute` workflow.

## If the User wants to PLAN a new test run:

1. Ask them the goal, context, and which domains/tests should be included.
2. Create a new directory in `test-management/test-runs/` following the format `YYYY-MM-DD-purpose`.
3. Create `run-plan.md` in this directory based on `test-management/test-runs/TEMPLATE_RUN_PLAN.md`.
4. Create a `results/` directory inside the test run.
5. Copy the requested test cases from `test-management/tests/` to the `results/` directory.
6. For each copied test case, inject the "Execution Result" metadata block immediately after the main Title:
   ```markdown
   ## Execution Result

   | Who | When | Result | Where | Comment |
   | :--- | :--- | :--- | :--- | :--- |
   | [Name/AI] | [YYYY-MM-DD] | `⏳ PENDING` | [Context/Browser] | |
   ```
7. Populate the `run-plan.md` statistics and checklist.

## If the User wants to EXECUTE a planned test run:

1. Locate the active `run-plan.md` in `test-management/test-runs/`.
2. Find the first pending test case in the checklist.
3. Open the copied file in its `results/` directory (e.g. `test-management/test-runs/2026-04-09-release/results/TC_CRT_001.md`).
4. Present the steps to the user one by one (or in batches) through the chat interface.
5. Wait for the user to report passed/failed for the steps.
6. Check off the steps `[x]` inside the snapshot Markdown file.
7. Once the test case concludes:
   - Update the `Execution Result` block in the test case markdown.
   - Update the Checklist in `run-plan.md` to indicate `✅ PASSED`, `❌ FAILED`, or `🚧 BLOCKED`.
   - Recalculate the `📊 Live Statistics` table perfectly in `run-plan.md`.
   - **Append an entry to the `📝 Execution Log`** section in `run-plan.md` with a summary of the result, any observations, test data corrections made, and references to any defects or improvements logged (e.g., `BUS-BUG-15`).
8. Proceed to the next pending test or yield back to the user.

### During Execution — Capturing Findings

- **Defects & Improvements:** When the user reports a bug or improvement idea during testing, log it immediately to the appropriate backlog (e.g., `specifications/backlog/BUSINESS_BACKLOG.md`) and note the backlog ID in the Execution Log entry for that test case.
- **Test Data Corrections:** If test data in a template is found to be incorrect during execution (wrong locale term, wrong currency, etc.), update the **template** in `test-management/tests/` immediately and note the correction in the Execution Log.
- **Scope Changes:** If new test cases are created during the run (e.g., a gap is discovered), add them to the `results/` directory with the Execution Result block, append them to the checklist with an ⚡ *Added during execution* annotation, and update the Total Scope count in the statistics table.

### After Execution — Execution Report

When all test cases in the checklist are completed:

1. Set the run-plan status to `✅ Completed` and fill in the **Finished** timestamp.
2. Create an `execution-report.md` in the test run directory based on `test-management/test-runs/TEMPLATE_EXECUTION_REPORT.md`.
3. Populate the report by compiling data from:
   - The `run-plan.md` statistics and execution log.
   - The `Execution Result` blocks in each `results/` snapshot file.
   - Any backlog items created during the run.
4. The Execution Report is the **final deliverable** of a test run — a single document that captures the complete picture for stakeholders.
