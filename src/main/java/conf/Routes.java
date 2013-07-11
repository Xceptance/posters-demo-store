package conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.BasketController;
import controllers.CatalogController;
import controllers.CheckoutController;
import controllers.CustomerController;
import controllers.WebShopController;

public class Routes implements ApplicationRoutes
{

    @Override
    public void init(Router router)
    {
        // home page
        router.GET().route("/").with(WebShopController.class, "index");
        // Customer
        router.POST().route("/login").with(CustomerController.class, "login");
        router.GET().route("/logout").with(CustomerController.class, "logout");
        router.GET().route("/registration").with(CustomerController.class, "registration");
        router.POST().route("/registrationCompleted").with(CustomerController.class, "registrationCompleted");
        router.GET().route("/accountOverview").with(CustomerController.class, "accountOverview");
        router.GET().route("/orderOverview").with(CustomerController.class, "orderOverview");
        router.GET().route("/paymentOverview").with(CustomerController.class, "paymentOverview");
        router.GET().route("/settingOverview").with(CustomerController.class, "settingOverview");
        router.GET().route("/addPaymentToCustomer").with(CustomerController.class, "addPaymentToCustomer");
        router.POST().route("/addPaymentToCustomerCompleted")
              .with(CustomerController.class, "addPaymentToCustomerCompleted");
        router.POST().route("/deletePayment").with(CustomerController.class, "deletePayment");
        // router.GET().route("/settingOverview").with(CustomerController.class, "settingOverview");
        router.GET().route("/addressOverview").with(CustomerController.class, "addressOverview");
        router.POST().route("/deleteBillingAddress").with(CustomerController.class, "deleteBillingAddress");
        router.POST().route("/deleteDeliveryAddress").with(CustomerController.class, "deleteDeliveryAddress");
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
