package conf;

import ninja.utils.NinjaProperties;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PosterConstants
{

    public final String REGEX_ZIP;

    public final String[] REGEX_CREDITCARD;

    public final String REGEX_EMAIL;

    public final String REGEX_PRODUCT_COUNT;

    public final String TEMPLATE_CART_OVERVIEW = "views/BasketController/basketOverview.ftl.html";

    public final String TEMPLATE_PRODUCT_OVERVIEW = "views/CatalogController/productOverview.ftl.html";

    public final String TEMPLATE_SHIPPING_ADDRESS = "views/CheckoutController/deliveryAddress.ftl.html";

    public final String TEMPLATE_BILLING_ADDRESS = "views/CheckoutController/billingAddress.ftl.html";

    public final String TEMPLATE_PAYMENT_METHOD = "views/CheckoutController/paymentMethod.ftl.html";

    public final String TEMPLATE_REGISTRATION = "views/CustomerController/registration.ftl.html";

    public final String TEMPLATE_ADD_PAYMENT_TO_CUSTOMER = "views/CustomerController/addPaymentToCustomer.ftl.html";

    public final String TEMPLATE_ADD_SHIPPING_ADDRESS_TO_CUSTOMER = "views/CustomerController/addDeliveryAddressToCustomer.ftl.html";

    public final String TEMPLATE_ADD_BILLING_ADDRESS_TO_CUSTOMER = "views/CustomerController/addBillingAddressToCustomer.ftl.html";

    public final String TEMPLATE_UPDATE_SHIPPING_ADDRESS = "views/CustomerController/updateDeliveryAddress.ftl.html";

    public final String TEMPLATE_UPDATE_BILLING_ADDRESS = "views/CustomerController/updateBillingAddress.ftl.html";

    public final String TEMPLATE_CONFIRM_DELETING_ADDRESS = "views/CustomerController/confirmDeleteAddress.ftl.html";

    public final String TEMPLATE_CONFIRM_DELETING_PAYMENT = "views/CustomerController/confirmDeletePayment.ftl.html";

    public final String TEMPLATE_LOGIN_FORM = "views/CustomerController/loginForm.ftl.html";

    public final String APPLICATION_URL_HTTP;

    public final String APPLICATION_URL_HTTPS;

    public final String CURRENCY;

    public final double SHIPPING_COSTS;

    public final double TAX;

    public final int PRODUCTS_PER_PAGE;

    public final String UNIT_OF_LENGTH;

    @Inject
    public PosterConstants(NinjaProperties ninjaProp)
    {
        REGEX_ZIP = ninjaProp.getOrDie("regex.zip");

        REGEX_CREDITCARD = ninjaProp.getStringArray("regex.creditCard");

        REGEX_EMAIL = ninjaProp.getOrDie("regex.email");

        REGEX_PRODUCT_COUNT = ninjaProp.getOrDie("regex.productCount");

        APPLICATION_URL_HTTP = ninjaProp.getOrDie("application.url.http");

        APPLICATION_URL_HTTPS = ninjaProp.getOrDie("application.url.https");

        CURRENCY = ninjaProp.getOrDie("application.currency");

        SHIPPING_COSTS = Double.parseDouble(ninjaProp.getOrDie("application.shippingCosts"));

        TAX = Double.parseDouble(ninjaProp.getOrDie("application.tax"));

        PRODUCTS_PER_PAGE = ninjaProp.getInteger("application.pageSize");

        UNIT_OF_LENGTH = ninjaProp.getOrDie("application.unitLength");
    }
}
