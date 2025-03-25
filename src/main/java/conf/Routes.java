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

import ninja.AssetsController;
import ninja.Results;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.BreakController;
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
        // assets
        // ############################################################
        router.GET().route("/assets/{fileName: .*}").with(AssetsController::serveStatic);

        // ############################################################
        // site icon
        // ############################################################

        router.GET().route("/favicon.ico").with(() -> Results.redirect("/assets/ico/favicon.ico"));

        // ############################################################
        // ############################################################

        // back office

        // ############################################################
        // ############################################################

        // ############################################################
        // user login
        // ############################################################
        router.GET().route("/posters/backoffice/login").with(UserController::loginForm);
        router.POST().route("/posters/backoffice/login").with(UserController::login);
        router.GET().route("/posters/backoffice/logout").with(UserController::logout);

        // ############################################################
        // homepage
        // ############################################################
        router.GET().route("/posters/backoffice").with(BackofficeController::homepage);

        // ############################################################
        // user registration
        // ############################################################
        router.GET().route("/posters/backoffice/user/create").with(UserController::registration);
        router.POST().route("/posters/backoffice/user/create").with(UserController::registrationCompleted);

        // ############################################################
        // admin users
        // ############################################################
        router.GET().route("/posters/backoffice/user").with(BackofficeController::userList);
        router.GET().route("/posters/backoffice/user/{userId}/edit").with(BackofficeController::userEdit);
        router.GET().route("/posters/backoffice/user/{userId}").with(BackofficeController::userView);
        router.POST().route("/posters/backoffice/user/{userId}/delete").with(BackofficeController::userDelete);
        router.POST().route("/posters/backoffice/user/{userId}").with(BackofficeController::userEditComplete);

        // ############################################################
        // backoffice statistics
        // ############################################################
        router.GET().route("/posters/backoffice/statistics").with(BackofficeController::statistics);

        // ############################################################
        // orders
        // ############################################################
        router.GET().route("/posters/backoffice/order").with(BackofficeController::orderList);
        router.GET().route("/posters/backoffice/order/{orderId}").with(BackofficeController::orderView);

        // ############################################################
        // customers
        // ############################################################
        router.GET().route("/posters/backoffice/customer").with(BackofficeController::customerList);
        router.GET().route("/posters/backoffice/customer/{customerId}/view").with(BackofficeController::customerView);
        router.GET().route("/posters/backoffice/customer/{customerId}/view/orders").with(BackofficeController::customerViewOrders);
        //router.POST().route("/posters/backoffice/customer/{customerId}/view/edit").with(BackofficeController::customerViewEdit);
        router.GET().route("/posters/backoffice/customer/{customerId}/edit").with(BackofficeController::customerEdit);
        //router.GET().route("/posters/backoffice/customer/{customerId}/edit").with(BackofficeController::customerEdit);
        router.POST().route("/posters/backoffice/customer/{customerId}/edit-complete").with(BackofficeController::customerEditComplete);
        router.POST().route("/posters/backoffice/customer/{customerId}/del-ship-address").with(BackofficeController::customerViewShipppingAddressDelete);
        router.POST().route("/posters/backoffice/customer/{customerId}/del-bill-address").with(BackofficeController::customerViewBillingAddressDelete);
        router.POST().route("/posters/backoffice/customer/{customerId}/edit-ship-address").with(BackofficeController::shippingAddressEdit);
        router.POST().route("/posters/backoffice/customer/{customerId}/edit-bill-address").with(BackofficeController::billingAddressEdit);
        router.POST().route("/posters/backoffice/customer/{customerId}/del-payment-info").with(BackofficeController::paymentInfoDelete);
        router.POST().route("/posters/backoffice/customer/{customerId}/edit-payment-info").with(BackofficeController:: paymentInfoEdit);
        router.POST().route("/posters/backoffice/customer/{customerId}/payment-edit-complete").with(BackofficeController.class, "paymentInfoEditComplete");
        router.POST().route("/posters/backoffice/customer/{customerId}/billing-edit-complete").with(BackofficeController.class, "billingAddressEditComplete");
        router.POST().route("/posters/backoffice/customer/{customerId}/shipping-edit-complete").with(BackofficeController.class, "shippingAddressEditComplete");
        router.POST().route("/posters/backoffice/customer/{customerId}/view-edit-complete").with(BackofficeController.class, "customerViewEditComplete");

        //router.POST().route("/posters/backoffice/customer/{customerId}/view-edit-complete").with((request) -> BackofficeController.customerViewEditComplete(request.routeParameter("customerId")));
  
        //router.POST().route("/posters/backoffice/customer/{customerId}/del-ship-address").with(BackofficeController::customerViewShipppingAddressDelete);

        // ############################################################
        // Catalog
        // ############################################################
        router.GET().route("/posters/backoffice/catalog").with(BackofficeController::catalog);

        // ############################################################
        // Products
        // ############################################################
        router.GET().route("/posters/backoffice/product").with(BackofficeController::productList);
        // router.GET().route("/posters/backoffice/product/p{pageNumber}").with(BackofficeController::productPage);
        router.GET().route("/posters/backoffice/product/{productId}").with(BackofficeController::productView);
        router.GET().route("/posters/backoffice/product/{productId}/edit").with(BackofficeController::productEdit);
        router.POST().route("/posters/backoffice/product/{productId}").with(BackofficeController.class, "productEditComplete");

        // ############################################################
        // Preferences
        // ############################################################
        router.GET().route("/posters/backoffice/preferences").with(BackofficeController::preferences);

        // ############################################################
        // Import/Export
        // ############################################################
        router.GET().route("/posters/backoffice/data-management").with(BackofficeController::dataManagement);

        // ############################################################
        // Statistics JSON provider
        // ############################################################
        router.GET().route("/posters/backoffice/JSON").with(BackofficeController::statisticsJSON);

        // ############################################################
        // Search Page
        // ############################################################
        router.GET().route("/posters/backoffice/search").with(BackofficeController::searchList);

        // ############################################################
        // Page Not Found
        // ############################################################
        router.GET().route("/posters/backoffice/.*").with(BackofficeController::Error404);
        // ############################################################
        // Dark Mode
        // ############################################################
        router.POST().route("/posters/backoffice/dark-mode").with(BackofficeController::dark);


        // ############################################################
        // home page
        // ############################################################
        router.GET().route("/").with(() -> Results.redirect("/en-US"));
        router.GET().route("/{urlLocale}/").with(WebShopController::index);
        router.GET().route("/{urlLocale}").with(WebShopController::index);

        // ############################################################
        // search
        // ############################################################
        router.GET().route("/{urlLocale}/search").with(SearchController::searchProduct);
        router.POST().route("/{urlLocale}/getProductOfSearch").with(SearchController::getProductOfSearch);
        router.GET().route("/{urlLocale}/notFound").with(SearchController::noResult);
        router.GET().route("/{urlLocale}/noResult").with(SearchController::noResult);

        // ############################################################
        // customer registration
        // ############################################################
        router.GET().route("/{urlLocale}/login").with(CustomerController::loginForm);
        router.POST().route("/{urlLocale}/login").with(CustomerController::login);
        router.GET().route("/{urlLocale}/logout").with(CustomerController::logout);
        router.GET().route("/{urlLocale}/registration").with(CustomerController::registration);
        router.POST().route("/{urlLocale}/registration").with(CustomerController::registrationCompleted);

        // ############################################################
        // customer backend
        // ############################################################
        router.GET().route("/{urlLocale}/accountOverview").with(CustomerController::accountOverview);
        router.GET().route("/{urlLocale}/paymentOverview").with(CustomerController::paymentOverview);
        router.GET().route("/{urlLocale}/addressOverview").with(CustomerController::addressOverview);
        router.GET().route("/{urlLocale}/settingOverview").with(CustomerController::settingOverview);
        router.GET().route("/{urlLocale}/orderOverview").with(CustomerController::orderOverview);

        // ############################################################
        // customer's payment
        // ############################################################
        router.GET().route("/{urlLocale}/addPaymentToCustomer").with(CustomerController::addPaymentToCustomer);
        router.POST().route("/{urlLocale}/addPaymentToCustomer").with(CustomerController::addPaymentToCustomerCompleted);
        router.POST().route("/{urlLocale}/deletePayment").with(CustomerController::deletePayment);
        router.POST().route("/{urlLocale}/updatePaymentMethod").with(CustomerController::updatePaymentMethod);
        router.POST().route("/{urlLocale}/updatePaymentMethodCompleted").with(CustomerController::updatePaymentMethodCompleted);

        // ############################################################
        // customer's addresses
        // ############################################################
        router.POST().route("/{urlLocale}/updateShippingAddress").with(CustomerController::updateShippingAddress);
        //router.POST().route("/updateShippingAddressCompleted").with(CustomerController::updateShippingAddressCompleted);
        router.POST().route("/{urlLocale}/updateShippingAddressCompleted").with(CustomerController.class, "updateShippingAddressCompleted");

        router.POST().route("/{urlLocale}/updateBillingAddress").with(CustomerController::updateBillingAddress);
        //router.POST().route("/updateBillingAddressCompleted").with(CustomerController::updateBillingAddressCompleted);
        router.POST().route("/{urlLocale}/updateBillingAddressCompleted").with(CustomerController.class, "updateBillingAddressCompleted");
        router.POST().route("/{urlLocale}/deleteBillingAddress").with(CustomerController::deleteBillingAddress);
        router.POST().route("/{urlLocale}/deleteShippingAddress").with(CustomerController::deleteShippingAddress);
        router.GET().route("/{urlLocale}/addShippingAddressToCustomer").with(CustomerController::addShippingAddressToCustomer);
        //router.POST().route("/addShippingAddressToCustomerCompleted").with(CustomerController::addShippingAddressToCustomerCompleted);
        //router.POST().route("/addShippingAddressToCustomerCompleted").with((req, res) -> CustomerController.addShippingAddressToCustomerCompleted(req, res));
        router.POST().route("/{urlLocale}/addShippingAddressToCustomerCompleted").with(CustomerController.class, "addShippingAddressToCustomerCompleted");

        router.GET().route("/{urlLocale}/addBillingAddressToCustomer").with(CustomerController::addBillingAddressToCustomer);
        //router.POST().route("/addBillingAddressToCustomerCompleted").with(CustomerController::addBillingAddressToCustomerCompleted);
        router.POST().route("/{urlLocale}/addBillingAddressToCustomerCompleted").with(CustomerController.class, "addBillingAddressToCustomerCompleted");

        // ############################################################
        // customer's personal data
        // ############################################################
        router.GET().route("/{urlLocale}/changeNameOrEmail").with(CustomerController::changeNameOrEmail);
        router.POST().route("/{urlLocale}/changeNameOrEmailCompleted").with(CustomerController::changeNameOrEmailCompleted);
        router.GET().route("/{urlLocale}/changePassword").with(CustomerController::changePassword);
        router.POST().route("/{urlLocale}/changePasswordCompleted").with(CustomerController::changePasswordCompleted);
        router.GET().route("/{urlLocale}/confirmDeleteAccount").with(CustomerController::confirmDeleteAccount);
        router.POST().route("/{urlLocale}/deleteAccount").with(CustomerController::deleteAccount);

        // ############################################################
        // catalog and products
        // ############################################################
        router.GET().route("/{urlLocale}/productDetail/{product}").with(CatalogController::productDetail);
        router.GET().route("/{urlLocale}/topCategory/{topCategory}").with(CatalogController::topCategoryOverview);
        router.GET().route("/{urlLocale}/category/{subCategory}").with(CatalogController::productOverview);
        router.POST().route("/{urlLocale}/getProductOfTopCategory").with(CatalogController::getProductOfTopCategory);
        router.POST().route("/{urlLocale}/getProductOfSubCategory").with(CatalogController::getProductOfSubCategory);

        // ############################################################
        // cart
        // ############################################################
        router.GET().route("/{urlLocale}/cart").with(CartController::cart);
        router.GET().route("/{urlLocale}/deleteFromCart").with(CartController::deleteFromCart);
        router.GET().route("/{urlLocale}/updateProductCount").with(CartController::updateProductCount);
        router.POST().route("/{urlLocale}/updatePrice").with(CartController::updatePrice);
        router.GET().route("/{urlLocale}/addToCartSlider").with(CartController::addToCart);
        router.GET().route("/{urlLocale}/getMiniCartElements").with(CartController::getMiniCartElements);

        // ############################################################
        // checkout
        // ############################################################
        router.GET().route("/{urlLocale}/checkout").with(CheckoutController::checkout);
        router.GET().route("/{urlLocale}/enterShippingAddress").with(CheckoutController::enterShippingAddress);
        //router.POST().route("/shippingAddressCompleted").with(CheckoutController::shippingAddressCompleted);
        router.POST().route("/{urlLocale}/shippingAddressCompleted").with(CheckoutController.class, "shippingAddressCompleted");
        router.GET().route("/{urlLocale}/enterBillingAddress").with(CheckoutController::enterBillingAddress);
        router.POST().route("/{urlLocale}/billingAddressCompleted").with(CheckoutController::billingAddressCompleted);
        router.POST().route("/{urlLocale}/addShippingAddressToOrder").with(CheckoutController::addShippingAddressToOrder);
        router.POST().route("/{urlLocale}/addBillingAddressToOrder").with(CheckoutController::addBillingAddressToOrder);
        router.GET().route("/{urlLocale}/enterPaymentMethod").with(CheckoutController::enterPaymentMethod);
        router.POST().route("/{urlLocale}/addPaymentToOrder").with(CheckoutController::addPaymentToOrder);
        router.POST().route("/{urlLocale}/paymentMethodCompleted").with(CheckoutController::paymentMethodCompleted);
        router.GET().route("/{urlLocale}/checkoutOverview").with(CheckoutController::checkoutOverview);
        router.POST().route("/{urlLocale}/checkoutCompleted").with(CheckoutController::checkoutCompleted);
        router.GET().route("/{urlLocale}/orderConfirmation").with(CheckoutController::orderConfirmation);

        // ############################################################
        // cart
        // ############################################################
        router.GET().route("/{urlLocale}/ok3ok2ru8udqx7gZGS9n/statusInfo").with(BreakController::statusInfo);
        router.POST().route("/{urlLocale}/ok3ok2ru8udqx7gZGS9n/statusUpdate").with(BreakController::statusUpdate);

    }
}
