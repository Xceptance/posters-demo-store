---
name: openspec-execute-test
description: Execute a manual functional test case, updating its status dynamically and managing the test session immutability.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# Test Execution Workflow

Follow these instructions when the user wants to execute test cases or plan a new test run session through the `/opsx-execute` workflow.

## If the User wants to PLAN a new test run:

1. Ask them the goal, context, and which domains/tests should be included.
2. Create a new directory in `doc/test-runs/` following the format `YYYY-MM-DD-purpose`.
3. Create `run-plan.md` in this directory based on `doc/test-runs/TEMPLATE_RUN_PLAN.md`.
4. Create a `results/` directory inside the test run.
5. Copy the requested test cases from `doc/tests/` to the `results/` directory.
6. For each copied test case, inject the "Execution Result" metadata block immediately after the main Title:
   ```markdown
   ## Execution Result

   | Who | When | Result | Where | Comment |
   | :--- | :--- | :--- | :--- | :--- |
   | [Name/AI] | [YYYY-MM-DD] | `⏳ PENDING` | [Context/Browser] | |
   ```
7. Populate the `run-plan.md` statistics and checklist.

## If the User wants to EXECUTE a planned test run:

1. Locate the active `run-plan.md` in `doc/test-runs/`.
2. Find the first pending test case in the checklist.
3. Open the copied file in its `results/` directory (e.g. `doc/test-runs/2026-04-09-release/results/TC_CRT_001.md`).
4. Present the steps to the user one by one (or in batches) through the chat interface.
5. Wait for the user to report passed/failed for the steps.
6. Check off the steps `[x]` inside the snapshot Markdown file.
7. Once the test case concludes:
   - Update the `Execution Result` block in the test case markdown.
   - Update the Checklist in `run-plan.md` to indicate `✅ PASSED`, `❌ FAILED`, or `🚧 BLOCKED`.
   - Recalculate the `📊 Live Statistics` table perfectly in `run-plan.md`.
8. Proceed to the next pending test or yield back to the user.
