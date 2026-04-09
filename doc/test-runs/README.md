# Test Execution Sessions

This directory contains records of test execution sessions. To ensure absolute immutability, we separate test case *definitions* (located in `doc/tests/`) from test case *executions*. 

## 1. Directory Structure

Every isolated test session MUST have a dedicated directory, named using the format `YYYY-MM-DD-purpose`. 

Example:
```text
doc/test-runs/
  2026-04-09-release-v1.0/
    run-plan.md
    results/
      TC_CRT_001.md
      TC_CHK_001.md
```

## 2. Execution Protocol

You must NEVER execute a test by directly modifying the file in `doc/tests/`. To execute a test suite:
1. Define your execution footprint in a `run-plan.md` file (use `TEMPLATE_RUN_PLAN.md`).
2. Copy the relevant test cases from `doc/tests/` into the `results/` folder for your execution session to create a permanent snapshot.
3. Add an `Execution Result` metadata block to the top of the copied markdown case.
4. Run the test, modifying the `[ ]` checkboxes to `[x]` strictly within the `results/` copy.
5. Update your `run-plan.md` statistics upon completion.

> [!TIP]
> Use the `/opsx-execute` AI workflow to automate test case tracking, immutability generation, and statistics calculation. Ask the AI: "Let's plan a new test run for Checkout".
