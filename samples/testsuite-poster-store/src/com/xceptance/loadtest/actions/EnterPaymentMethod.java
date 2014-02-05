package com.xceptance.loadtest.actions;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.util.CreditCard;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * Fills in the payment method form.
 * 
 * @author sebastianloob
 */
public class EnterPaymentMethod extends AbstractHtmlPageAction
{

    /**
     * The payment form.
     */
    private HtmlForm paymentForm;

    /**
     * The payment method.
     */
    private CreditCard creditCard;

    private HtmlElement submitPaymentMethod;

    public EnterPaymentMethod(AbstractHtmlPageAction previousAction, String timerName, final CreditCard creditCard)
    {
        super(previousAction, timerName);
        this.creditCard = creditCard;
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        // check that the form to enter a new credit card is available
        Assert.assertTrue("Form to enter credit card not found.",
                          HtmlPageUtils.isElementPresent(page, "id('formAddPayment')"));
        // remember the payment form
        this.paymentForm = HtmlPageUtils.findSingleHtmlElementByID(page, "formAddPayment");
        // check that the button to submit the payment method is available
        Assert.assertTrue("Button to submit payment method not found.",
                          HtmlPageUtils.isElementPresent(page, "id('btnAddPayment')"));
        // remember the button to submit the payment method
        this.submitPaymentMethod = HtmlPageUtils.findSingleHtmlElementByID(page, "btnAddPayment");
    }

    @Override
    protected void execute() throws Exception
    {
        // fill in the payment method
        HtmlPageUtils.setInputValue(paymentForm, "number", creditCard.getNumber());
        HtmlPageUtils.selectRandomly(paymentForm, "expirationDateMonth");
        HtmlPageUtils.selectRandomly(paymentForm, "expirationDateYear");

        // submit the billing address
        loadPageByClick(submitPaymentMethod);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getHtmlPage();

        // Basic checks - see action 'Homepage' for some more details how and when to use these validators
        HttpResponseCodeValidator.getInstance().validate(page);
        ContentLengthValidator.getInstance().validate(page);
        HtmlEndTagValidator.getInstance().validate(page);

        HeaderValidator.getInstance().validate(page);
        // TODO
        // check that it's the page to enter or select a payment method
        Assert.assertTrue("Title not found.", HtmlPageUtils.isElementPresent(page, "id('titlePayment')"));
        // check that the form to enter a new payment method is available
        Assert.assertTrue("Form to enter payment method not found.",
                          HtmlPageUtils.isElementPresent(page, "id('formAddPayment')"));
    }

}
