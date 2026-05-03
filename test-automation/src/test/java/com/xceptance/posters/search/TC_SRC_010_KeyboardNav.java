package com.xceptance.posters.search;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TC_SRC_010 - Keyboard Navigation & Accessibility
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_010_KeyboardNav.yaml")
@Tag("search")
@Tag("keyboard")
@Tag("a11y")
@Tag("navigation")
@Tag("regression")
@Tag("full")
public class TC_SRC_010_KeyboardNav
{
    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can fully utilize the search functionality using only a keyboard.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
