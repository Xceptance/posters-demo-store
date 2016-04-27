package posters.loadtest.actions.account;

import org.junit.Assert;

import posters.loadtest.validators.HeaderValidator;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * Browse to the sign in page.
 */
public class GoToSignIn extends AbstractHtmlPageAction
{
    /**
     * The sign in button.
     */
    private HtmlElement signInButton;

    /**
     * Constructor.
     * 
     * @param previousAction
     *            The previously performed action
     */
    public GoToSignIn(final AbstractHtmlPageAction previousAction)
    {
        super(previousAction, null);
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the previous action.
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);

        // Check that no customer is logged in.
        Assert.assertTrue("A customer is already logged in.", HtmlPageUtils.isElementPresent(page, "id('btnShowLoginForm')"));

        // Remember the sign in button.
        signInButton = HtmlPageUtils.findSingleHtmlElementByID(page, "btnShowLoginForm");
    }

    @Override
    protected void execute() throws Exception
    {
        // Click the button to load the sign in page.
        loadPageByClick(signInButton);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Get the result of the action.
        final HtmlPage page = getHtmlPage();

        // Repeated basic checks - see action 'Homepage' for some more details how and when to use these validators.
        HttpResponseCodeValidator.getInstance().validate(page);
        ContentLengthValidator.getInstance().validate(page);
        HtmlEndTagValidator.getInstance().validate(page);

        HeaderValidator.getInstance().validate(page);

        // Check that it's the sign in page.
        Assert.assertTrue("Sign in form not found.", HtmlPageUtils.isElementPresent(page, "id('formLogin')"));
        Assert.assertTrue("Link to register not found.", HtmlPageUtils.isElementPresent(page, "id('linkRegister')"));
    }
}
