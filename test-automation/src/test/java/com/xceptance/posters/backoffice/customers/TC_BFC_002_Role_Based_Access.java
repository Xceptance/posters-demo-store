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

package com.xceptance.posters.backoffice.customers;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TC_BFC_002 - Role-Based Access for Customers Module.
 *
 * Verifies that the "Customer Admin" role correctly isolates the user to
 * the Customers module, and that unauthorized roles like "Catalog User"
 * are blocked from the Customers module.
 *
 * AI-generated: Gemini 3.5 Flash (Medium)
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/backoffice/customers/TC_BFC_002_Role_Based_Access.yaml")
@Tag("backoffice")
@Tag("security")
@Tag("rbac")
@Tag("customers")
@Tag("smoke")
@Tag("regression")
public class TC_BFC_002_Role_Based_Access
{
    @BeforeAll
    public static void setup()
    {
        // can be set in properties, just here so it's directly visible
        System.setProperty("neodymium.ai.interactive", "false");
    }

    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that Customer Admin has access restricted strictly to Customers, and Catalog User is blocked.")
    final void testRoleBasedAccess() throws Throwable
    {
        final long timestamp = System.currentTimeMillis();

        final String custAdminUsername = "cust_admin_" + timestamp;
        final String custAdminDisplayName = "Customer Admin " + timestamp;
        final String custAdminEmail = "custadmin_" + timestamp + "@posters.com";
        final String custAdminPassword = "customerAdmin2026!";

        final String catalogUserUsername = "catalog_user_" + timestamp;
        final String catalogUserDisplayName = "Catalog User " + timestamp;
        final String catalogUserEmail = "cataloguser_" + timestamp + "@posters.com";
        final String catalogUserPassword = "catalogUser2026!";

        Neodymium.getData().put("custAdminUsername", custAdminUsername);
        Neodymium.getData().put("custAdminDisplayName", custAdminDisplayName);
        Neodymium.getData().put("custAdminEmail", custAdminEmail);
        Neodymium.getData().put("custAdminPassword", custAdminPassword);

        Neodymium.getData().put("catalogUserUsername", catalogUserUsername);
        Neodymium.getData().put("catalogUserDisplayName", catalogUserDisplayName);
        Neodymium.getData().put("catalogUserEmail", catalogUserEmail);
        Neodymium.getData().put("catalogUserPassword", catalogUserPassword);

        Neodymium.ai().execute();
    }

}
