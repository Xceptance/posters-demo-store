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
 * Automates TC_ACC_011 - Successful Account Login.
 *
 * Verifies that a user can successfully log in using valid registered credentials.
 * Dynamically registers the account first as a self-contained precondition.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_011_SuccessfulLogin.yaml")
@Tag("account")
@Tag("login")
@Tag("happy-path")
@Tag("regression")
public class TC_ACC_011_SuccessfulLogin
{

    /**
     * Executes the main AI-driven test scenario, injecting a unique email.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        final String randomEmail = "test_TC_ACC_011_" + System.currentTimeMillis() + "@posters.com";
        Neodymium.getData().put("randomEmail", randomEmail);
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_011_en_US")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify successful account login with valid credentials (en-US).")
    final void testSuccessfulLoginEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_011_en_GB")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify successful account login with valid credentials (en-GB).")
    final void testSuccessfulLoginEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_011_de_DE")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify successful account login with valid credentials (de-DE).")
    final void testSuccessfulLoginDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_011_sv_SE")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify successful account login with valid credentials (sv-SE).")
    final void testSuccessfulLoginSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_011_ja_JP")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify successful account login with valid credentials (ja-JP).")
    final void testSuccessfulLoginJaJp() throws Throwable
    {
        executeTest();
    }

}
