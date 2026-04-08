# [Test Case Title]

[Provide a short summary of the end-to-end flow being tested. This is the first thing a reader sees.]

## Metadata

- **Test ID:** TC_[DOMAIN]_[NUM]
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** [Domain Name, Domain Name]
- **Priority:** [🔴 Critical / 🟠 High / 🟡 Medium / 🟢 Low]
- **Status:** [📝 Draft / ✅ Active / 🗄️ Deprecated]
- **Execution Type:** Manual
- **Suite:** [🚀 Smoke / 🔄 Regression / 🧠 Sanity / 🧪 Full / ⚡ Performance / 🔒 Security / ♿ Accessibility]
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

## Test Data

[List any generic data or create a table if data is locale-specific.]

| Field | Value |
| :--- | :--- |
| [Key] | [Value] |

## Execution Targets

**Target Locales:**
- [ ] EN-US
- [ ] EN-GB
- [ ] DE-DE
- [ ] SV-SE

**Target Viewports:**
- [ ] Desktop (Large)
- [ ] Tablet (Medium)
- [ ] Mobile (Small)

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
