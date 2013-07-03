package controllers;

import java.util.HashMap;
import java.util.Map;

import com.avaje.ebean.Ebean;

import models.CreditCard;
import models.Customer;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.database.CarouselInformation;
import util.database.CommonInformation;
import util.database.CreditCardInformation;
import util.database.CustomerInformation;
import util.session.SessionHandling;

public class CustomerController {

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

		CommonInformation.setCommonData(data, context);
		CarouselInformation.getCarouselProducts(data);
		return Results.html().render(data)
				.template("views/WebShopController/index.ftl.html");
	}

	public Result logout(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		SessionHandling.deleteCustomerId(context);

		CommonInformation.setCommonData(data, context);
		CarouselInformation.getCarouselProducts(data);
		return Results.html().render(data)
				.template("views/WebShopController/index.ftl.html");
	}
	
	public Result registration(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setCommonData(data, context);

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

		CommonInformation.setCommonData(data, context);

		return Results.html().render(data);
	}
	
	public Result accountOverview(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setCommonData(data, context);
		return Results.html().render(data);
	}

	public Result orderOverview(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CustomerInformation.addOrderByCustomerToMap(context, data);

		CommonInformation.setCommonData(data, context);
		return Results.html().render(data);
	}

	public Result paymentOverview(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CustomerInformation.addPaymentByCustomerToMap(context, data);

		CommonInformation.setCommonData(data, context);
		return Results.html().render(data);
	}
	
	public Result settingOverview(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CustomerInformation.addCustomerToMap(context, data);

		CommonInformation.setCommonData(data, context);
		return Results.html().render(data);
	}
	
	public Result addPaymentToCustomerCompleted(
			@Param("creditCardNumber") int creditNumber,
			@Param("name") String name,
			@Param("expirationDateMonth") int month,
			@Param("expirationDateYear") int year, Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		Customer customer = CustomerInformation.getCustomerById(SessionHandling
				.getCustomerId(context));

		CommonInformation.setCommonData(data, context);

		CreditCard creditCard = new CreditCard();

		creditCard.setNumber(creditNumber);
		creditCard.setName(name);
		creditCard.setMonth(month);
		creditCard.setYear(year);
		
		customer.addCreditCard(creditCard);
		
		Ebean.save(customer);
		
		return Results.html().render(data).template("views/info/savingComplete.ftl.html");
	}
	
	public Result addPaymentToCustomer(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setCommonData(data, context);
		
		return Results.html().render(data);
	}
	
	public Result deletePayment(@Param("cardId") int cardId, Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		Customer customer = CustomerInformation.getCustomerById(SessionHandling
				.getCustomerId(context));
		customer.deleteCreditCard(CreditCardInformation.getCreditCardById(cardId));
		System.out.println("card id: " + cardId);
		System.out.println("card name " + CreditCardInformation.getCreditCardById(cardId).getName());
		Ebean.save(customer);
		
		CommonInformation.setCommonData(data, context);
		
		return Results.html().render(data).template("views/info/savingComplete.ftl.html");
	}
}
