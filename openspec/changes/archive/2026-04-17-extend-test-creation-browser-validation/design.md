## Context

The Posters Demo Store utilizes AI agents to autonomously draft manual QA test cases that human or automated testers can later follow. Currently, these tests are generated strictly from static W3C flows and templates. Because there's no feedback loop enforcing that the AI actually validated the selectors, URL paths, and exact behavioral logic against the live site, the agent risks producing test specifications that contain hallucinations.

## Goals / Non-Goals

**Goals:**
- Guarantee physical validation of all AI-drafted test cases prior to their finalization.
- Automatically invoke the `browser_subagent` as part of the `testmanagement-create-tests` workflow to step through the draft test.
- Create a reusable standard for an `agent-browser-validation` capability.

**Non-Goals:**
- This is not a complete end-to-end automated UI framework replacing Playwright. It's strictly the usage of the agent subagent to validate structural logic and text accuracy during the creation/draft phase of manual test cases.

## Decisions

- **Workflow Integration**: The browser validation step will be injected into both the `Light Flow` (after drafting) and `Standard Flow` (by appending it as 'Step 10'). However, to support scenarios where the system is not yet deployed but tests need to be prepared (e.g., from mockups), the agent will *ask* the user if they want to execute the validation step. It will be an optional mode.
- **Standalone Capability Spec**: The OpenSpec structure will introduce this logic as `openspec/specs/agent-browser-validation/spec.md` to cleanly separate meta agent toolsets from standard commerce product logic.

## Risks / Trade-offs

- **Performance Trade-off**: Enforcing a browser subagent trace linearly slows down the time it takes the Agent to report success back to the end user. This trade-off is absolutely worth the increase in 100% accurate test definitions.
- **Flaky Environmental Behavior**: The `browser_subagent` operations could fail due to timing or element loading even when the test steps are technically valid. Agent instructions must ensure graceful recovery and explicit error logs indicating if the failure was a structural hallucination or a system timeout.
