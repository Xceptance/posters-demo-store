package com.xceptance.loadtest.actions;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.util.Account;
import com.xceptance.loadtest.util.Address;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * Fills in the shipping address form.
 * 
 * @author sebastianloob
 */
public class EnterShippingAddress extends AbstractHtmlPageAction
{

    /**
     * The shipping address form.
     */
    private HtmlForm shippingAddressForm;

    /**
     * The shipping address.
     */
    private Address address;

    /**
     * Account data to use.
     */
    private Account account;

    private HtmlElement submitAddressButton;

    public EnterShippingAddress(AbstractHtmlPageAction previousAction, String timerName, Account account,
        Address address)
    {
        super(previousAction, timerName);
        this.account = account;
        this.address = address;
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        // check that the form to enter a new shipping address is available
        Assert.assertTrue("Form to enter shipping address not found.",
                          HtmlPageUtils.isElementPresent(page, "id('formAddDelAddr')"));
        // remember the shipping address form
        this.shippingAddressForm = HtmlPageUtils.findSingleHtmlElementByID(page, "formAddDelAddr");
        // check that the button to submit the shipping address is available
        Assert.assertTrue("Button to submit shipping address not found.",
                          HtmlPageUtils.isElementPresent(page, "id('btnAddDelAddr')"));
        // remember the button to submit the shipping address
        this.submitAddressButton = HtmlPageUtils.findSingleHtmlElementByID(page, "btnAddDelAddr");
    }

    @Override
    protected void execute() throws Exception
    {
        // fill in the shipping address
        HtmlPageUtils.setInputValue(shippingAddressForm, "fullName",
                                    account.getFirstName() + " " + account.getLastName());
        HtmlPageUtils.setInputValue(shippingAddressForm, "company", address.getCompany());
        HtmlPageUtils.setInputValue(shippingAddressForm, "addressLine", address.getAddressLine());
        HtmlPageUtils.setInputValue(shippingAddressForm, "city", address.getCity());
        HtmlPageUtils.setInputValue(shippingAddressForm, "state", address.getState());
        HtmlPageUtils.setInputValue(shippingAddressForm, "zip", address.getZip());
        // we want to add a billing address separately, so both addresses are not equal
        HtmlPageUtils.checkRadioButton(shippingAddressForm, "billEqualShipp", 1);

        // submit the shipping address
        loadPageByClick(submitAddressButton);
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

        // check that it's the page to enter or select a billing address
        Assert.assertTrue("Title not found.", HtmlPageUtils.isElementPresent(page, "id('titleBillAddr')"));
        // check that the form to enter a new billing address is available
        Assert.assertTrue("Form to enter billing address not found.",
                          HtmlPageUtils.isElementPresent(page, "id('formAddBillAddr')"));
    }

}
