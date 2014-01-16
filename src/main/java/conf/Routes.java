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

/**
 * This class provides all routes of the application.
 * 
 * @author sebastianloob
 */
public class Routes implements ApplicationRoutes
{

    @Override
    public void init(Router router)
    {
        // ############################################################
        // home page
        // ############################################################
        router.GET().route("/").with(WebShopController.class, "index");

        // ############################################################
        // search
        // ############################################################
        router.GET().route("/search").with(SearchController.class, "searchProduct");
        router.POST().route("/getProductOfSearch").with(SearchController.class, "getProductOfSearch");

        // ############################################################
        // customer registration
        // ############################################################
        router.GET().route("/login").with(CustomerController.class, "loginForm");
        router.POST().route("/login").with(CustomerController.class, "login");
        router.GET().route("/logout").with(CustomerController.class, "logout");
        router.GET().route("/registration").with(CustomerController.class, "registration");
        router.POST().route("/registration").with(CustomerController.class, "registrationCompleted");

        // ############################################################
        // customer backend
        // ############################################################
        router.GET().route("/accountOverview").with(CustomerController.class, "accountOverview");
        router.GET().route("/paymentOverview").with(CustomerController.class, "paymentOverview");
        router.GET().route("/addressOverview").with(CustomerController.class, "addressOverview");
        router.GET().route("/settingOverview").with(CustomerController.class, "settingOverview");
        router.GET().route("/orderOverview").with(CustomerController.class, "orderOverview");

        // ############################################################
        // customer's payment
        // ############################################################
        router.GET().route("/addPaymentToCustomer").with(CustomerController.class, "addPaymentToCustomer");
        router.POST().route("/addPaymentToCustomer").with(CustomerController.class, "addPaymentToCustomerCompleted");
        router.POST().route("/deletePayment").with(CustomerController.class, "deletePayment");
        router.POST().route("/confirmDeletePayment").with(CustomerController.class, "confirmDeletePayment");

        // ############################################################
        // customer's addresses
        // ############################################################
        router.POST().route("/updateDeliveryAddress").with(CustomerController.class, "updateDeliveryAddress");
        router.POST().route("/updateDeliveryAddressCompleted")
              .with(CustomerController.class, "updateDeliveryAddressCompleted");
        router.POST().route("/updateBillingAddress").with(CustomerController.class, "updateBillingAddress");
        router.POST().route("/updateBillingAddressCompleted")
              .with(CustomerController.class, "updateBillingAddressCompleted");
        router.POST().route("/deleteBillingAddress").with(CustomerController.class, "deleteBillingAddress");
        router.POST().route("/confirmDeleteBillingAddress")
              .with(CustomerController.class, "confirmDeleteBillingAddress");
        router.POST().route("/deleteDeliveryAddress").with(CustomerController.class, "deleteDeliveryAddress");
        router.POST().route("/confirmDeleteDeliveryAddress")
              .with(CustomerController.class, "confirmDeleteDeliveryAddress");
        router.GET().route("/addDeliveryAddressToCustomer")
              .with(CustomerController.class, "addDeliveryAddressToCustomer");
        router.POST().route("/addDeliveryAddressToCustomerCompleted")
              .with(CustomerController.class, "addDeliveryAddressToCustomerCompleted");
        router.GET().route("/addBillingAddressToCustomer")
              .with(CustomerController.class, "addBillingAddressToCustomer");
        router.POST().route("/addBillingAddressToCustomerCompleted")
              .with(CustomerController.class, "addBillingAddressToCustomerCompleted");

        // ############################################################
        // customer's personal data
        // ############################################################
        router.GET().route("/changeNameOrEmail").with(CustomerController.class, "changeNameOrEmail");
        router.POST().route("/changeNameOrEmailCompleted").with(CustomerController.class, "changeNameOrEmailCompleted");
        router.GET().route("/changePassword").with(CustomerController.class, "changePassword");
        router.POST().route("/changePasswordCompleted").with(CustomerController.class, "changePasswordCompleted");
        router.GET().route("/confirmDeleteAccount").with(CustomerController.class, "confirmDeleteAccount");
        router.POST().route("/deleteAccount").with(CustomerController.class, "deleteAccount");

        // ############################################################
        // catalog and products
        // ############################################################
        router.GET().route("/productDetail/{product}").with(CatalogController.class, "productDetail");
        router.GET().route("/topCategory/{topCategory}").with(CatalogController.class, "topCategoryOverview");
        router.GET().route("/category/{subCategory}").with(CatalogController.class, "productOverview");
        router.POST().route("/getProductOfTopCategory").with(CatalogController.class, "getProductOfTopCategory");
        router.POST().route("/getProductOfSubCategory").with(CatalogController.class, "getProductOfSubCategory");

        // ############################################################
        // cart
        // ############################################################
        router.GET().route("/addToCartSlider").with(BasketController.class, "addToCart");
        router.GET().route("/basket").with(BasketController.class, "basket");
        router.GET().route("/getCartElementSlider").with(BasketController.class, "getCartElementSlider");
        router.GET().route("/deleteFromCart").with(BasketController.class, "deleteFromCart");
        router.GET().route("/updateProductCount").with(BasketController.class, "updateProductCount");
        router.POST().route("/updatePrice").with(BasketController.class, "updatePrice");

        // ############################################################
        // checkout
        // ############################################################
        router.GET().route("/checkout").with(CheckoutController.class, "checkout");
        router.GET().route("/enterShippingAddress").with(CheckoutController.class, "enterShippingAddress");
        router.POST().route("/deliveryAddressCompleted").with(CheckoutController.class, "deliveryAddressCompleted");
        router.GET().route("/enterBillingAddress").with(CheckoutController.class, "enterBillingAddress");
        router.POST().route("/billingAddressCompleted").with(CheckoutController.class, "billingAddressCompleted");
        router.POST().route("/addDeliveryAddressToOrder").with(CheckoutController.class, "addDeliveryAddressToOrder");
        router.POST().route("/addBillingAddressToOrder").with(CheckoutController.class, "addBillingAddressToOrder");
        router.GET().route("/enterPaymentMethod").with(CheckoutController.class, "enterPaymentMethod");
        router.POST().route("/addPaymentToOrder").with(CheckoutController.class, "addPaymentToOrder");
        router.POST().route("/paymentMethodCompleted").with(CheckoutController.class, "paymentMethodCompleted");
        router.GET().route("/checkoutOverview").with(CheckoutController.class, "checkoutOverview");
        router.POST().route("/checkoutCompleted").with(CheckoutController.class, "checkoutCompleted");

        // ############################################################
        // assets
        // ############################################################
        router.GET().route("/assets/.*").with(AssetsController.class, "serve");
    }
}
