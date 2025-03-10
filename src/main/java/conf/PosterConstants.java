/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package conf;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.utils.NinjaProperties;

/**
 * Collection of template paths, regular expressions, default values and so on. Some of these values will be read from
 * the file application.conf.
 * 
 * @author sebastianloob
 */
@Singleton
public class PosterConstants
{

    /**
     * The regular expression of a valid ZIP.
     */
    public final String REGEX_ZIP;

    /**
     * Different regular expressions of a valid credit card, e.g. VISA or MasterCard.
     */
    public final String[] REGEX_CREDITCARD;

    /**
     * The regular expression of a valid email address.
     */
    public final String REGEX_EMAIL;
    
    /**
     * The regular expression of a valid name.
     */
    public final String REGEX_NAME;
    
    /**
     * The regular expression of a valid product count in the cart.
     */
    public final String REGEX_PRODUCT_COUNT;

    /**
     * The path to the cart overview template.
     */
    public final String TEMPLATE_CART_OVERVIEW = "views/CartController/cartOverview.ftl.html";

    public final String TEMPLATE_PRODUCT_NOT_FOUND = "views/noResult.ftl.html";
    /**
     * The path to the product overview template.
     */
    public final String TEMPLATE_PRODUCT_OVERVIEW = "views/CatalogController/productOverview.ftl.html";

    /**
     * The path to the template to enter a shipping address.
     */
    public final String TEMPLATE_SHIPPING_ADDRESS = "views/CheckoutController/shippingAddress.ftl.html";

    /**
     * The path to the template to enter a shipping address as guest.
     */
    public final String TEMPLATE_SHIPPING_ADDRESS_GUEST = "views/CheckoutController/shippingAddressGuest.ftl.html";

    /**
     * The path to the template to enter a billing address.
     */
    public final String TEMPLATE_BILLING_ADDRESS = "views/CheckoutController/billingAddress.ftl.html";

    /**
     * The path to the template to enter a billing address as guest.
     */
    public final String TEMPLATE_BILLING_ADDRESS_GUEST = "views/CheckoutController/billingAddressGuest.ftl.html";

    /**
     * The path to the template to enter a payment method.
     */
    public final String TEMPLATE_PAYMENT_METHOD = "views/CheckoutController/paymentMethod.ftl.html";

    /**
     * The path to the template to enter a payment method as guest.
     */
    public final String TEMPLATE_PAYMENT_METHOD_GUEST = "views/CheckoutController/paymentMethodGuest.ftl.html";
    
    /**
     * The path to the template to the order confirmation page.
     */
    public final String TEMPLATE_ORDER_CONFIRMATION_PAGE = "views/CheckoutController/orderConfirmation.ftl.html";
        
    /**
     * The path to the registration template.
     */
    public final String TEMPLATE_REGISTRATION = "views/CustomerController/registration.ftl.html";

    /**
     * The path to the template to add a customer's payment method.
     */
    public final String TEMPLATE_ADD_PAYMENT_TO_CUSTOMER = "views/CustomerController/addPaymentToCustomer.ftl.html";

    /**
     * The path to the template to add a customer's shipping address.
     */
    public final String TEMPLATE_ADD_SHIPPING_ADDRESS_TO_CUSTOMER = "views/CustomerController/addShippingAddressToCustomer.ftl.html";

    /**
     * The path to the template to add a customer's billing address.
     */
    public final String TEMPLATE_ADD_BILLING_ADDRESS_TO_CUSTOMER = "views/CustomerController/addBillingAddressToCustomer.ftl.html";

    /**
     * The path to the template to update a customer's shipping address.
     */
    public final String TEMPLATE_UPDATE_SHIPPING_ADDRESS = "views/CustomerController/updateShippingAddress.ftl.html";

    /**
     * The path to the template to update a customer's billing address.
     */
    public final String TEMPLATE_UPDATE_BILLING_ADDRESS = "views/CustomerController/updateBillingAddress.ftl.html";

    /**
     * The path to the login-form template.
     */
    public final String TEMPLATE_LOGIN_FORM = "views/CustomerController/loginForm.ftl.html";

    /** 
     * The path to the product overview template.
     */
    public final String TEMPLATE_NOT_FOUND = "views/CatalogController/noResult.ftl.html";

    /** 
     * The path to the Error 404 Not Found template.
     */
    public final String NOT_FOUND_404 = "views/system/404notFound.ftl.html";

    /**
     * The URL of the application, if HTTP is used.
     */
    public final String APPLICATION_URL_HTTP;

    /**
     * The URL of the application, if HTTPS is used.
     */
    public final String APPLICATION_URL_HTTPS;

    /**
     * The version of the application
     */
    public final String APPLICATION_VERSION;

    /**
     * The currency, which is used in the shop.
     */
    public final String CURRENCY;

    /**
     * The shipping costs of a order.
     */
    public final double SHIPPING_COSTS;

    /**
     * The tax, which will be added to the sub-total price.
     */
    public final double TAX;

    /**
     * Defines, how much products should be shown on a product overview page.
     */
    public final int PRODUCTS_PER_PAGE;

    /**
     * The unit of length, which is used in the shop (e.g. in, cm).
     */
    public final String UNIT_OF_LENGTH;

    /*
     * The languages supported by the appliction listed by ISO code seperated by comma
     */
    public final String APPLICATION_LANGUAGES;

    @Inject
    public PosterConstants(final NinjaProperties ninjaProp)
    {
        REGEX_ZIP = ninjaProp.getOrDie("regex.zip");

        REGEX_CREDITCARD = ninjaProp.getStringArray("regex.creditCard");

        REGEX_EMAIL = ninjaProp.getOrDie("regex.email");
        
        REGEX_NAME = ninjaProp.getOrDie("regex.name");

        REGEX_PRODUCT_COUNT = ninjaProp.getOrDie("regex.productCount");

        APPLICATION_URL_HTTP = ninjaProp.getOrDie("application.url.http");

        APPLICATION_URL_HTTPS = ninjaProp.getOrDie("application.url.https");

        CURRENCY = ninjaProp.getOrDie("application.currency");

        SHIPPING_COSTS = Double.parseDouble(ninjaProp.getOrDie("application.shippingCosts"));

        TAX = Double.parseDouble(ninjaProp.getOrDie("application.tax"));

        PRODUCTS_PER_PAGE = ninjaProp.getInteger("application.pageSize");

        UNIT_OF_LENGTH = ninjaProp.getOrDie("application.unitLength");

        APPLICATION_VERSION = ninjaProp.getOrDie("application.version");

        APPLICATION_LANGUAGES = ninjaProp.getOrDie("application.languages");
    }
}
