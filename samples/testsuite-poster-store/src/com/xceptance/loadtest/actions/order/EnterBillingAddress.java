package com.xceptance.loadtest.actions.order;

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
 * Fills in the billing address form.
 * 
 * @author sebastianloob
 */
public class EnterBillingAddress extends AbstractHtmlPageAction
{

    /**
     * The billing address form.
     */
    private HtmlForm billingAddressForm;

    /**
     * The billing address.
     */
    private Address address;

    /**
     * Account data to use.
     */
    private Account account;

    /**
     * The submit address button
     */
    private HtmlElement submitAddressButton;

    
    /**
     * Constructor that takes an existing account to get a first name and last name
     * 
     * @param previousAction
     * @param timerName
     * @param account
     * @param address
     */
    public EnterBillingAddress(AbstractHtmlPageAction previousAction, String timerName, Account account, Address address)
    {
        super(previousAction, timerName);
        this.account = account;
        this.address = address;
    }

   /**
    * Constructor that creates an new account to get a first name and a last name
    * 
    * @param previousAction
    * @param timerName
    * @param address
    */
    public EnterBillingAddress(AbstractHtmlPageAction previousAction, String timerName, Address address)
    {
        super(previousAction, timerName);
        this.account = new Account();
        this.address = address;
    }

    
    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);
        
        // check that the form to enter a new billing address is available
        Assert.assertTrue("Form to enter billing address not found.",
                          HtmlPageUtils.isElementPresent(page, "id('formAddBillAddr')"));
        
        // remember the billing address form
        this.billingAddressForm = HtmlPageUtils.findSingleHtmlElementByID(page, "formAddBillAddr");
        
        // check that the button to submit the billing address is available
        Assert.assertTrue("Button to submit billing address not found.",
                          HtmlPageUtils.isElementPresent(page, "id('btnAddBillAddr')"));
        
        // remember the button to submit the billing address
        this.submitAddressButton = HtmlPageUtils.findSingleHtmlElementByID(page, "btnAddBillAddr");
    }

    @Override
    protected void execute() throws Exception
    {
        // fill in the billing address
        HtmlPageUtils.setInputValue(billingAddressForm, "fullName",
                                    account.getFirstName() + " " + account.getLastName());
        HtmlPageUtils.setInputValue(billingAddressForm, "company", address.getCompany());
        HtmlPageUtils.setInputValue(billingAddressForm, "addressLine", address.getAddressLine());
        HtmlPageUtils.setInputValue(billingAddressForm, "city", address.getCity());
        HtmlPageUtils.setInputValue(billingAddressForm, "state", address.getState());
        HtmlPageUtils.setInputValue(billingAddressForm, "zip", address.getZip());

        // submit the billing address
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

        // check that it's the page to enter or select a payment method
        Assert.assertTrue("Title not found.", HtmlPageUtils.isElementPresent(page, "id('titlePayment')"));
        
        // check that the form to enter a new payment method is available
        Assert.assertTrue("Form to enter payment method not found.",
                          HtmlPageUtils.isElementPresent(page, "id('formAddPayment')"));
    }

}
