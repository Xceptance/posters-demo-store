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
        router.GET().route("/").with(WebShopController::index);

        // ############################################################
        // search
        // ############################################################
        router.GET().route("/search").with(SearchController::searchProduct);
        router.POST().route("/getProductOfSearch").with(SearchController::getProductOfSearch);

        // ############################################################
        // customer registration
        // ############################################################
        router.GET().route("/login").with(CustomerController::loginForm);
        router.POST().route("/login").with(CustomerController::login);
        router.GET().route("/logout").with(CustomerController::logout);
        router.GET().route("/registration").with(CustomerController::registration);
        router.POST().route("/registration").with(CustomerController::registrationCompleted);

        // ############################################################
        // customer backend
        // ############################################################
        router.GET().route("/accountOverview").with(CustomerController::accountOverview);
        router.GET().route("/paymentOverview").with(CustomerController::paymentOverview);
        router.GET().route("/addressOverview").with(CustomerController::addressOverview);
        router.GET().route("/settingOverview").with(CustomerController::settingOverview);
        router.GET().route("/orderOverview").with(CustomerController::orderOverview);

        // ############################################################
        // customer's payment
        // ############################################################
        router.GET().route("/addPaymentToCustomer").with(CustomerController::addPaymentToCustomer);
        router.POST().route("/addPaymentToCustomer").with(CustomerController::addPaymentToCustomerCompleted);
        router.POST().route("/deletePayment").with(CustomerController::deletePayment);
        router.POST().route("/confirmDeletePayment").with(CustomerController::confirmDeletePayment);
        router.POST().route("/updatePaymentMethod").with(CustomerController::updatePaymentMethod);
        router.POST().route("/updatePaymentMethodCompleted").with(CustomerController::updatePaymentMethodCompleted);


        // ############################################################
        // customer's addresses
        // ############################################################
        router.POST().route("/updateShippingAddress").with(CustomerController::updateShippingAddress);
        router.POST().route("/updateShippingAddressCompleted").with(CustomerController::updateShippingAddressCompleted);
        router.POST().route("/updateBillingAddress").with(CustomerController::updateBillingAddress);
        router.POST().route("/updateBillingAddressCompleted").with(CustomerController::updateBillingAddressCompleted);
        router.POST().route("/deleteBillingAddress").with(CustomerController::deleteBillingAddress);
        router.POST().route("/confirmDeleteBillingAddress").with(CustomerController::confirmDeleteBillingAddress);
        router.POST().route("/deleteShippingAddress").with(CustomerController::deleteShippingAddress);
        router.POST().route("/confirmDeleteShippingAddress").with(CustomerController::confirmDeleteShippingAddress);
        router.GET().route("/addShippingAddressToCustomer").with(CustomerController::addShippingAddressToCustomer);
        router.POST().route("/addShippingAddressToCustomerCompleted")
              .with(CustomerController::addShippingAddressToCustomerCompleted);
        router.GET().route("/addBillingAddressToCustomer").with(CustomerController::addBillingAddressToCustomer);
        router.POST().route("/addBillingAddressToCustomerCompleted").with(CustomerController::addBillingAddressToCustomerCompleted);

        // ############################################################
        // customer's personal data
        // ############################################################
        router.GET().route("/changeNameOrEmail").with(CustomerController::changeNameOrEmail);
        router.POST().route("/changeNameOrEmailCompleted").with(CustomerController::changeNameOrEmailCompleted);
        router.GET().route("/changePassword").with(CustomerController::changePassword);
        router.POST().route("/changePasswordCompleted").with(CustomerController::changePasswordCompleted);
        router.GET().route("/confirmDeleteAccount").with(CustomerController::confirmDeleteAccount);
        router.POST().route("/deleteAccount").with(CustomerController::deleteAccount);

        // ############################################################
        // catalog and products
        // ############################################################
        router.GET().route("/productDetail/{product}").with(CatalogController::productDetail);
        router.GET().route("/topCategory/{topCategory}").with(CatalogController::topCategoryOverview);
        router.GET().route("/category/{subCategory}").with(CatalogController::productOverview);
        router.POST().route("/getProductOfTopCategory").with(CatalogController::getProductOfTopCategory);
        router.POST().route("/getProductOfSubCategory").with(CatalogController::getProductOfSubCategory);

        // ############################################################
        // cart
        // ############################################################
        router.GET().route("/cart").with(CartController::cart);
        router.GET().route("/deleteFromCart").with(CartController::deleteFromCart);
        router.GET().route("/updateProductCount").with(CartController::updateProductCount);
        router.POST().route("/updatePrice").with(CartController::updatePrice);
        router.GET().route("/addToCartSlider").with(CartController::addToCart);
        router.GET().route("/getMiniCartElements").with(CartController::getMiniCartElements);

        // ############################################################
        // checkout
        // ############################################################
        router.GET().route("/checkout").with(CheckoutController::checkout);
        router.GET().route("/enterShippingAddress").with(CheckoutController::enterShippingAddress);
        router.POST().route("/shippingAddressCompleted").with(CheckoutController::shippingAddressCompleted);
        router.GET().route("/enterBillingAddress").with(CheckoutController::enterBillingAddress);
        router.POST().route("/billingAddressCompleted").with(CheckoutController::billingAddressCompleted);
        router.POST().route("/addShippingAddressToOrder").with(CheckoutController::addShippingAddressToOrder);
        router.POST().route("/addBillingAddressToOrder").with(CheckoutController::addBillingAddressToOrder);
        router.GET().route("/enterPaymentMethod").with(CheckoutController::enterPaymentMethod);
        router.POST().route("/addPaymentToOrder").with(CheckoutController::addPaymentToOrder);
        router.POST().route("/paymentMethodCompleted").with(CheckoutController::paymentMethodCompleted);
        router.GET().route("/checkoutOverview").with(CheckoutController::checkoutOverview);
        router.POST().route("/checkoutCompleted").with(CheckoutController::checkoutCompleted);
        router.GET().route("/orderConfirmation").with(CheckoutController::orderConfirmation);

        // ############################################################
        // assets
        // ############################################################
        router.GET().route("/assets/{fileName: .*}").with(AssetsController::serveStatic);
    }
}
