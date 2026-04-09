---
name: testmanagement-create-tests
description: Generate and scaffold new manual functional test cases.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# Test Creation Workflow

Follow these instructions when the user wants to create a new manual functional test case through the `/test-create` workflow.

1. Engage the user to define the basic scenario, domain, and data points.
2. Read `doc/tests/TEMPLATE.md` to ensure you perfectly understand the required formatting structure.
3. Read `doc/tests/README.md` to understand the domain layout and the 9-step creation process.
4. Draft the new test case inside the appropriate domain directory (e.g., `doc/tests/checkout/TC_CHK_...`).
5. Ensure Metadata, Preconditions, Steps, Execution Targets, and Change History are perfectly populated.
6. Present the draft to the user for review.
