# Agent Instructions

Please adhere to the following rules when contributing to this repository:

## General Behavior
- **Explicit Confirmation:** Do not implement anything unless told and confirmed by the user.
- **Tooling Preference:** Prefer using Java for scripting and agent tasks (e.g., utility scripts) instead of Python or Bash, unless it's typical Unix tooling. If a non-Java tool is necessary, ask for permission first.

## Coding Standards
- **Test-Driven Development (TDD):** Write unit and integration tests before implementing new functionality. Ensure comprehensive test coverage for all new code.
- **Code Style:** Prefer the Allman code style (braces on a new line).
- **Documentation:** Always comment code thoroughly.
- **AI Attribution:** If a file is exclusively created by AI, mark it in the class comment accordingly with the specific model you used.
- **License Headers:** Always add an Apache license header to all new source code files.
- **Final:** Use `final` modifiers aggressively on variables, arguments, methods, and fields wherever possible to enforce strict immutability.
- **Imports:** NEVER use Fully-Qualified Class Names (FQCN) in inline code; ALWAYS declare explicit imports at the top of the file instead.
- **Java Language Features:** Use JDK 21 features and syntax where appropriate.

## Third-Party Dependencies
- **Attribution:** If you use an open-source library, document it in `NOTICE.md` and include the appropriate license references inside `doc/3rd-party-licenses`.

## GIT

- **Merge:** Never fast-forward, never stash.
- **Stashing:** Ask for permission every time. 

## Testing & Specifications
- **Manual & Automated Tests:** You MUST create test cases as part of any specification process or implementation work.
- **Maintenance:** You MUST review existing test cases and update them to reflect any logic, UI, or specification changes made during implementation.

## Security Standards

### CSRF Protection (MANDATORY)
All state-changing HTTP operations MUST be protected against Cross-Site Request Forgery (CSRF) attacks.

**Requirements:**

1. **Thymeleaf Forms:** ALL forms with `method="post"` MUST use `th:action` instead of hardcoded `action` attributes
   - ✅ Correct: `<form th:action="@{'/' + ${urlLocale} + '/login'}" method="post">`
   - ❌ Incorrect: `<form action="/en/login" method="post">`
   - Thymeleaf automatically injects CSRF tokens when using `th:action`

2. **HTMX Requests:** ALL HTMX requests that modify state MUST include CSRF token headers
   - Automatically handled by `htmx:configRequest` event listener in layout templates
   - Verify meta tags are present: `<meta name="_csrf" th:content="${_csrf.token}"/>`
   - No manual token management needed - configuration is in layout/default.html and layout/checkoutLayout.html

3. **Controller Endpoints:** ALL POST/PUT/DELETE/PATCH endpoints are automatically protected by Spring Security
   - Exception: Stateless APIs under `/api/v2/**` are excluded from CSRF validation
   - If you need to exclude an endpoint, document the security justification in code comments and link to relevant specification

4. **Testing:** ALL new state-changing endpoints MUST include CSRF test coverage
   - Test with valid token → Success (200/302)
   - Test without token → 403 Forbidden
   - Test with invalid token → 403 Forbidden
   - See `openspec/specs/csrf-protection/spec.md` for detailed testing requirements

**Verification Checklist:**
- [ ] All forms use `th:action` (no hardcoded `action` attributes)
- [ ] HTMX configuration includes CSRF headers (check layout templates)
- [ ] New endpoints have CSRF test coverage
- [ ] Security exceptions are documented and justified

**Reference:**
- Full specification: `openspec/specs/csrf-protection/spec.md`
- Security documentation: `specifications/security/SECURITY_AND_PCI.md` (SEC-007)
- Implementation guide: `openspec/changes/storefront-csrf-protection/design.md`
- **Format & Standards:** All functional test documentation MUST explicitly adhere to the standards outlined in `doc/tests/README.md`. When creating new test cases, you MUST use `doc/tests/TEMPLATE.md` as your starting point.
