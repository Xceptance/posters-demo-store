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
// AI-generated: Antigravity (Gemini 2.5 Pro)
package com.xceptance.posters.freeform;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

import org.junit.jupiter.api.Tag;

/**
 * A base freeform test case template designed for running experimental
 * automated
 * tests without pre-defined scenarios.
 */
@Browser("Chrome_1500x1000")
@Tag("freeform")
public class TC_FF_001_FreeformTest
{
    /**
     * Just demo simple things
     *
     * @throws Throwable if execution fails
     */
    @NeodymiumTest
    final void testFreeform() throws Throwable
    {
        Neodymium.ai()
                .steps("""
                        # This is just a freeform test for some simple features of the automation.
                        # It is not intended to test any business functionality as defined in our test cases.
                        Open ${neodymium.url}
                        # This comes up with a verification for some critereria, it is not visual!
                        Verify that you see an ecommerce store homepage, which sells posters.
                        # Try to check for the bear.
                        Verify that a brown bear photo is show on the right side of the homepage (visual).
                        Check that the page is mainly white and blue (visual).
                        Top left, we can see a red X next to the text Posters (visual).
                        # Search and visual identify the search button
                        Type 'bear' into the search box.
                        Click the blue button next to the input box (visual).
                        # Visually verify the bears
                        Verify that the result page shows a brown bear, a picture with gummy bears, and a picture with metallic shiny gummy bears on a notebook keyboard (visual).
                        # Flag
                        Store the color of the animal shown on the second result image in the variable 'animalColor' (visual).
                        """)
                .execute();
    }
}
