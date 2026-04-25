# Agent Browser Validation

Enable AI agents to automatically launch a localized browser instance to physically trace and validate manually drafted test cases against a live environment, ensuring the test specification structurally matches the actual application.

## Requirements

### Workflow Integration
- **Optional Execution & Target URL**: The browser validation must be an **optional** capability. The agent must prompt the user before execution and request the target environment URL (e.g., asking "Do you want me to physically validate this test? If yes, please provide the target URL."). This ensures tests can be drafted without an active deployment and allows validation against any environment (local, staging, prod).
- **Light Flow**: After drafting a test case and during the final refinement, the agent offers the automated browser validation mode.
- **Standard Flow**: The strict 9-step test creation workflow is expanded with an optional "Step 10: Automated Browser Validation".

### System Traceability
- The agent must use the `browser_subagent` skill to strictly execute the `Action` and `Data` flags as defined in the drafted Markdown document, observing the exact `Verify` parameters.
- Any physical deviation, unresponsive element, or missing layout state must be treated as a validation failure. The agent must surface this to the user for collaborative correction of the test case.
