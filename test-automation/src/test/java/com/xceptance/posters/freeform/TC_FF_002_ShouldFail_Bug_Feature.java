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
package com.xceptance.posters.freeform;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.common.testdata.DataSet;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test that we fail as expected and don't continue. It is expected that this
 * test fails but that is by design and hence no complains, only when we change
 * the code we suddenly fail because the bug has been fixed.
 */
@Browser("Chrome_1500x1000")
@Tag("neofeaturetest")
public class TC_FF_002_ShouldFail_Bug_Feature
{
    /**
     * No fail
     *
     * @throws Throwable if execution fails
     */
    @NeodymiumTest
    final void assertionFailBug() throws Throwable
    {
        Neodymium.ai()
                .steps("""
                        # Homepage
                        Open ${neodymium.url}
                        # Verify something that is not true aka a defect and we know that
                        # Sure this is not a true bug, just made up for this test.
                        Verify that the minicart shows two items (bug).
                        # This is not executed!!!
                        Verify that the top header shows a warning about a demo application.
                        """)
                .execute();
    }

    /**
     * Fail with an assertion because expected bug done.
     *
     * @throws Throwable if execution fails
     */
    @NeodymiumTest
    final void assertionFailBug_BugGone() throws Throwable
    {
        assertThrows(AssertionError.class, () ->
        {
            Neodymium.ai()
                    .steps("""
                            # Homepage
                            Open ${neodymium.url}
                            # Verify something that is not true aka a defect and we know that
                            # Sure this is not a true bug, just made up for this test.
                            Verify that the minicart shows 0 items (bug).
                            # This is not executed!!!
                            Verify that the top header shows a warning about a demo application.
                            """)
                    .execute();
        });
    }

        /**
     * Fail with an assertion
     *
     * @throws Throwable if execution fails
     */
    @NeodymiumTest
    final void assertionFailVisualBug() throws Throwable
    {
        Neodymium.ai()
                .steps("""
                        # Homepage
                        Open ${neodymium.url}
                        # Verify something that is not true aka a defect and we know that
                        # Sure this is not a true bug, just made up for this test.
                        Verify that the screen is mostly black and white (bug) (visual).
                        # This is not executed!!!
                        Verify that the top header shows a warning about a demo application.
                        """)
                .execute();
    }

    /**
     * Ok, the bug is gone... this should escalate.
     *
     * @throws Throwable if execution fails
     */
    @NeodymiumTest
    final void assertionFailVisualBug_BugGone() throws Throwable
    {
        assertThrows(AssertionError.class, () ->
        {
            Neodymium.ai()
                    .steps("""
                            # Homepage
                            Open ${neodymium.url}
                            # Verify something that is not true aka a defect and we know that
                            # Sure this is not a true bug, just made up for this test.
                            Verify that the screen is mostly blue and white (bug) (visual).
                            # This is not executed!!!
                            Verify that the top header shows a warning about a demo application.
                            """)
                    .execute();
        });
    }
}
