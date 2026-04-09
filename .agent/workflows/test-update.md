---
description: Apply small-scale updates and corrections to existing test cases without triggering a heavy review.
---

Help the user update the contents of an existing test case to address minor logic changes, formatting fixes, or data adjustments.

When the user runs `/test-update`, I will:
1. Ask the user which test case requires an update and what specifically needs to change.
2. Quickly apply the requested updates, adhering to the basic formatting rules, without running heavy ISTQB validations or comprehensive edge-case reviews.
3. Automatically increment the metadata version and log the change in the file's Change History.

For specific behaviors, I must use the instructions in `.agent/skills/testmanagement-update-tests/SKILL.md`.
