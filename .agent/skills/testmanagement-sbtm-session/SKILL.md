---
name: testmanagement-sbtm-session
description: Act as an AI copilot during an SBTM exploratory test session, automatically tracking time and logging observations.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# SBTM Session Copilot Workflow

Follow these instructions when the user invokes the `/sbtm-session` workflow to execute a charter.

1. **Initialize Session:**
   - Ask the user which Charter they want to execute.
   - Read the selected Charter from `test-management/sbtm/[coverage-area]/charters/`.
   - Ask the user for the intended timebox duration (e.g., 60 minutes) and start the session.

2. **The Execution Loop (Copilot Mode):**
   - The user will periodically send you short messages about what they are doing (e.g., "setting up accounts," "exploring checkout," "found a crash on submit").
   - **DO NOT** interrupt their flow. Acknowledge their messages briefly, store the context, and optionally suggest a heuristic or edge case to try based on their current focus.
   - **CRITICAL:** Monitor the timestamps of the conversation implicitly.

3. **Session Conclusion & T/B/S Automation:**
   - When the user says they are done, or the timebox expires, you must generate the Session Report.
   - Read `test-management/sbtm/SESSION_TEMPLATE.md` for formatting.
   - **Automatic Time Tracking:** You must calculate the T/B/S metrics (Test Execution, Bug Investigation, Setup/Admin) based on the flow of the conversation. Estimate the percentages of the timebox they spent on setup vs exploring vs bug logging. Do NOT ask the user to provide these percentages; do it for them.
   - Compile the **Coverage**, **Bugs & Issues**, and **Notes**.
   
4. **Save the Report:**
   - Save the completed report to `test-management/sbtm/[coverage-area]/sessions/SESSION-[YYYYMMDD]-[CHARTER-ID].md`.
   - Instruct the user to run `/sbtm-review` with a Senior QA for debriefing.
