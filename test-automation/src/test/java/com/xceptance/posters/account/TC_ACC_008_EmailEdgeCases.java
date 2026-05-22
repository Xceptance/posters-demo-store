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
 * Automates TC_ACC_008 - Robust Input Processing and Localization.
 *
 * Verifies that the registration form handles padded whitespaces in names and emails,
 * email case-insensitivity, Japanese name inputs, and Punycode email domains.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_008_EmailEdgeCases.yaml")
@Tag("account")
@Tag("registration")
@Tag("localization")
@Tag("regression")
public class TC_ACC_008_EmailEdgeCases
{

    /**
     * Executes the main AI-driven test scenario, injecting dynamic edge-case inputs.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        final long timestamp = System.currentTimeMillis();
        // Construct dynamic email addresses
        final String paddedEmail = " emma_" + timestamp + "@posters.com ";
        final String expectedTrimmedEmail = paddedEmail.trim();
        final String caseVariationEmail = "Emma_" + timestamp + "@posters.com";
        final String punycodeEmail = "user_" + timestamp + "@xn--zckzah.com";
        final String trimmedTestEmail = "emma_trimmed_" + timestamp + "@posters.com";

        Neodymium.getData().put("paddedEmail", paddedEmail);
        Neodymium.getData().put("expectedTrimmedEmail", expectedTrimmedEmail);
        Neodymium.getData().put("caseVariationEmail", caseVariationEmail);
        Neodymium.getData().put("punycodeEmail", punycodeEmail);
        Neodymium.getData().put("trimmedTestEmail", trimmedTestEmail);

        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_008_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify robust input and localization handling (en-US).")
    final void testRobustInputEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_008_en_GB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify robust input and localization handling (en-GB).")
    final void testRobustInputEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_008_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify robust input and localization handling (de-DE).")
    final void testRobustInputDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_008_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify robust input and localization handling (sv-SE).")
    final void testRobustInputSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_008_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify robust input and localization handling (ja-JP).")
    final void testRobustInputJaJp() throws Throwable
    {
        executeTest();
    }

}
