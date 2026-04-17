# SBTM Guidelines & Mentorship

This document outlines the best practices for conducting Session-Based Test Management (SBTM), focusing on low-overhead metric tracking, debriefing, and mentorship.

## 1. Low-Overhead Metric Tracking (T/B/S)

SBTM uses Timebox Metrics to understand how testing effort is being spent. **Do not use stopwatches.** T/B/S tracking should take exactly 10 seconds at the end of a session.

*   **T (Test Execution):** Time spent actively exploring the application, trying out ideas, and interacting with the UI.
*   **B (Bug Investigation):** Time spent investigating anomalies, reproducing bugs, and documenting them.
*   **S (Setup & Admin):** Time spent setting up test data, configuring environments, or writing non-bug notes.

**(T + B + S must equal 100%)**

### Tracking Methods:
1. **Gut Feel Bucketing:** At the end of the session, use rough estimates.
   - *Smooth Session:* 70/15/15
   - *Buggy Session:* 30/60/10
   - *Admin Hell Session:* 20/10/70
2. **AI Copilot Automation:** If you use the `/sbtm-session` workflow, the AI copilot will automatically calculate the T/B/S percentages based on the timestamps and content of your conversation, bringing your mental overhead to zero.

## 2. The Debriefing Phase (PROOF)

The Debriefing phase is crucial. A Senior QA must review the session logs with the tester. Do not treat SBTM as "hands-off" testing.

Use the **PROOF** structure during the `/sbtm-review`:
*   **P (Past):** What happened during the session? Did we stick to the charter?
*   **R (Results):** What bugs were found? What coverage was achieved?
*   **O (Outlook):** What should we do next? Do we need a new charter based on these findings?
*   **O (Obstacles):** Did setup or data issues block testing? (High 'S' metric).
*   **F (Feelings):** How did the tester feel about the stability of the coverage area?

## 3. Heuristics & Oracles

When a bug is found, the tester should log the **Heuristic** or **Oracle** used to determine it was a bug.
- *Oracle:* The mechanism used to recognize a problem (e.g., "Compared against standard UX guidelines," "Checked against the legacy system," "Consistency with the rest of the application").
- *Heuristic:* The test strategy used to find it (e.g., "Zero/Null/Negative boundary testing," "Rapid double-clicking," "Session timeout interruption").
