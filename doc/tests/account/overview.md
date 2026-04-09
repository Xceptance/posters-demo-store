# Account Domain Test Cases

This directory contains manual test cases for the User Account domain, covering registration, login, and profile management.

| Test ID | Title | Priority | Suite | Objective |
| :--- | :--- | :--- | :--- | :--- |
| [TC_ACC_001](./TC_ACC_001.md) | Successful Account Registration | 🔴 Critical | Smoke | Verify user can register with valid data. |
| [TC_ACC_002](./TC_ACC_002.md) | Mandatory Fields Validation | 🟠 High | Regression | Verify required fields block submission when empty. |
| [TC_ACC_003](./TC_ACC_003.md) | Invalid Email Format Validation | 🟡 Medium | Regression | Verify email format rules are enforced. |
| [TC_ACC_004](./TC_ACC_004.md) | Duplicate Account Registration Attempt | 🔴 Critical | Regression| Verify duplicate emails are rejected. |
| [TC_ACC_005](./TC_ACC_005.md) | Password Visibility Toggle | 🟢 Low | Full | Verify password UI eye icon functionality. |
| [TC_ACC_006](./TC_ACC_006.md) | Login Navigation Link | 🟢 Low | Full | Verify link points to login page. |
| [TC_ACC_007](./TC_ACC_007.md) | Password Complexity Requirements Check | 🟠 High | Regression | Verify password complexity rules are enforced. |
| [TC_ACC_008](./TC_ACC_008.md) | Robust Email Input Processing | 🟠 High | Regression | Verify email whitespace trimming and case insensitivity. |
| [TC_ACC_009](./TC_ACC_009.md) | Registration Input Boundary Limits | 🟡 Medium | Regression | Verify maximum form field lengths. |
| [TC_ACC_010](./TC_ACC_010.md) | Form Accessibility and Keyboard Nav | 🟡 Medium | Accessibility | Verify complete keyboard interactive capabilities. |
