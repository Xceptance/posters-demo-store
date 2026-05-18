/*
 * MIT License
 *
 * Copyright (c) 2026 Xceptance Software Technologies GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xceptance.posters.cart;

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
 * Automates TC_CRT_001 - Cart Price & Tax Totals Verification.
 *
 * Verifies that the cart summary panel displays correct subtotal, tax, shipping,
 * and total values. Tax must be calculated as (subtotal + shipping) × taxRate.
 * Also verifies the tax rate display format and the row display order.
 *
 * Known failing assertions on the current build:
 * - BUS-BUG-23: Tax amount calculated incorrectly (shows $0.01 instead of ~$1.44)
 * - BUS-BUG-24: Tax rate format shows '6.0%' instead of required '6.00%'
 * - BUS-BUG-25: Tax row appears before Shipping row in the cart summary
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/cart/TC_CRT_001_CartTotals.yaml")
@Tag("cart")
@Tag("totals")
@Tag("tax")
@Tag("pricing")
@Tag("smoke")
@Tag("regression")
public class TC_CRT_001_CartTotals
{
    /**
     * Common execution delegate for all locale-specific test methods.
     *
     * @throws Throwable if an error occurs during execution
     */
    private void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_crt_001_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify cart summary totals: subtotal, tax rate format (6.00%), tax = (subtotal + shipping) x taxRate, and row order (en-US).")
    final void testCartTotalsEnUS() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_crt_001_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify cart summary totals: subtotal, tax rate format (6.00%), tax = (subtotal + shipping) x taxRate, and row order (de-DE).")
    final void testCartTotalsDeDE() throws Throwable
    {
        executeTest();
    }
}
