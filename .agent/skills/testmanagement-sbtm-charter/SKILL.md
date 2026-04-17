---
name: testmanagement-sbtm-charter
description: Plan and generate a new Session-Based Test Management (SBTM) Charter.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# SBTM Charter Creation Workflow

Follow these instructions when the user wants to create a new exploratory testing charter via the `/sbtm-charter` workflow.

1. **Engage the User:** Ask the user for the high-level mission or idea they want to explore. If they provide a domain or specific requirement, parse it.
2. **Review Templates & Guidelines:** Read `doc/sbtm/CHARTER_TEMPLATE.md` to understand the required format, and `doc/sbtm/GUIDELINES.md` for context on SBTM rules.
3. **Draft the Charter:** 
   - Define the **Coverage Area** (Domain).
   - Formulate a clear **Mission**.
   - Outline the **Area/Scope**.
   - Detail the necessary **Setup/Environment/Data**.
   - Suggest 3-5 specific **Test Focus/Strategy** angles (heuristics to use, like boundary values, interrupt testing, etc.).
4. **Present & Refine:** Present the drafted charter to the user for review.
5. **Save the Artifact:** Once the user approves, save the charter to `doc/sbtm/[coverage-area]/charters/CHARTER-[ID]-[Title-Slug].md`. Make sure to create the directories if they do not exist.
6. **Next Steps:** Suggest that the user can execute this charter using the `/sbtm-session` workflow.
