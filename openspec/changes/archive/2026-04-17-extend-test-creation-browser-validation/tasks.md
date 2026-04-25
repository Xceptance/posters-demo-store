# Implementation Tasks

## 1. Agent Skill File Update
- **Target File**: `.agent/skills/testmanagement-create-tests/SKILL.md`
- **Action**: Update the instructions for both Light and Standard flows.
  - In `Light Flow`, state that the agent should offer an optional physical browser validation of the formulated test case using `browser_subagent`.
  - In `Standard Flow`, append an optional `Step 10: Automated Browser Validation` which acts as a final execution check if the product is live.

## 2. Agent Workflow Update
- **Target File**: `.agent/workflows/test-create.md`
- **Action**: Add an explanation in the overview that the agent supports an optional browser validation loop.
