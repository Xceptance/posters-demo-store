package conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.BasketController;
import controllers.CatalogController;
import controllers.CheckoutController;
import controllers.CustomerController;
import controllers.SearchController;
import controllers.WebShopController;

public class Routes implements ApplicationRoutes
{

    @Override
    public void init(Router router)
    {
        // home page
        router.GET().route("/").with(WebShopController.class, "index");

        // search
        router.POST().route("/search").with(SearchController.class, "search");

        // Customer
        router.GET().route("/login").with(CustomerController.class, "loginForm");
        router.POST().route("/login").with(CustomerController.class, "login");
        router.GET().route("/logout").with(CustomerController.class, "logout");
        router.GET().route("/registration").with(CustomerController.class, "registration");
        router.POST().route("/registration").with(CustomerController.class, "registrationCompleted");
        router.GET().route("/accountOverview").with(CustomerController.class, "accountOverview");
        router.GET().route("/orderOverview").with(CustomerController.class, "orderOverview");
        router.GET().route("/paymentOverview").with(CustomerController.class, "paymentOverview");
        router.GET().route("/settingOverview").with(CustomerController.class, "settingOverview");
        router.GET().route("/addPaymentToCustomer").with(CustomerController.class, "addPaymentToCustomer");
        router.POST().route("/addPaymentToCustomer")
              .with(CustomerController.class, "addPaymentToCustomerCompleted");
        router.POST().route("/deletePayment").with(CustomerController.class, "deletePayment");
        router.POST().route("/confirmDeletePayment").with(CustomerController.class, "confirmDeletePayment");
        router.GET().route("/addressOverview").with(CustomerController.class, "addressOverview");
        router.POST().route("/updateDeliveryAddress").with(CustomerController.class, "updateDeliveryAddress");
        router.POST().route("/updateDeliveryAddressCompleted")
              .with(CustomerController.class, "updateDeliveryAddressCompleted");
        router.POST().route("/updateBillingAddress").with(CustomerController.class, "updateBillingAddress");
        router.POST().route("/updateBillingAddressCompleted")
              .with(CustomerController.class, "updateBillingAddressCompleted");
        router.POST().route("/deleteBillingAddress").with(CustomerController.class, "deleteBillingAddress");
        router.POST().route("/confirmDeleteBillingAddress").with(CustomerController.class, "confirmDeleteBillingAddress");
        router.POST().route("/deleteDeliveryAddress").with(CustomerController.class, "deleteDeliveryAddress");
        router.POST().route("/confirmDeleteDeliveryAddress").with(CustomerController.class, "confirmDeleteDeliveryAddress");
        router.GET().route("/addDeliveryAddressToCustomer")
              .with(CustomerController.class, "addDeliveryAddressToCustomer");
        router.POST().route("/addDeliveryAddressToCustomerCompleted")
              .with(CustomerController.class, "addDeliveryAddressToCustomerCompleted");
        router.GET().route("/addBillingAddressToCustomer")
              .with(CustomerController.class, "addBillingAddressToCustomer");
        router.POST().route("/addBillingAddressToCustomerCompleted")
              .with(CustomerController.class, "addBillingAddressToCustomerCompleted");
        router.POST().route("/changeNameEmail").with(CustomerController.class, "changeNameOrEmail");
        router.POST().route("/changeNameEmailCompleted").with(CustomerController.class, "changeNameOrEmailCompleted");
        router.POST().route("/changePassword").with(CustomerController.class, "changePassword");
        router.POST().route("/changePasswordCompleted").with(CustomerController.class, "changePasswordCompleted");
        router.POST().route("/deleteAccount").with(CustomerController.class, "deleteAccount");
        router.POST().route("/confirmDeleteAccount").with(CustomerController.class, "confirmDeleteAccount");

        // products and catalog
        router.GET().route("/productDetail/{product}").with(CatalogController.class, "productDetail");
        router.GET().route("/topCategory/{topCategory}").with(CatalogController.class, "topCategoryOverview");
        router.GET().route("/category/{subCategory}").with(CatalogController.class, "productOverview");

        // basket
        router.POST().route("/addToBasket").with(BasketController.class, "addToBasket");
        router.POST().route("/deleteFromBasket").with(BasketController.class, "deleteFromBasket");
        router.GET().route("/basket").with(BasketController.class, "basket");
        router.POST().route("/updateProductCount").with(BasketController.class, "updateProductCount");

        // checkout
        router.POST().route("/checkout").with(CheckoutController.class, "checkout");
        router.POST().route("/deliveryAddressCompleted").with(CheckoutController.class, "deliveryAddressCompleted");
        router.POST().route("/billingAddressCompleted").with(CheckoutController.class, "billingAddressCompleted");
        router.POST().route("/addDeliveryAddressToOrder").with(CheckoutController.class, "addDeliveryAddressToOrder");
        router.POST().route("/addBillingAddressToOrder").with(CheckoutController.class, "addBillingAddressToOrder");
        router.POST().route("/addPaymentToOrder").with(CheckoutController.class, "addPaymentToOrder");
        router.POST().route("/paymentMethodCompleted").with(CheckoutController.class, "paymentMethodCompleted");
        router.POST().route("/checkoutCompleted").with(CheckoutController.class, "checkoutCompleted");

        router.GET().route("/assets/.*").with(AssetsController.class, "serve");
    }
}
