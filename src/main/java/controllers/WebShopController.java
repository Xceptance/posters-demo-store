package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Basket;
import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.DeliveryAddress;
import models.Order;
import models.Product;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import util.database.BasketInformation;
import util.database.CarouselInformation;
import util.database.CustomerInformation;
import util.database.HeaderInformation;
import util.database.OrderInformation;
import util.database.ProductInformation;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;

public class WebShopController {

	public Result index(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		setOverallData(data, context);

		// remember products for carousel
		CarouselInformation.getCarouselProducts(data);

		// ---Test Anfang---
		// context.getSessionCookie().clear();
		// System.out.println("Session Handling ID: " +
		// SessionHandling.getId(context));
		// System.out.println("Session Handling USER: " +
		// SessionHandling.get(context, "user"));
		// SessionHandling.setUnknownUser(context);
		// System.out.println("Session Handling USER: " +
		// SessionHandling.get(context, "user"));
		// SessionHandling.put(context, "user", "ich");
		// ---Test Ende---
		return Results.html().render(data);
	}

	public Result login(@Param("email") String email,
			@Param("password") String password, Context context) {

		final Map<String, Object> data = new HashMap<String, Object>();

		boolean emailExist = CustomerInformation.emailExist(email, password);
		boolean correctPassowrd = CustomerInformation.correctPassword(email,
				password);
		if (emailExist && correctPassowrd) {
			// normal login
			// put customer id to session
			Customer customer = CustomerInformation.getCustomerByEmail(email);
			SessionHandling.setCustomerId(context, customer.getId());
		} else if (emailExist && !correctPassowrd) {
			// user exist, wrong password
		} else {
			// wrong email
		}

		setOverallData(data, context);
		CarouselInformation.getCarouselProducts(data);
		return Results.html().render(data).template("views/WebShopController/index.ftl.html");
	}
	
	public Result logout(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();
		
		SessionHandling.deleteCustomerId(context);
		
		setOverallData(data, context);
		CarouselInformation.getCarouselProducts(data);
		return Results.html().render(data).template("views/WebShopController/index.ftl.html");
	}
	
	public Result accountOverview(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();
		
		
		setOverallData(data, context);
		return Results.html().render(data);
	}
	
	public Result orderOverview(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();
		
		CustomerInformation.addOrderByCustomerToMap(context, data);
		
		setOverallData(data, context);
		return Results.html().render(data);
	}

	public Result productDetail(@PathParam("product") String productUrl,
			Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		setOverallData(data, context);

		// remember product for product detail site
		ProductInformation.addProductDetailToMap(productUrl, data);

		return Results.html().render(data);
	}

	public Result productOverview(@PathParam("subCategory") String subCategory,
			Context context) {

		final Map<String, Object> data = new HashMap<String, Object>();
		setOverallData(data, context);
		// remember products for the given sub category
		ProductInformation.addSubCategoryProductsToMap(subCategory, data);

		return Results.html().render(data);
	}

	public Result topCategoryOverview(
			@PathParam("topCategory") String topCategory, Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();
		setOverallData(data, context);
		// remember products for the given top category
		ProductInformation.addTopCategoryProductsToMap(topCategory, data);

		return Results.html()
				.template("views/WebShopController/productOverview.ftl.html")
				.render(data);
	}

	public Result addToCart(@Param("productName") String productId,
			Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		setOverallData(data, context);

		// get product by id
		Product product = ProductInformation.getProductById(Integer
				.parseInt(productId));
		// get basket by session
		Basket basket = BasketInformation.getBasketById(SessionHandling
				.getBasketId(context));
		// put basket id to session
		SessionHandling.setBasketId(context, basket.getId());

		BasketInformation.addProductToBasket(basket, product);

		// System.out.println("---- Count " + basket.getProductCount().keySet()
		// + " ----");
		// System.out.println("---- Count of product " +
		// basket.getProductCount().values() + " ----");
		BasketInformation.addBasketDetailToMap(basket, data);

		return Results.html().render(data)
				.template("views/WebShopController/basketOverview.ftl.html");
	}

	public Result basket(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		setOverallData(data, context);

		return Results.html().render(data)
				.template("views/WebShopController/basketOverview.ftl.html");
	}

	public Result checkout(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		setOverallData(data, context);

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
			// customer is logged
			if (SessionHandling.isCustomerLogged(context)) {
				template = "views/WebShopController/deliveryAddress.ftl.html";
			}
			// guest
			else {
				template = "views/WebShopController/deliveryAddress.ftl.html";
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

		setOverallData(data, context);

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

			template = "views/WebShopController/paymentMethod.ftl.html";
		} else {
			template = "views/WebShopController/billingAddress.ftl.html";
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

		setOverallData(data, context);

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

		String template = "views/WebShopController/paymentMethod.ftl.html";

		Ebean.save(order);

		return Results.html().render(data).template(template);
	}

	public Result paymentMethodCompleted(
			@Param("creditCardNumber") int creditNumber,
			@Param("name") String name,
			@Param("expirationDateMonth") int month,
			@Param("expirationDateYear") int year, Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		setOverallData(data, context);

		CreditCard creditCard = new CreditCard();

		creditCard.setNumber(creditNumber);
		creditCard.setName(name);
		creditCard.setMonth(month);
		creditCard.setYear(year);

		Order order = OrderInformation.getOrderById(SessionHandling
				.getOrderId(context));

		order.setCreditCard(creditCard);

		String template = "views/WebShopController/checkoutOverview.ftl.html";

		Ebean.save(order);

		return Results.html().render(data).template(template);
	}

	public Result checkoutCompleted(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		// remove basket
		Basket basket = BasketInformation.getBasketById(SessionHandling
				.getBasketId(context));
		BasketInformation.removeBasket(context, basket);
		// remove basket from session
		SessionHandling.setBasketId(context, -1);
		// remove order from session
		SessionHandling.setOrderId(context, -1);
		// remove order, if a guest bought something

		setOverallData(data, context);
		
		return Results.html().render(data);
	}

	public Result registration(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		setOverallData(data, context);

		return Results.html().render(data);
	}

	public Result registrationCompleted(@Param("name") String name,
			@Param("firstName") String firstName, @Param("eMail") String email,
			@Param("password") String password,
			@Param("passwordAgain") String passwordAgain, Context context) {

		final Map<String, Object> data = new HashMap<String, Object>();
		
		// create new customer
		Customer customer = new Customer();
		customer.setName(name);
		customer.setFirstName(firstName);
		customer.setEmail(email);
		customer.setPassword(password);
		
		Ebean.save(customer);
		
		// put customer id to session
		SessionHandling.setCustomerId(context, customer.getId());
		
		setOverallData(data, context);

		return Results.html().render(data);
	}

	/**
	 * Adds all categories to the map data. Sets the session cookie, if its
	 * unknown. Adds some information of the basket of the current user. Should
	 * be called before each page view.
	 * 
	 * @param data
	 * @param context
	 */
	private void setOverallData(final Map<String, Object> data, Context context) {
		// remember categories
		HeaderInformation.getCategories(data);
		// set session cookie
		SessionHandling.setUnknownUser(context);
		// get basket by session
		Basket basket = BasketInformation.getBasketById(SessionHandling
				.getBasketId(context));
		// put basket id to session
		SessionHandling.setBasketId(context, basket.getId());
		// remember basket stuff
		BasketInformation.addBasketDetailToMap(basket, data);
		// remember logged customer
		CustomerInformation.addCustomerToMap(context, data);
	}

	// just for testing purposes
	public Result flowers() {
		final Map<String, Object> data = new HashMap<String, Object>();

		HeaderInformation.getCategories(data);
		ProductInformation.addAllProductsToMap(data);

		return Results.html().render(data);
	}
}
