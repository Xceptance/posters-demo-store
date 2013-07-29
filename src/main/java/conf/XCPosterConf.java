package conf;

import ninja.utils.NinjaProperties;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class XCPosterConf
{

    public final String regexZip;

    public final String[] regexCreditCard;

    public final String regexEmail;
    
    public final String regexProductCount;

    public final String templateIndex;

    public final String templateBasketOverview;

    public final String templateProductOverview;

    public final String templateCheckoutOverview;

    public final String templateDeliveryAddress;

    public final String templateBillingAddress;

    public final String templatePaymentMethod;

    public final String templateRegistration;

    public final String templateAddPaymentToCustomer;

    public final String templateAddDeliveryAddressToCustomer;

    public final String templateAddBillingAddressToCustomer;

    public final String templateAccountOverview;

    public final String templateUpdateDeliveryAddress;

    public final String templateUpdateBillingAddress;

    public final String templateChangeNameOrEmail;

    public final String templateChangePassword;

    public final String templateConfirmDeletion;

    @Inject
    public XCPosterConf(NinjaProperties ninjaProp)
    {
        regexZip = ninjaProp.getOrDie("regex.zip");

        regexCreditCard = ninjaProp.getStringArray("regex.creditCard");

        regexEmail = ninjaProp.getOrDie("regex.email");
        
        regexProductCount = ninjaProp.getOrDie("regex.productCount");

        templateIndex = "views/WebShopController/index.ftl.html";

        templateBasketOverview = "views/BasketController/basketOverview.ftl.html";

        templateProductOverview = "views/CatalogController/productOverview.ftl.html";

        templateCheckoutOverview = "views/CheckoutController/checkoutOverview.ftl.html";

        templateDeliveryAddress = "views/CheckoutController/deliveryAddress.ftl.html";

        templateBillingAddress = "views/CheckoutController/billingAddress.ftl.html";

        templatePaymentMethod = "views/CheckoutController/paymentMethod.ftl.html";

        templateRegistration = "views/CustomerController/registration.ftl.html";

        templateAddPaymentToCustomer = "views/CustomerController/addPaymentToCustomer.ftl.html";

        templateAddDeliveryAddressToCustomer = "views/CustomerController/addDeliveryAddressToCustomer.ftl.html";

        templateAddBillingAddressToCustomer = "views/CustomerController/addBillingAddressToCustomer.ftl.html";

        templateAccountOverview = "views/CustomerController/accountOverview.ftl.html";

        templateUpdateDeliveryAddress = "views/CustomerController/updateDeliveryAddress.ftl.html";

        templateUpdateBillingAddress = "views/CustomerController/updateBillingAddress.ftl.html";

        templateChangeNameOrEmail = "views/CustomerController/changeNameOrEmail.ftl.html";

        templateChangePassword = "views/CustomerController/changePassword.ftl.html";

        templateConfirmDeletion = "views/CustomerController/confirmDeletion.ftl.html";
    }
}
