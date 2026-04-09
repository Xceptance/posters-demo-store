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

Ask the user if they want to execute a **Light Flow** or a **Standard Flow** when this skill is invoked.

## Light Flow

1. Engage the user to define the basic scenario, domain, and data points.
2. **Suggest and Refine:** Suggest a few test scenarios to the user and iteratively validate/refine them before writing code.
3. Read `doc/tests/TEMPLATE.md` to ensure you perfectly understand the required formatting structure.
4. Read `doc/tests/README.md` to understand the domain layout and the 9-step creation process.
5. Draft the new test case inside the appropriate domain directory (e.g., `doc/tests/checkout/TC_CHK_...`).
6. Ensure Metadata, Preconditions, Steps, Execution Targets, and Change History are perfectly populated.
7. Present the draft to the user and conduct a fast review round to refine and improve the test case before finalizing.

## Standard Flow

You must act as the orchestrator of the exact 9-step QA process specified in the `doc/tests/README.md`.

1. **AI Suggests:** Engage the user to define the domain and suggest baseline test scenarios.
2. **Joint Refinement:** Validate, refine, and change the scenarios collaboratively with the user.
3. **AI Builds:** Read `doc/tests/TEMPLATE.md` and write the initial markdown files into the appropriate directories. Ensure Metadata, Preconditions, Steps, Execution Targets, and Change History are perfectly populated, then explicitly inform the user.
4. **ISTQB Validation:** Automatically run coverage grades against standard quality models and report back.
5. **Business Analyst Review:** Act as a Business Analyst to review the test cases for e-commerce alignment.
6. **Senior Tester Review:** Act as a Senior Tester to verify boundary values, partitions, and edge cases.
7. **Test Manager Review:** Act as a Test Manager to verify coverage, traceability, and execution efficiency.
8. **End Consumer Review:** Act as an End Consumer to verify usability, clarity, and real-world alignment.
9. **Finalize:** Require explicit sign-off from the user before concluding the domain creation.
