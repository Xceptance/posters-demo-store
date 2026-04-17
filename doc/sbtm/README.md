# Session-Based Test Management (SBTM)

This directory contains the documentation, templates, and execution records for our Exploratory Testing workflow, which uses the Session-Based Test Management (SBTM) methodology.

SBTM is designed to provide high-value defect discovery and deep exploration without the overhead of maintaining rigid, step-by-step test scripts. It relies on **Charters** (the mission) and time-boxed **Sessions** (the execution logs).

## Directory Structure
- `GUIDELINES.md`: Mentorship, debriefing strategies, metric definitions, and best practices.
- `CHARTER_TEMPLATE.md`: The starting template for test missions.
- `SESSION_TEMPLATE.md`: The starting template for execution logs.
- `[coverage-area]/`: Subdirectories grouping charters and sessions by product domain (e.g., `account/`, `checkout/`).

## Core Concepts
1. **Charters:** A focused mission outlining what to explore and the required setup.
2. **Sessions:** A strict, uninterrupted timebox (usually 60-90 minutes) where a tester explores the area defined by the charter.
3. **Session Reports:** A log of what was covered, bugs found, and how time was spent (T/B/S metrics).

## Workflows
You can initiate the SBTM lifecycle using our automated agent skills:
- `/sbtm-charter`: Plan a new test mission.
- `/sbtm-session`: Execute a charter with an AI copilot handling time tracking.
- `/sbtm-review`: Debrief a completed session and groom the backlog.

## Important Limitations
- **Compliance & Sign-offs:** SBTM is **not** a replacement for formal compliance testing. If you need a strict audit signature next to a requirement, use the classic test creation workflow (`/test-create`).
- **Reproducibility:** Session notes provide rich context and heuristics, but not always a 15-step reproduction path. Rely on formal automated regression tests for strict reproducibility.
