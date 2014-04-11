package posters.loadtest.validators;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Checks for the correct header elements
 */
public class SideNavValidator
{
    /**
     * Make a stateless singleton available.
     */
    private static final SideNavValidator instance = new SideNavValidator();

    /**
     * Checks the poster store side navigation elements
     * 
     * @param page
     *            the page to check
     */
    public void validate(final HtmlPage page) throws Exception
    {

        // Check that the side navigation contains at least two top categories
        // For this purpose we get a list of all top categories and check the
        // size of the list
        final List<HtmlElement> topCategories = HtmlPageUtils.findHtmlElements(page, "id('sidebarNav')/ul/li[@class='topCategory']");
        Assert.assertTrue("There are less then two top categories in the side nav.", topCategories.size() >= 2);

        // Check that each top category is followed by at least one level-1
        // category
        for (final Iterator<HtmlElement> iterator = topCategories.iterator(); iterator.hasNext();)
        {
            final HtmlElement htmlElement = iterator.next();
            // relative xpath to address the first sibling after the top
            // category that is a level-1 category
            Assert.assertTrue("Top category is not followed by a level-1 category.",
                              HtmlPageUtils.isElementPresent(htmlElement, "./following-sibling::li[1][@class='level-1']"));
        }

    }

    /**
     * The instance for easy reuse. Possible because this validator is stateless.
     * 
     * @return the instance
     */
    public static SideNavValidator getInstance()
    {
        return instance;
    }
}
