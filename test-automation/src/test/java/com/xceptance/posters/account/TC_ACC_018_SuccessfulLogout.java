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
 * Automates TC_ACC_018 - Successful Account Logout.
 *
 * Verifies that clicking "Logout" successfully terminates the user session,
 * and that navigating backward using the browser's back button does not restore
 * the session or expose authenticated content.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_018_SuccessfulLogout.yaml")
@Tag("account")
@Tag("logout")
@Tag("session")
@Tag("security")
@Tag("regression")
public class TC_ACC_018_SuccessfulLogout
{

    /**
     * Executes the main AI-driven test scenario, injecting a unique email.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        final String randomEmail = "test_TC_ACC_018_" + System.currentTimeMillis() + "@posters.com";
        Neodymium.getData().put("randomEmail", randomEmail);
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_018_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful logout and session termination (en-US).")
    final void testSuccessfulLogoutEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_018_en_GB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful logout and session termination (en-GB).")
    final void testSuccessfulLogoutEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_018_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful logout and session termination (de-DE).")
    final void testSuccessfulLogoutDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_018_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful logout and session termination (sv-SE).")
    final void testSuccessfulLogoutSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_018_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful logout and session termination (ja-JP).")
    final void testSuccessfulLogoutJaJp() throws Throwable
    {
        executeTest();
    }

}
