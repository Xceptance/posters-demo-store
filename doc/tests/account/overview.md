# Account Domain: Test Overview

This document provides a high-level summary of all functional test cases governing the **Account** domain (Registration, Login, User Profiles, and Security).

| Test ID | Test Case | Priority | Suite | Objective |
| :--- | :--- | :--- | :--- | :--- |
| **TC_ACC_001** | [Valid Account Registration](./TC_ACC_001_Registration_Happy_Path.md) | 🔴 | 🚀 🔄 | Validates the standard registration flow, ensuring a user can successfully create an account, is logged securely, and is immediately redirected. |
| **TC_ACC_002** | [Registration Validation Limits](./TC_ACC_002_Registration_Validation_Errors.md) | 🔴 | 🔄 🔒 | Verifies that the system strictly rejects invalid registration attempts, specifically targeting duplicate email address constraints and minimum password constraints. |
| **TC_ACC_003** | [Registration Accessibility and UI](./TC_ACC_003_Registration_Accessibility_UI.md) | 🟡 | 🧠 🔄 ♿ | Verifies the functionality and compliance of the accessibility (WCAG) features baked into the registration form (show/hide password toggles). |
| **TC_ACC_004** | [Account Login and Session Teardown](./TC_ACC_004_Login_Success_And_Logout.md) | 🔴 | 🚀 🔄 🔒 | Checks the core authentication cycle: validating correct credentials, initiating a server-side session, and cleanly tearing down that session on logout. |
| **TC_ACC_005** | [Authentication Rejection and Failure Handling](./TC_ACC_005_Login_Failure_Handling.md) | 🔴 | 🔄 🔒 | Guarantees that incorrect combinations of email and passwords securely reject the end user and return safe, un-escaped error strings to the UI. |
| **TC_ACC_006** | [Account Modification and State Management](./TC_ACC_006_Account_Overview_And_Update.md) | 🟠 | 🔄 | Verifies the user ability to retrieve active profile data from the database securely and modify those attributes locally while logged in. |
| **TC_ACC_007** | [Protected Route Authorization Guards](./TC_ACC_007_Protected_Routes_Redirection.md) | 🔴 | 🔒 🔄 | Explicitly checks that URL endpoints governing sensitive account mutations refuse unauthenticated (Guest) requests by strictly enforcing session checks. |

*Note: This overview is actively maintained by AI assistants whenever new tests are added or existing suites are modified.*
