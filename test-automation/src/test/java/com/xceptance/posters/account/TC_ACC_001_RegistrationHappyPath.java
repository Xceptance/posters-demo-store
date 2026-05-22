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
 * Automates TC_ACC_001 - Successful Account Registration.
 *
 * Verifies that a new user can successfully create an account when all fields
 * are filled with valid data, and checks account persistence after logout and login.
 *
 * AI-generated: Gemini 3.5 Flash
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_001_RegistrationHappyPath.yaml")
@Tag("account")
@Tag("registration")
@Tag("happy-path")
@Tag("smoke")
@Tag("regression")
public class TC_ACC_001_RegistrationHappyPath
{

    /**
     * Executes the main AI-driven test scenario, injecting a unique email.
     *
     * @throws Throwable if execution fails
     */
    private final void executeTest() throws Throwable
    {
        final String randomEmail = "test_TC_ACC_001_" + System.currentTimeMillis() + "@posters.com";
        Neodymium.getData().put("randomEmail", randomEmail);
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_001_en_US")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a user can successfully register, log out, and log back in (en-US).")
    final void testRegistrationEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_001_en_GB")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a user can successfully register, log out, and log back in (en-GB).")
    final void testRegistrationEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_001_de_DE")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a user can successfully register, log out, and log back in (de-DE).")
    final void testRegistrationDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_001_sv_SE")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a user can successfully register, log out, and log back in (sv-SE).")
    final void testRegistrationSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_001_ja_JP")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a user can successfully register, log out, and log back in (ja-JP).")
    final void testRegistrationJaJp() throws Throwable
    {
        executeTest();
    }

}
