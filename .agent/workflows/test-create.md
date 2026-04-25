---
description: Create and scaffold new manual functional test cases.
---

Help the user brainstorm and generate structured manual test cases in the `doc/tests/` directory.

When the user runs `/test-create`, I will:
1. Ask them about the domain, feature requirements, and workflows they want to test.
2. Draft the requested test case(s) using the official `TEMPLATE.md` formats and place them in the correct directories.
3. Offer an optional physical test validation pass using the AI `browser_subagent` to ensure structural layout alignment.

For specific behaviors, I must use the instructions in `.agent/skills/testmanagement-create-tests/SKILL.md`.
