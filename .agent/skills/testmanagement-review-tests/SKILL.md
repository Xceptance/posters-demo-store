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

1. Read `test-management/tests/README.md` to understand the 9-step review lifecycle.
2. Validate the target test cases against standard ISTQB coverage, boundary variables, positive/negative paths, and accessibility concerns.
3. Provide critical analysis and suggest improvements to the user. Explicitly evaluate whether the test case is a strong candidate for future automation (e.g. repetitive functional happy paths) and ask the user/creator to decide if a `tobeautomated` tag should be added to the metadata.
4. Save or update these collective findings directly in the respective domain folder (e.g., `test-management/tests/DOMAIN/ISTQB_REVIEW.md`) using the exact format defined in `test-management/tests/TEMPLATE_ISTQB_REVIEW.md`.
5. When making edits to specific test cases based on the review (such as adding the `tobeautomated` tag if confirmed by the user), you MUST increment the version in `## Metadata` and append your change to the `## Change History` table as specified in the README rules.
6. Update the test case `Status` to `👀 To Be Reviewed` or `✅ Active` based on the user's explicit sign-off.
