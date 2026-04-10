---
name: testmanagement-update-tests
description: Update existing manual test cases for minor adjustments, style updates, or simple workflow tweaks.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# Test Update Workflow

Follow these instructions when the user wants to make small-scale adjustments to an existing test case via `/test-update`.

1. Receive the specific modifications requested by the user, or identify the minor style/formatting corrections needed in the target test case.
2. Edit the test case (e.g. updating a few Steps, preconditions, or fixing layout issues). This is NOT a heavy QA validation or review step; just stick to the specific updates needed.
3. Keep the file compliant with the `TEMPLATE.md` style.
4. When making edits, you MUST increment the minor version in `## Metadata` (e.g., from `1.1` to `1.2`) and append your change to the `## Change History` table exactly as specified in the README rules.
