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
import controllers_backoffice.BackofficeController;
import controllers_backoffice.UserController;

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
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
    
        // ############################################################
        // ############################################################

        // back office

        // ############################################################
        // ############################################################

        // ############################################################
        // user login
        // ############################################################
        router.GET().route("/posters/backoffice/login").with(UserController.class, "loginForm");
        router.POST().route("/posters/backoffice/login").with(UserController.class, "login");
        router.GET().route("/posters/backoffice/logout").with(UserController.class, "logout");

        // ############################################################
        // homepage
        // ############################################################
        router.GET().route("/posters/backoffice").with(BackofficeController.class, "homepage");

        // ############################################################
        // user registration
        // ############################################################
        router.GET().route("/posters/backoffice/user/create").with(UserController.class, "registration");
        router.POST().route("/posters/backoffice/user/create").with(UserController.class, "registrationCompleted");

        // ############################################################
        // admin users
        // ############################################################
        router.GET().route("/posters/backoffice/user").with(BackofficeController.class, "userList");
        router.GET().route("/posters/backoffice/user/{userId}/edit").with(BackofficeController.class, "userEdit");
        router.GET().route("/posters/backoffice/user/{userId}").with(BackofficeController.class, "userView");
        router.POST().route("/posters/backoffice/user/{userId}/delete").with(BackofficeController.class, "userDelete");
        router.POST().route("/posters/backoffice/user/{userId}").with(BackofficeController.class, "userEditComplete");

        // ############################################################
        // backoffice statistics
        // ############################################################
        router.GET().route("/posters/backoffice/statistics").with(BackofficeController.class, "statistics");

        // ############################################################
        // orders
        // ############################################################
        router.GET().route("/posters/backoffice/order").with(BackofficeController.class, "orderList");
        router.GET().route("/posters/backoffice/order/{orderId}").with(BackofficeController.class, "orderView");

        // ############################################################
        // customers
        // ############################################################
        router.GET().route("/posters/backoffice/customer").with(BackofficeController.class, "customerList");
        router.GET().route("/posters/backoffice/customer/{customerId}").with(BackofficeController.class, "customerView");
        router.GET().route("/posters/backoffice/customer/{customerId}/edit").with(BackofficeController.class, "customerEdit");
        router.POST().route("/posters/backoffice/customer/{customerId}").with(BackofficeController.class, "customerEditComplete");

        // ############################################################
        // Catalog
        // ############################################################
        router.GET().route("/posters/backoffice/catalog").with(BackofficeController.class, "catalog");

        // ############################################################
        // Products
        // ############################################################
        router.GET().route("/posters/backoffice/product").with(BackofficeController.class, "productList");
        router.GET().route("/posters/backoffice/product/{productId}").with(BackofficeController.class, "productView");
        router.GET().route("/posters/backoffice/product/{productId}/edit").with(BackofficeController.class, "productEdit");
        router.POST().route("/posters/backoffice/product/{productId}").with(BackofficeController.class, "productEditComplete");

        // ############################################################
        // Preferences
        // ############################################################
        router.GET().route("/posters/backoffice/preferences").with(BackofficeController.class, "preferences");

        // ############################################################
        // Import/Export
        // ############################################################
        router.GET().route("/posters/backoffice/data-management").with(BackofficeController.class, "dataManagement");

        // ############################################################
        // Page Not Found
        // ############################################################
        router.GET().route("/posters/backoffice/.*").with(BackofficeController.class, "Error404");
    }
}
