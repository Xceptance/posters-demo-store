---
description: Plan or execute a manual test run in the test-runs directory.
---

Help the user plan a test execution session, or execute tests within a session interactively.

When the user runs `/test-execute`, I will:
1. Check if there is an active test run in `test-management/test-runs/`.
   - If not, I will ask them if they want to plan a new one, gather goals, copy tests to `results/` to ensure immutable snapshots, and bootstrap the `run-plan.md`.
2. Guide the user through executing the tests step-by-step.
3. Automatically update the specific step `[x]` checkboxes in the copied immutable files.
4. Update the `run-plan.md` dashboard statistics continuously.
5. Maintain the `📝 Execution Log` in `run-plan.md` — after each test case, append an entry noting the result, observations, test data corrections, and references to any defects or improvements logged to the backlog.
6. When all tests are complete, generate an `execution-report.md` using `TEMPLATE_EXECUTION_REPORT.md` as the final deliverable.

For specific behaviors and step-by-step logic, I must use the instructions in `.agent/skills/testmanagement-execute-tests/SKILL.md`.

**Dependencies**:
- `test-management/test-runs/README.md`
- `test-management/test-runs/TEMPLATE_RUN_PLAN.md`
- `test-management/test-runs/TEMPLATE_EXECUTION_REPORT.md`
