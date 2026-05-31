# [Test Case Title]

[Provide a short summary of the end-to-end flow being tested. This is the first thing a reader sees.]

## Metadata

- **Test ID:** TC_[DOMAIN]_[NUM]
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** [Domain Name, Domain Name]
- **Priority:** [🔴 Critical / 🟠 High / 🟡 Medium / 🟢 Low]
- **Status:** [📝 Draft / 👀 To Be Reviewed / ✅ Active / 🗄️ Deprecated]
- **Execution Type:** Manual
- **Suite:** [🚀 Smoke / 🔄 Regression / 🧠 Sanity / 🧪 Full]
- **Recommended Viewports:** Desktop (Large), Mobile (Small)
- **Recommended Browsers:** Chrome, Firefox
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `tag1`, `tag2`
- **Author:** [Name] ([YYYY-MM-DD])
- **Reviewers:**
  - [Name or AI Model] ([YYYY-MM-DD])

## Comments

[Optional. Free-form notes about tricky conditions, known defects, or anything a tester should be aware of before running this test.]

> [!TIP]
> *Example Screenshot:*
> <!-- ![Hint Image](../images/domain/screenshot.png) -->

## Preconditions

- The Posters Demo Store is running.
- [Additional system or user states required...]

## Test Data (Common Constants)

These values are constant across all test profiles. Define any sensitive fields using explicit parenthetical suffix notation like `(sensitive)` or `(private)` and inline mock stand-ins `(mock: ...)`.

| Field | Value |
| :--- | :--- |
| First Name | `John` |
| Password (private) | `Secret123!` (mock: `mockPassword_abc`) | <!-- Sensitive field with inline mock stand-in -->

---

## Test Profiles (Logical Scenarios)

> Each checked profile represents a distinct logical dataset to execute. You can toggle checkboxes to filter which profiles run.

| Run | ID | Locale | Subtotal | Expected Total |
| :---: | :--- | :---: | :--- | :--- |
| - [x] | `Guest-US` | `en-US` | `$17.00` | `$25.44` |
| - [x] | `Guest-DE` | `de-DE` | `14,96 €` | `23,28 €` |
| - [ ] | `Guest-JP` | `ja-JP` | `￥17` | `￥25` |

---

## Steps

### [Step Name or 1. Step Name]

- **Action:** [What the tester does.]
- **Data:** `[Key]` = `[Value]` (locale-specific if needed)
- **Verify:** [What must be true after this action.]
- **Hint:** [Optional per-step note.]

### [Next Step]

---

## Pass/Fail Criteria

- **Pass:** [Overall criteria for the test case to be considered passed.]
- **Fail:** [Specific conditions that immediately fail the test case.]

---

## Postconditions

- [State of the DB, cart, or user session after the test.]

---

## Related Cases

- [TC_XXX_NUM: Title of related test case](./path.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| YYYY-MM-DD | 1.0 | [Author] | Initial creation |
