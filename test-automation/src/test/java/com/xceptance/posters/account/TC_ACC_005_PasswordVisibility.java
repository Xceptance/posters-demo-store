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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TC_ACC_005 - Password Visibility Toggle (Registration).
 *
 * Verifies that the eye icon correctly toggles the password input masking
 * by switching the input type between 'password' and 'text'.
 *
 * AI-generated: Gemini 2.5 Pro
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_005_PasswordVisibility.yaml")
@Tag("account")
@Tag("registration")
@Tag("ui")
@Tag("regression")
public class TC_ACC_005_PasswordVisibility
{
    @BeforeAll
    public static void setup()
    {
        // can be set in properties, just here so it's directly visible
        //System.setProperty("neodymium.ai.interactive", "true");
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
    @DataSet(id = "tc_acc_005_en_US")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify password visibility toggling during registration (en-US).")
    final void testPasswordVisibilityEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_005_en_GB")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify password visibility toggling during registration (en-GB).")
    final void testPasswordVisibilityEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_005_de_DE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify password visibility toggling during registration (de-DE).")
    final void testPasswordVisibilityDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_005_sv_SE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify password visibility toggling during registration (sv-SE).")
    final void testPasswordVisibilitySvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_acc_005_ja_JP")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify password visibility toggling during registration (ja-JP).")
    final void testPasswordVisibilityJaJp() throws Throwable
    {
        executeTest();
    }

}
