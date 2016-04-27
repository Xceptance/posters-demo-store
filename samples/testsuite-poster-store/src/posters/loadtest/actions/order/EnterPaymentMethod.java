package posters.loadtest.actions.order;

import org.junit.Assert;

import posters.loadtest.util.CreditCard;
import posters.loadtest.validators.HeaderValidator;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * Fill in and submit the payment form.
 */
public class EnterPaymentMethod extends AbstractHtmlPageAction
{
    /**
     * The payment form.
     */
    private HtmlForm paymentForm;

    /**
     * The credit card data.
     */
    private final CreditCard creditCard;

    /**
     * The payment method.
     */
    private HtmlElement submitPaymentMethod;

    /**
     * Constructor
     * 
     * @param previousAction
     *            The previously performed action
     * @param creditCard
     *            The credit card used for payment
     */
    public EnterPaymentMethod(final AbstractHtmlPageAction previousAction, final CreditCard creditCard)
    {
        super(previousAction, null);
        this.creditCard = creditCard;
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the previous action.
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);

        // Check that the form to enter a new credit card is available.
        Assert.assertTrue("Form to enter credit card not found.", HtmlPageUtils.isElementPresent(page, "id('formAddPayment')"));

        // Remember the payment form.
        paymentForm = HtmlPageUtils.findSingleHtmlElementByID(page, "formAddPayment");

        // Check that the button to submit the payment method is available.
        Assert.assertTrue("Button to submit payment method not found.", HtmlPageUtils.isElementPresent(page, "id('btnAddPayment')"));

        // Remember the button to submit the payment method.
        submitPaymentMethod = HtmlPageUtils.findSingleHtmlElementByID(page, "btnAddPayment");
    }

    @Override
    protected void execute() throws Exception
    {
        // Fill in the payment method.
        HtmlPageUtils.setInputValue(paymentForm, "creditCardNumber", creditCard.getNumber());
        HtmlPageUtils.setInputValue(paymentForm, "name", creditCard.getOwner());
        HtmlPageUtils.selectRandomly(paymentForm, "expirationDateMonth");
        HtmlPageUtils.selectRandomly(paymentForm, "expirationDateYear");

        // Submit the billing address.
        loadPageByClick(submitPaymentMethod);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Get the result of the action.
        final HtmlPage page = getHtmlPage();

        // Basic checks - see action 'Homepage' for some more details how and when to use these validators.
        HttpResponseCodeValidator.getInstance().validate(page);
        ContentLengthValidator.getInstance().validate(page);
        HtmlEndTagValidator.getInstance().validate(page);

        HeaderValidator.getInstance().validate(page);

        // Check that it's the order overview page.
        Assert.assertTrue("Title not found.", HtmlPageUtils.isElementPresent(page, "id('titleOrderOverview')"));
    }
}
