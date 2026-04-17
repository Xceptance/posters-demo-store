---
name: testmanagement-sbtm-review
description: Facilitate the SBTM Debriefing phase (PROOF) and groom the test backlog.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# SBTM Debrief & Review Workflow

Follow these instructions when the user invokes the `/sbtm-review` workflow to debrief a completed session.

1. **Select Session:** Ask the user which Session Report they want to review. Read the report from `doc/sbtm/[coverage-area]/sessions/`.
2. **Conduct the PROOF Debrief:** Act as a Senior QA Mentor and walk the user through the PROOF framework as defined in `doc/sbtm/GUIDELINES.md`:
   - **P (Past):** Did you stick to the charter's mission?
   - **R (Results):** Let's review the bugs and coverage. (Analyze if they missed any obvious edge cases based on their coverage notes).
   - **O (Outlook):** What's next? Do we need a follow-up charter?
   - **O (Obstacles):** I see your 'Setup (S)' metric was 40%. What blocked you?
   - **F (Feelings):** How confident are you in this area's stability?
3. **Action Items:**
   - If critical functionality was verified, suggest migrating it to a formal test case using `/test-create`.
   - If new risks were found, automatically draft a new Charter and save it to the backlog.
4. **Finalize:** Update the Session Report's "Debrief & Next Steps" section with the outcomes of this conversation and save the file.
