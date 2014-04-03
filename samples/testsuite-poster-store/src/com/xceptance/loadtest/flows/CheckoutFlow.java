package com.xceptance.loadtest.flows;

import com.xceptance.loadtest.actions.order.EnterBillingAddress;
import com.xceptance.loadtest.actions.order.EnterPaymentMethod;
import com.xceptance.loadtest.actions.order.EnterShippingAddress;
import com.xceptance.loadtest.actions.order.StartCheckout;
import com.xceptance.loadtest.util.Account;
import com.xceptance.loadtest.util.Address;
import com.xceptance.loadtest.util.CreditCard;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;

/**
 * Perform the checkout steps: Start checkout from cart overview page, enter
 * shipping and billing address and payment method but do not submit the order.
 * The checkout is encapsulated in a flow that combines a sequence of several XLT actions.
 * Different test cases can call this method now to reuse the flow. 
 * This is a concept for code structuring you can implement if needed, yet explicit support 
 * is neither available in the XLT framework nor necessary when you manually create a flow.
 * 
 */
public class CheckoutFlow
{
    /**
     * The previous action
     */
    private AbstractHtmlPageAction previousAction;

    /**
     * This account data will be used to create a new account.
     */
    private Account account;

    /**
     * Create new address. Use this address as shipping address.
     */
    private Address shippingAddress = new Address();

    /**
     * Create new address. Use this address as billing address.
     */
    private Address billingAddress = new Address();

    /**
     * Create new credit card. Use this credit card as payment method.
     */
    private CreditCard creditCard;

    /**
     * Constructor
     * 
     * @param previousAction
     * 		The previously performed action
     * @param account
     * 		The account used in the checkout
     */
    public CheckoutFlow(AbstractHtmlPageAction previousAction, Account account)
    {
	this.previousAction = previousAction;
	this.account = account;
	this.creditCard = new CreditCard(account);
    }

    
    /**
     * {@inheritDoc}
     */
    public AbstractHtmlPageAction run() throws Throwable
    {
	// Start the checkout
	StartCheckout startCheckout = new StartCheckout(previousAction);
	startCheckout.run();

	// Enter the shipping address
	EnterShippingAddress enterShippingAddress = new EnterShippingAddress(
		startCheckout, account, shippingAddress);
	enterShippingAddress.run();

	// Enter the billing address
	EnterBillingAddress enterBillingAddress = new EnterBillingAddress(
		enterShippingAddress, account,
		billingAddress);
	enterBillingAddress.run();

	// Enter the payment method
	EnterPaymentMethod enterPaymentMethod = new EnterPaymentMethod(
		enterBillingAddress, creditCard);
	enterPaymentMethod.run();

	// Return the last action of this flow to be the input for subsequent actions in a test case
	return enterPaymentMethod;
    }
}
