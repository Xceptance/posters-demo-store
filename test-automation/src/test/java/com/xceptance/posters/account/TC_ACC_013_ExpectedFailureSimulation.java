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
 * AI-driven test scenario simulating expected failure (bug tag) in the middle.
 *
 * AI-generated: Antigravity
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/account/TC_ACC_013_ExpectedFailureSimulation.yaml")
@Tag("account")
@Tag("regression")
@Tag("expected-failure")
public class TC_ACC_013_ExpectedFailureSimulation
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
    @DataSet(id = "tc_acc_013_bug_sim")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify playbook aborts early at expected failure step and terminates successfully.")
    final void testExpectedFailureSimulation() throws Throwable
    {
        executeTest();
    }

}
