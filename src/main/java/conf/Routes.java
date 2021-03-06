package conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.CartController;
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
    public void init(final Router router)
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
        router.POST().route("/updatePaymentMethod").with(CustomerController.class, "updatePaymentMethod");
        router.POST().route("/updatePaymentMethodCompleted").with(CustomerController.class, "updatePaymentMethodCompleted");


        // ############################################################
        // customer's addresses
        // ############################################################
        router.POST().route("/updateShippingAddress").with(CustomerController.class, "updateShippingAddress");
        router.POST().route("/updateShippingAddressCompleted").with(CustomerController.class, "updateShippingAddressCompleted");
        router.POST().route("/updateBillingAddress").with(CustomerController.class, "updateBillingAddress");
        router.POST().route("/updateBillingAddressCompleted").with(CustomerController.class, "updateBillingAddressCompleted");
        router.POST().route("/deleteBillingAddress").with(CustomerController.class, "deleteBillingAddress");
        router.POST().route("/confirmDeleteBillingAddress").with(CustomerController.class, "confirmDeleteBillingAddress");
        router.POST().route("/deleteShippingAddress").with(CustomerController.class, "deleteShippingAddress");
        router.POST().route("/confirmDeleteShippingAddress").with(CustomerController.class, "confirmDeleteShippingAddress");
        router.GET().route("/addShippingAddressToCustomer").with(CustomerController.class, "addShippingAddressToCustomer");
        router.POST().route("/addShippingAddressToCustomerCompleted")
              .with(CustomerController.class, "addShippingAddressToCustomerCompleted");
        router.GET().route("/addBillingAddressToCustomer").with(CustomerController.class, "addBillingAddressToCustomer");
        router.POST().route("/addBillingAddressToCustomerCompleted").with(CustomerController.class, "addBillingAddressToCustomerCompleted");

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
        router.GET().route("/cart").with(CartController.class, "cart");
        router.GET().route("/deleteFromCart").with(CartController.class, "deleteFromCart");
        router.GET().route("/updateProductCount").with(CartController.class, "updateProductCount");
        router.POST().route("/updatePrice").with(CartController.class, "updatePrice");
        router.GET().route("/addToCartSlider").with(CartController.class, "addToCart");
        router.GET().route("/getMiniCartElements").with(CartController.class, "getMiniCartElements");

        // ############################################################
        // checkout
        // ############################################################
        router.GET().route("/checkout").with(CheckoutController.class, "checkout");
        router.GET().route("/enterShippingAddress").with(CheckoutController.class, "enterShippingAddress");
        router.POST().route("/shippingAddressCompleted").with(CheckoutController.class, "shippingAddressCompleted");
        router.GET().route("/enterBillingAddress").with(CheckoutController.class, "enterBillingAddress");
        router.POST().route("/billingAddressCompleted").with(CheckoutController.class, "billingAddressCompleted");
        router.POST().route("/addShippingAddressToOrder").with(CheckoutController.class, "addShippingAddressToOrder");
        router.POST().route("/addBillingAddressToOrder").with(CheckoutController.class, "addBillingAddressToOrder");
        router.GET().route("/enterPaymentMethod").with(CheckoutController.class, "enterPaymentMethod");
        router.POST().route("/addPaymentToOrder").with(CheckoutController.class, "addPaymentToOrder");
        router.POST().route("/paymentMethodCompleted").with(CheckoutController.class, "paymentMethodCompleted");
        router.GET().route("/checkoutOverview").with(CheckoutController.class, "checkoutOverview");
        //router.POST().route("/orderConfirmation").with(CheckoutController.class, "orderConfirmation");
        router.POST().route("/checkoutCompleted").with(CheckoutController.class, "checkoutCompleted");
        // ############################################################
        // assets
        // ############################################################
        router.GET().route("/assets/.*").with(AssetsController.class, "serve");
    }
}
