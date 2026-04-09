---
description: Plan or execute a manual test run in the test-runs directory.
---

Help the user plan a test execution session, or execute tests within a session interactively.

When the user runs `/opsx-execute`, I will:
1. Check if there is an active test run in `doc/test-runs/`.
   - If not, I will ask them if they want to plan a new one, gather goals, copy tests to `results/` to ensure immutable snapshots, and bootstrap the `run-plan.md`.
2. Guide the user through executing the tests step-by-step.
3. Automatically update the specific step `[x]` checkboxes in the copied immutable files.
4. Update the `run-plan.md` dashboard statistics continuously.

For specific behaviors and step-by-step logic, I must use the instructions in `.agent/skills/openspec-execute-test/SKILL.md`.

**Dependencies**:
- `doc/test-runs/README.md`
- `doc/test-runs/TEMPLATE_RUN_PLAN.md`
