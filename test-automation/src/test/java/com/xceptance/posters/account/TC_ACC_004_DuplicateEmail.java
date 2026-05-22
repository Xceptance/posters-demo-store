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
 * Automates TC_ACC_004 - Duplicate Account Registration.
 *
 * Verifies that registering an account with an already registered email is rejected.
 * Dynamically registers the email first as a self-contained precondition.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_004_DuplicateEmail.yaml")
@Tag("account")
@Tag("registration")
@Tag("security")
@Tag("regression")
public class TC_ACC_004_DuplicateEmail
{

    /**
     * Executes the main AI-driven test scenario, injecting a unique email.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        final String existingEmail = "test_TC_ACC_004_" + System.currentTimeMillis() + "@posters.com";
        Neodymium.getData().put("existingEmail", existingEmail);
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_004_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify duplicate account registration is rejected (en-US).")
    final void testDuplicateEmailEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_004_en_GB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify duplicate account registration is rejected (en-GB).")
    final void testDuplicateEmailEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_004_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify duplicate account registration is rejected (de-DE).")
    final void testDuplicateEmailDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_004_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify duplicate account registration is rejected (sv-SE).")
    final void testDuplicateEmailSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_004_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify duplicate account registration is rejected (ja-JP).")
    final void testDuplicateEmailJaJp() throws Throwable
    {
        executeTest();
    }

}
