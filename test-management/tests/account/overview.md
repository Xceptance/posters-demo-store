# Account Domain Test Cases

This directory contains manual and automated test cases for the User Account domain, covering registration, login, and profile management.

| Test ID | Title | Priority | Suite | Type | Objective |
| :--- | :--- | :--- | :--- | :--- | :--- |
| [TC_ACC_001](./TC_ACC_001.md) | Successful Account Registration | 🔴 Critical | Smoke, Regression | Automated | Verify user can register with valid data. |
| [TC_ACC_002](./TC_ACC_002.md) | Mandatory Fields Validation | 🟠 High | Regression | Automated | Verify required fields block submission when empty. |
| [TC_ACC_003](./TC_ACC_003.md) | Invalid Email Format Validation | 🟡 Medium | Regression | Automated | Verify email format rules are enforced. |
| [TC_ACC_004](./TC_ACC_004.md) | Duplicate Account Registration Attempt | 🔴 Critical | Regression | Automated | Verify duplicate emails are rejected. |
| [TC_ACC_005](./TC_ACC_005.md) | Password Visibility Toggle | 🟢 Low | Full | Automated | Verify password UI eye icon functionality. |
| [TC_ACC_006](./TC_ACC_006.md) | Login Navigation Link | 🟢 Low | Full | Automated | Verify link points to login page. |
| [TC_ACC_007](./TC_ACC_007.md) | Password Complexity Requirements Check | 🟠 High | Regression | Automated | Verify password complexity rules are enforced. |
| [TC_ACC_008](./TC_ACC_008.md) | Robust Input Processing and Localization | 🟠 High | Regression | Automated | Verify whitespace trimming (names/email), case-insensitivity, and internationalized inputs. |
| [TC_ACC_009](./TC_ACC_009.md) | Registration Input Boundary Limits | 🟡 Medium | Regression | Automated | Verify maximum form field lengths. |
| [TC_ACC_010](./TC_ACC_010.md) | Form Accessibility and Keyboard Nav | 🟡 Medium | Accessibility | Automated | Verify complete keyboard interactive capabilities. |
| [TC_ACC_011](./TC_ACC_011.md) | Successful Account Login | 🔴 Critical | Smoke | Automated | Verify user can log in with valid credentials. |
| [TC_ACC_012](./TC_ACC_012.md) | Failed Login (Invalid Credentials) | 🔴 Critical | Regression | Automated | Verify user receives an error when using incorrect credentials. |
| [TC_ACC_013](./TC_ACC_013.md) | Missing Email Validation | 🟠 High | Regression | Automated | Verify login form prevents submission when the email field is empty. |
| [TC_ACC_014](./TC_ACC_014.md) | Missing Password Validation | 🟠 High | Regression | Automated | Verify login form prevents submission when the password field is empty. |
| [TC_ACC_015](./TC_ACC_015.md) | Invalid Login - Swapped Credentials | 🟡 Medium | Regression | Automated | Verify login fails cleanly when email and password values are swapped. |
| [TC_ACC_016](./TC_ACC_016.md) | Password Visibility Toggle on Login | 🟢 Low | Full | Automated | Verify the eye icon correctly toggles password visibility. |
| [TC_ACC_017](./TC_ACC_017.md) | Registration Navigation Link from Login | 🟢 Low | Full | Automated | Verify the 'Don't have an account?' link directs the user to the registration flow. |
| [TC_ACC_018](./TC_ACC_018.md) | Successful Account Logout | 🔴 Critical | Smoke | Automated | Verify clicking logout terminates the session correctly. |
| [TC_ACC_019](./TC_ACC_019.md) | Concurrent Session Handling | 🟠 High | Full | Manual | Verify system behavior when logged in simultaneously across multiple browsers. |

