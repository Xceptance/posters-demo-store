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

package com.xceptance.posters.account;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.common.testdata.DataSet;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;
import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TC_ACC_009 - Registration Input Boundary Limits.
 *
 * Verifies that the registration form handles character lengths up to 255 characters
 * and blocks or validates inputs exceeding 255 characters to prevent database issues.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_009_BoundaryInputs.yaml")
@Tag("account")
@Tag("registration")
@Tag("boundary")
@Tag("regression")
public class TC_ACC_009_BoundaryInputs
{

    /**
     * Executes the main AI-driven test scenario, injecting dynamic boundary inputs.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        final long timestamp = System.currentTimeMillis();
        final String suffix = timestamp + "@posters.com";
        
        // Construct boundary strings
        final String name255 = "a".repeat(255);
        final String name256 = "a".repeat(256);
        final String email255 = "a".repeat(255 - suffix.length()) + suffix;
        final String email256 = "a".repeat(256 - suffix.length()) + suffix;
        // Dynamic email for name testing (happy path)
        final String nameTestEmail = "test_TC_ACC_009_" + timestamp + "@posters.com";

        Neodymium.getData().put("name255", name255);
        Neodymium.getData().put("name256", name256);
        Neodymium.getData().put("email255", email255);
        Neodymium.getData().put("email256", email256);
        Neodymium.getData().put("nameTestEmail", nameTestEmail);

        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_009_en_US")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify name and email boundary limits during registration (en-US).")
    final void testBoundariesEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_009_en_GB")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify name and email boundary limits during registration (en-GB).")
    final void testBoundariesEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_009_de_DE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify name and email boundary limits during registration (de-DE).")
    final void testBoundariesDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_009_sv_SE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify name and email boundary limits during registration (sv-SE).")
    final void testBoundariesSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_009_ja_JP")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify name and email boundary limits during registration (ja-JP).")
    final void testBoundariesJaJp() throws Throwable
    {
        executeTest();
    }

}
