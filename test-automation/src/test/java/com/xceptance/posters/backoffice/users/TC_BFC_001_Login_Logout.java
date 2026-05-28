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

package com.xceptance.posters.backoffice.users;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;
import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TC_BFC_001 - Backoffice Login and Logout.
 *
 * Verifies that an administrative user can log into the backoffice successfully,
 * reload to verify session persistence, and log out securely.
 *
 * AI-generated: Gemini 3.5 Flash (Medium)
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/backoffice/users/TC_BFC_001_Login_Logout.yaml")
@Tag("backoffice")
@Tag("security")
@Tag("login")
@Tag("smoke")
@Tag("regression")
public class TC_BFC_001_Login_Logout
{
    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify backoffice login, session persistence, and secure logout.")
    final void testLoginLogout() throws Throwable
    {
        Neodymium.ai().execute();
    }

}
