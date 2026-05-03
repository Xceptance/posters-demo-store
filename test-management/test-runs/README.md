# Test Execution Sessions

This directory contains records of test execution sessions. To ensure absolute immutability, we separate test case *definitions* (located in `test-management/tests/`) from test case *executions*. 

## 1. Directory Structure

Every isolated test session MUST have a dedicated directory, named using the format `YYYY-MM-DD-purpose`. 

Example:
```text
test-management/test-runs/
  2026-05-03-simple-search/
    run-plan.md
    execution-report.md
    results/
      TC_SRC_001.md
      TC_SRC_002.md
```

## 2. Key Files

| File | Purpose | When Created |
| :--- | :--- | :--- |
| `run-plan.md` | Live tracker — statistics, checklist, execution log | At planning time |
| `execution-report.md` | Final deliverable — complete findings summary | After all tests complete |
| `results/*.md` | Immutable snapshots of executed test cases | At planning time (copied from `tests/`) |

## 3. Execution Protocol

You must NEVER execute a test by directly modifying the file in `test-management/tests/`. To execute a test suite:
1. Define your execution footprint in a `run-plan.md` file (use `TEMPLATE_RUN_PLAN.md`).
2. Copy the relevant test cases from `test-management/tests/` into the `results/` folder for your execution session to create a permanent snapshot.
3. Add an `Execution Result` metadata block to the top of each copied markdown case.
4. Run the test, modifying the `[ ]` checkboxes to `[x]` strictly within the `results/` copy.
5. After each test case, append an entry to the `📝 Execution Log` in `run-plan.md` noting the result, observations, and any backlog IDs created.
6. Update your `run-plan.md` statistics upon completion of each test.
7. When all tests are done, generate an `execution-report.md` (use `TEMPLATE_EXECUTION_REPORT.md`) as the final deliverable.

> [!TIP]
> Use the `/test-execute` AI workflow to automate test case tracking, immutability generation, finding capture, and statistics calculation. Ask the AI: "Let's plan a new test run for Checkout".

## 4. Templates

| Template | Purpose |
| :--- | :--- |
| `TEMPLATE_RUN_PLAN.md` | Bootstrap a new `run-plan.md` with statistics, checklist, and execution log |
| `TEMPLATE_EXECUTION_REPORT.md` | Generate the final execution report with defects, corrections, and verdict |
