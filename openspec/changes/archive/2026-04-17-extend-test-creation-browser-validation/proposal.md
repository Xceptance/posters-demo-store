## Why

When the agent uses the `/test-create` workflow to generate manual QA test cases, it drafts the markdown based on instructions and expected W3C flows. However, without executing the test, the generated steps could be hallucinated, mismatch the physical UI, or reference non-existent elements. Adding a synchronized requirement where the agent *physically* validates the test case using the browser subagent guarantees that all committed test cases are factually working against the application.

## What Changes

We are extending the `testmanagement-create-tests` skill and `/test-create` workflow to inject a final (or late-stage) execution step. During this step, the agent will load the locally running application using the `browser_subagent` and walk through the dynamically generated test case steps. Only if the validation succeeds will the test case be structurally signed-off and the domain initialized.

## Capabilities

### New Capabilities

- `agent-browser-validation`: Defines the capability of an AI agent to structurally launch a browser subagent and autonomously validate user-facing flows according to a defined Markdown test specification prior to saving.

### Modified Capabilities

- No existing OpenSpec capabilities are modified (the actual change happens in the agent architecture `.agent/skills/` and `.agent/workflows/` which are not currently modeled as formal OpenSpecs).

## Impact

- `.agent/skills/testmanagement-create-tests/SKILL.md`
- `.agent/workflows/test-create.md`
- System reliability of generated domain test cases.
