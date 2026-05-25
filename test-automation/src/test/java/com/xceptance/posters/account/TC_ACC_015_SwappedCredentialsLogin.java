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
 * Automates TC_ACC_015 - Invalid Login - Swapped Credentials.
 *
 * Verifies that the login fails cleanly when a user accidentally swaps their
 * email and password in the input fields.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_015_SwappedCredentialsLogin.yaml")
@Tag("account")
@Tag("login")
@Tag("validation")
@Tag("regression")
public class TC_ACC_015_SwappedCredentialsLogin
{

    /**
     * Executes the main AI-driven test scenario, injecting a unique email.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        final String randomEmail = "test_TC_ACC_015_" + System.currentTimeMillis() + "@posters.com";
        Neodymium.getData().put("randomEmail", randomEmail);
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_015_en_US")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login fails when email and password fields are swapped (en-US).")
    final void testSwappedCredentialsEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_015_en_GB")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login fails when email and password fields are swapped (en-GB).")
    final void testSwappedCredentialsEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_015_de_DE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login fails when email and password fields are swapped (de-DE).")
    final void testSwappedCredentialsDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_015_sv_SE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login fails when email and password fields are swapped (sv-SE).")
    final void testSwappedCredentialsSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_015_ja_JP")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login fails when email and password fields are swapped (ja-JP).")
    final void testSwappedCredentialsJaJp() throws Throwable
    {
        executeTest();
    }

}
