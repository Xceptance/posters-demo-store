package com.xceptance.posters.checkout;

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
 * Automates TC_CHK_001 - Guest Checkout (Happy Path)
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/checkout/TC_CHK_001_GuestCheckout.yaml")
@Tag("checkout")
@Tag("guest")
@Tag("happy-path")
@Tag("smoke")
@Tag("regression")
@Tag("full")
public class TC_CHK_001_GuestCheckout
{
    @BeforeAll
    public static void setup()
    {
        System.setProperty("neodymium.ai.interactive", "false");
    }

    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a guest user can add an item, navigate checkout, enter details, pay with a credit card, and successfully place an order.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
