# Posters Demo Store

Welcome to the Posters Demo Store repository. This repository represents a full software development lifecycle environment, explicitly divided into modular sub-projects representing different domains of the software delivery process.

## Repository Architecture

This project is organized into the following sub-projects:

### `specifications/`
**The Definition of What to Build.** 
Contains the OpenSpec workflows, design proposals, and the high-level business and technical backlogs. This acts as the central hub for product management and requirement engineering.

### `implementation/`
**The Core Application.** 
Contains the actual Spring Boot application source code for the Posters Demo Store, along with its Docker configurations, database schemas, and 3rd-party licenses.
> ➡️ [View Implementation README](implementation/README.md) for build and run instructions.

### `test-management/`
**The Manual Testing and Session-Based Test Management (SBTM) Hub.**
Contains functional test cases, exploratory testing charters, test runs, and manual quality assurance documentation.

### `test-automation/`
**The Automated Verification Suite.**
Contains the automated end-to-end and UI test suites derived from the manual tests. This suite is triggered against the `implementation` code to check correctness automatically.

### `test-performance/`
**The Load and Performance Suite.**
Contains load generation scripts and performance test scenarios (e.g., XLT tests) used to measure the capacity and stability of the application under stress.

## AI Agent Ecosystem

This repository includes an `.agent/` folder at the root, which houses specialized AI skills and workflows that operate across these sub-projects. The AI can pull specifications, generate code, scaffold test cases, and act as a copilot during your test sessions.

---
*Copyright 2013-2026 Xceptance Software Technologies GmbH*
