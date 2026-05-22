---
description: Automatically identify, review, scaffold, or verify automated tests using the Neodymium AI framework.
---

Help the user automate manual tests, verify existing automated tests, or review suitability.

When the user runs `/test-automate`, I will:
1. Ask the user if they want to:
   - **Review Suitability**: Scan manual tests (or specific directories) to assess automation feasibility and add/remove the `tobeautomated` tag.
   - **List/Implement Automation**: Find all active manual tests tagged with `tobeautomated` and automatically generate their Neodymium AI Java test runner and YAML playbooks.
   - **Verify Automation**: Audit tests with `Execution Type: Automated` to ensure their Java classes and YAML playbooks exist, match step specifications, and compile.
2. Generate the Neodymium AI code artifacts and data playbooks under `test-automation/src/test/` following established repository patterns and Java coding standards.
3. Automatically update the manual test case metadata, increment the version, and record the automation transition in the Change History.

For specific behaviors, I must use the instructions in `.agent/skills/testmanagement-automate-tests/SKILL.md`.
