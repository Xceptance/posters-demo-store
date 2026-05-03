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
 * Automates TC_SRC_009 - Search Input Boundary & Stress
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_009_InputBoundary.yaml")
@Tag("search")
@Tag("boundary")
@Tag("stress")
@Tag("security")
@Tag("edge-case")
@Tag("robustness")
@Tag("full")
public class TC_SRC_009_InputBoundary
{
    @NeodymiumTest
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search handles extreme and unusual input gracefully without server errors, application crashes, or security vulnerabilities.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
