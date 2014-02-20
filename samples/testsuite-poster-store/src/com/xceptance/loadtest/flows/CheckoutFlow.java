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
 * 
 * @author sebastian
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
    private CreditCard creditCard = new CreditCard(account);

    /**
     * Constructor
     * 
     * @param previousAction
     */
    public CheckoutFlow(AbstractHtmlPageAction previousAction, Account account)
    {
	this.previousAction = previousAction;
	this.account = account;

    }

    /**
     * {@inheritDoc}
     */
    public AbstractHtmlPageAction run() throws Throwable
    {

	// start the checkout
	StartCheckout startCheckout = new StartCheckout(previousAction,
		"StartCheckout");
	startCheckout.run();

	// enter the shipping address
	EnterShippingAddress enterShippingAddress = new EnterShippingAddress(
		startCheckout, "EnterShippingAddress", account, shippingAddress);
	enterShippingAddress.run();

	// enter the billing address
	EnterBillingAddress enterBillingAddress = new EnterBillingAddress(
		enterShippingAddress, "EnterBillingAddress", account,
		billingAddress);
	enterBillingAddress.run();

	// enter the payment method
	EnterPaymentMethod enterPaymentMethod = new EnterPaymentMethod(
		enterBillingAddress, "EnterPaymentMethod", creditCard);
	enterPaymentMethod.run();

	return enterPaymentMethod;
    }
}
