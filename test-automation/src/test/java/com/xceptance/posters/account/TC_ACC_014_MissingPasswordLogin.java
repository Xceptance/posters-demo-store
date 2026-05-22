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
 * Automates TC_ACC_014 - Missing Password Validation (Login).
 *
 * Verifies that the login form blocks submission and displays a native browser overlay
 * when the Password field is omitted.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_014_MissingPasswordLogin.yaml")
@Tag("account")
@Tag("login")
@Tag("validation")
@Tag("regression")
public class TC_ACC_014_MissingPasswordLogin
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
    @DataSet(id = "tc_acc_014_en_US")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify native browser warning when Password is omitted on Login (en-US).")
    final void testMissingPasswordEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_014_en_GB")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify native browser warning when Password is omitted on Login (en-GB).")
    final void testMissingPasswordEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_014_de_DE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify native browser warning when Password is omitted on Login (de-DE).")
    final void testMissingPasswordDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_014_sv_SE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify native browser warning when Password is omitted on Login (sv-SE).")
    final void testMissingPasswordSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_014_ja_JP")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify native browser warning when Password is omitted on Login (ja-JP).")
    final void testMissingPasswordJaJp() throws Throwable
    {
        executeTest();
    }

}
