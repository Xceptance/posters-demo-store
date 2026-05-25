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
 * Automates TC_ACC_003 - Invalid Email Formats.
 *
 * Verifies that registration blocks invalid email inputs and shows native
 * validation errors. Covers multiple equivalence partitions.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_003_InvalidEmails.yaml")
@Tag("account")
@Tag("registration")
@Tag("validation")
@Tag("regression")
public class TC_ACC_003_InvalidEmails
{

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
    @DataSet(id = "tc_acc_003_en_US")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify validation error overlays for invalid emails (en-US).")
    final void testInvalidEmailsEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_003_en_GB")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify validation error overlays for invalid emails (en-GB).")
    final void testInvalidEmailsEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_003_de_DE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify validation error overlays for invalid emails (de-DE).")
    final void testInvalidEmailsDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_003_sv_SE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify validation error overlays for invalid emails (sv-SE).")
    final void testInvalidEmailsSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_003_ja_JP")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify validation error overlays for invalid emails (ja-JP).")
    final void testInvalidEmailsJaJp() throws Throwable
    {
        executeTest();
    }

}
