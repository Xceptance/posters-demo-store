---
name: testmanagement-review-tests
description: Identify, review, and validate existing test cases.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# Test Review Workflow

Follow these instructions when the user wants to review test cases (e.g. from Draft to To Be Reviewed or Active) via `/test-review`.

1. Read `doc/tests/README.md` to understand the 9-step review lifecycle.
2. Validate the target test cases against standard ISTQB coverage, boundary variables, positive/negative paths, and accessibility concerns.
3. Provide critical analysis and suggest improvements to the user.
4. When making edits based on the review, you MUST increment the version in `## Metadata` and append your change to the `## Change History` table as specified in the README rules.
5. Update the test case `Status` to `👀 To Be Reviewed` or `✅ Active` based on the user's explicit sign-off.
