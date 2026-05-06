# Security Testing Overview

This directory contains manual test cases for verifying the security features of the Posters Demo Store, with a primary focus on Cross-Site Request Forgery (CSRF) protection.

## Test Cases

| ID | Title | Priority | Target |
| :--- | :--- | :--- | :--- |
| [TC_SEC_001](./TC_SEC_001.md) | CSRF Protection on Login | 🔴 Critical | Storefront |
| [TC_SEC_002](./TC_SEC_002.md) | CSRF Protection on Registration | 🔴 Critical | Storefront |
| [TC_SEC_003](./TC_SEC_003.md) | CSRF Protection on Checkout | 🔴 Critical | Checkout |
| [TC_SEC_004](./TC_SEC_004.md) | CSRF Protection on Cart Operations | 🔴 Critical | Cart |
| [TC_SEC_005](./TC_SEC_005.md) | CSRF Token in HTMX Requests | 🔴 Critical | Frontend |
| [TC_SEC_006](./TC_SEC_006.md) | Session Timeout Handling | 🟡 Medium | UX |
| [TC_SEC_007](./TC_SEC_007.md) | CSRF Attack Simulation | 🔴 Critical | Security |
| [TC_SEC_008](./TC_SEC_008.md) | API Endpoints Excluded from CSRF | 🟡 Medium | API |

## Related Documentation

- [SECURITY_AND_PCI.md](../../../specifications/security/SECURITY_AND_PCI.md)
- [CSRF Guide](../../../implementation/doc/CSRF_GUIDE.md)
