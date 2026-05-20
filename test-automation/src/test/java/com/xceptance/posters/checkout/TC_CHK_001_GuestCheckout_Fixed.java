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
 *
 * // AI-generated: Gemini 3.5 Flash
 */
package com.xceptance.posters.checkout;

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
 * Automates TC_CHK_001 - Guest Checkout with a fixed product (Grizzly Bear) for stable testing.
 *
 * Covers the full guest checkout flow: add to cart, shipping address, billing address,
 * payment, order review (including financial total verification), and order confirmation.
 * Runs against all supported locales as individual test cases.
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/checkout/TC_CHK_001_GuestCheckout_Fixed.yaml")
@Tag("checkout")
@Tag("guest")
@Tag("happy-path")
@Tag("smoke")
@Tag("regression")
@Tag("full")
public class TC_CHK_001_GuestCheckout_Fixed
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
    @DataSet(id = "tc_chk_001_fixed_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a guest user can complete the full checkout flow using the fixed 'Grizzly Bear' product (en-US).")
    final void testGuestCheckoutEnUS() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_chk_001_fixed_en_GB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a guest user can complete the full checkout flow using the fixed 'Grizzly Bear' product (en-GB).")
    final void testGuestCheckoutEnGB() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_chk_001_fixed_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a guest user can complete the full checkout flow using the fixed 'Grizzly Bear' product (de-DE).")
    final void testGuestCheckoutDeDE() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_chk_001_fixed_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a guest user can complete the full checkout flow using the fixed 'Grizzly Bear' product (sv-SE).")
    final void testGuestCheckoutSvSE() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_chk_001_fixed_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a guest user can complete the full checkout flow using the fixed 'Grizzly Bear' product (ja-JP).")
    final void testGuestCheckoutJaJP() throws Throwable
    {
        executeTest();
    }
}
