---
name: testmanagement-automate-tests
description: Automatically identify, review, scaffold, or verify automated tests using the Neodymium AI framework.
license: MIT
metadata:
  author: AI
  version: "1.0"
---

# Test Automation Workflow

Follow these instructions when the user wants to review, scaffold, or verify automated tests via the `/test-automate` workflow.

Engage the user to choose one of the three primary modes: **Review Suitability**, **Implement Automation**, or **Verify Automation**.

---

## 1. Review Suitability Mode

Use this mode to audit existing manual test cases under `test-management/tests/` to evaluate if they are good candidates for automation.

1. **Scan Directory**: Read the target manual test case(s) or scan a specific domain directory (e.g., `test-management/tests/account/`).
2. **Evaluate Criteria**:
   - **High Suitability**: Standard happy paths, predictable multi-field form validations, locale/language switching, simple search queries, and deterministic cart flows.
   - **Low Suitability**: Scenarios requiring complex multi-service synchronous integrations, non-deterministic random data validation, or extreme visual assertions that change dynamically.
3. **Recommend & Tag**:
   - Present a structured evaluation to the user showing each test's suitability score (High/Medium/Low) and rationale.
   - Ask the user if they agree to tag the high-suitability cases with the `tobeautomated` tag.
   - Upon approval, add the `tobeautomated` tag to the `Tags` section in the manual test case's `## Metadata`, increment the `Version` (minor, e.g., `1.0` -> `1.1`), and log this change in the `## Change History` table.

---

## 2. Implement Automation Mode

Use this mode to find manual test cases tagged with `tobeautomated` and automatically generate their executable Neodymium AI files.

### Scaffolding Actions

For each approved test case (e.g., `TC_ACC_002`):
1. **Identify Steps & Data**: Analyze the manual steps and the `## Test Data` / `## Execution Targets` tables in the markdown test file.
2. **Generate YAML Playbook**:
   - Save to: `test-automation/src/test/resources/posters/[domain]/[TestID]_[Name].yaml`
   - Map each manual test step to a clear natural language action parameterized with placeholders like `${variableName}` matching the Test Data.
   - Use the **YAML Playbook Template** below as a guide.
3. **Generate Java Test Class**:
   - Save to: `test-automation/src/test/java/com/xceptance/posters/[domain]/[TestID]_[Name].java`
   - Implement `@NeodymiumTest` methods for each locale or viewport option present in the execution targets.
   - Use the **Java Test Runner Template** below. Adhere strictly to Allman style (braces on new lines) and strict Java coding standards (use `final` aggressively on all methods, arguments, and local variables).
4. **Update Manual Test Metadata**:
   - Update `Execution Type:` in the manual `.md` file to `Automated`.
   - Optionally remove the `tobeautomated` tag from `Tags:` (or replace/augment with `automated`).
   - Increment the minor `Version` in `## Metadata`.
   - Append a row to the `## Change History` table logging the transition to automated execution (e.g., `"Automated with Neodymium AI"`).
5. **Update Domain Overview**:
   - Update the domain's `overview.md` file table to show that this test is now automated.

---

## 3. Verify Automation Mode

Use this mode to audit existing automated tests and ensure the manual specification perfectly matches the implemented code.

1. **Find Automated Cases**: Scan `test-management/tests/` for files with `Execution Type: Automated`.
2. **Verify Code Presence**:
   - Check if the corresponding `.java` class exists in `test-automation/src/test/java/com/xceptance/posters/[domain]/`.
   - Check if the corresponding `.yaml` playbook exists in `test-automation/src/test/resources/posters/[domain]/`.
3. **Validate Step Consistency**:
   - Cross-reference the steps in the manual test case markdown with the instructions in the YAML playbook.
   - Confirm that all execution target locales in the markdown file are covered by active `@DataSet` methods in the Java class.
4. **Report Findings**: Present a detailed dashboard of matches, missing files, or discrepancies. If compilation errors exist in the test package, report them and suggest fixes.

---

## Technical Templates

### A. YAML Playbook Template
Use this structure for the `.yaml` playbook file:

```yaml
# Neodymium AI Playbook
# Automated from manual test: TC_XXX_###
steps: |
  Open ${neodymium.url}
  Click the country selector button.
  Click the country represented by locale '${locale}' in the opened dialog.
  Verify that the flag icon is updated to the new locale.
  Type '${searchTerm}' into the search field and submit.
  Verify the search results page displays a heading with "Results for '${searchTerm}'".
  Verify search results contain at least one product with '${expectedProduct}' in the product title.
  Verify that all product descriptions are in ${expectedDescriptionLanguage}.
  Verify that all product prices use the '${expectedCurrencySymbol}' symbol.
  Verify there are CTA buttons for the products.

data:
  - testId: "tc_xxx_###_en_GB"
    searchTerm: "bear"
    expectedProduct: "Grizzly Bear"
    expectedCurrencySymbol: "£"
    expectedDescriptionLanguage: "English"
    locale: "en-GB"
  - testId: "tc_xxx_###_de_DE"
    searchTerm: "Bär"
    expectedProduct: "Grizzlybär"
    expectedCurrencySymbol: "€"
    expectedDescriptionLanguage: "German"
    locale: "de-DE"
```

### B. Java Test Runner Template
Use this structure for the Java runner class. Note the strict compliance with:
- **Allman style** (braces on new lines).
- **Apache license headers**.
- **Model attribution** (`// AI-generated: Gemini 3.5 Flash` or corresponding active model).
- **Aggressive `final` modifiers** on fields, arguments, methods, and variables.
- **Top-level imports** (no inline fully qualified class names).

```java
/*
 * Copyright (c) 2026 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xceptance.posters.DOMAIN;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.common.testdata.DataSet;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TEST_ID - TEST_TITLE.
 *
 * AI-generated: Gemini 3.5 Flash
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/DOMAIN/TEST_ID_TEST_NAME.yaml")
@Tag("DOMAIN")
public class TEST_ID_TEST_NAME
{

    @BeforeAll
    public static void setup()
    {
        // Setup logic if needed
    }

    /**
     * Executes the main AI-driven test scenario.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_xxx_###_en_US")
    @Severity(SeverityLevel.NORMAL)
    @Description("TEST_DESCRIPTION")
    final void testEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_xxx_###_de_DE")
    @Severity(SeverityLevel.NORMAL)
    @Description("TEST_DESCRIPTION")
    final void testDeDe() throws Throwable
    {
        executeTest();
    }

}
```
