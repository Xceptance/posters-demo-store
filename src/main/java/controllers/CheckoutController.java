package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Basket;
import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.DeliveryAddress;
import models.Order;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.database.BasketInformation;
import util.database.CommonInformation;
import util.database.CustomerInformation;
import util.database.OrderInformation;
import util.date.DateUtils;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;

public class CheckoutController {

	public Result checkout(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		String template = "";
		Basket basket = BasketInformation.getBasketById(SessionHandling
				.getBasketId(context));
		// check, if the basket is empty
		if (basket.getProductIds().size() == 0) {
			template = "views/error/emptyBasket.ftl.html";
		}
		// start checkout, if the basket is not empty
		else {
			// create new order
			Order order = OrderInformation.createNewOrder();
			// put order id to session
			SessionHandling.setOrderId(context, order.getId());
			// set basket to order
			order.setBasket(basket);
			// set delivery address information to data

			// customer is logged
			if (SessionHandling.isCustomerLogged(context)) {
				Customer customer = CustomerInformation
						.getCustomerById(SessionHandling.getCustomerId(context));
				order.setCustomer(customer);
				template = "views/CheckoutController/deliveryAddress.ftl.html";
			}
			// guest
			else {
				template = "views/CheckoutController/deliveryAddress.ftl.html";
			}
			System.out.println("Order-ID: " + order.getId());
			Ebean.save(order);
		}

		return Results.html().render(data).template(template);
	}

	public Result deliveryAddressCompleted(@Param("fullName") String name,
			@Param("addressLine1") String addressLine1,
			@Param("addressLine2") String addressLine2,
			@Param("city") String city, @Param("state") String state,
			@Param("zip") String zip, @Param("country") String country,
			@Param("billingAddress") String billingEqualDelivery,
			Context context) {

		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		// create delivery address

		DeliveryAddress deliveryAddress = new DeliveryAddress();
		deliveryAddress.setName(name);
		deliveryAddress.setAddressline1(addressLine1);
		deliveryAddress.setAddressline2(addressLine2);
		deliveryAddress.setCity(city);
		deliveryAddress.setState(state);
		deliveryAddress.setZip(Integer.parseInt(zip));
		deliveryAddress.setCountry(country);

		Order order = OrderInformation.getOrderById(SessionHandling
				.getOrderId(context));

		order.setDeliveryAddress(deliveryAddress);

		String template;
		// billing address is equal to delivery address
		if (billingEqualDelivery.equals("Yes")) {
			// create billing address
			BillingAddress billingAddress = new BillingAddress();
			billingAddress.setName(name);
			billingAddress.setAddressline1(addressLine1);
			billingAddress.setAddressline2(addressLine2);
			billingAddress.setCity(city);
			billingAddress.setState(state);
			billingAddress.setZip(Integer.parseInt(zip));
			billingAddress.setCountry(country);

			order.setBillingAddress(billingAddress);

			template = "views/CheckoutController/paymentMethod.ftl.html";
		} else {
			template = "views/CheckoutController/billingAddress.ftl.html";
		}

		Ebean.save(order);

		return Results.html().render(data).template(template);
	}

	public Result billingAddressCompleted(@Param("fullName") String name,
			@Param("addressLine1") String addressLine1,
			@Param("addressLine2") String addressLine2,
			@Param("city") String city, @Param("state") String state,
			@Param("zip") String zip, @Param("country") String country,
			Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		BillingAddress billingAddress = new BillingAddress();
		billingAddress.setName(name);
		billingAddress.setAddressline1(addressLine1);
		billingAddress.setAddressline2(addressLine2);
		billingAddress.setCity(city);
		billingAddress.setState(state);
		billingAddress.setZip(Integer.parseInt(zip));
		billingAddress.setCountry(country);

		Order order = OrderInformation.getOrderById(SessionHandling
				.getOrderId(context));

		order.setBillingAddress(billingAddress);

		String template = "views/CheckoutController/paymentMethod.ftl.html";

		Ebean.save(order);

		return Results.html().render(data).template(template);
	}

	public Result paymentMethodCompleted(
			@Param("creditCardNumber") int creditNumber,
			@Param("name") String name,
			@Param("expirationDateMonth") int month,
			@Param("expirationDateYear") int year, Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		CreditCard creditCard = new CreditCard();

		creditCard.setNumber(creditNumber);
		creditCard.setName(name);
		creditCard.setMonth(month);
		creditCard.setYear(year);

		Order order = OrderInformation.getOrderById(SessionHandling
				.getOrderId(context));

		order.setCreditCard(creditCard);

		String template = "views/CheckoutController/checkoutOverview.ftl.html";

		Ebean.save(order);

		return Results.html().render(data).template(template);
	}
	
	public Result checkoutCompleted(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();
		// set date to order
		Order order = OrderInformation.getOrderById(SessionHandling
				.getOrderId(context));
		order.setDate(DateUtils.getCurrentDate());
		System.out.println("Order date:" + order.getDate());
		Ebean.save(order);
		// remove basket
		Basket basket = BasketInformation.getBasketById(SessionHandling
				.getBasketId(context));
		BasketInformation.removeBasket(context, basket);
		// remove basket from session
		SessionHandling.setBasketId(context, -1);
		// remove order from session
		SessionHandling.setOrderId(context, -1);
		// remove order, if a guest bought something

		CommonInformation.setOverallData(data, context);

		return Results.html().render(data);
	}
}
