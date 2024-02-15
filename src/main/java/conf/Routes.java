/*
 * Copyright (c) 2013-2023 Xceptance Software Technologies GmbH
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
        router.GET().route("/").with(WebShopController::index);

        // ############################################################
        // search
        // ############################################################
        router.GET().route("/search").with(SearchController::searchProduct);
        router.POST().route("/getProductOfSearch").with(SearchController::getProductOfSearch);
        router.GET().route("/notFound").with(SearchController::noResult);
        router.GET().route("/noResult").with(SearchController::noResult);

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
        //router.POST().route("/addShippingAddressToCustomerCompleted").with(CustomerController::addShippingAddressToCustomerCompleted);
        //router.POST().route("/addShippingAddressToCustomerCompleted").with((req, res) -> CustomerController.addShippingAddressToCustomerCompleted(req, res));
        router.POST().route("/addShippingAddressToCustomerCompleted").with(CustomerController.class, "addShippingAddressToCustomerCompleted");

        router.GET().route("/addBillingAddressToCustomer").with(CustomerController::addBillingAddressToCustomer);
        //router.POST().route("/addBillingAddressToCustomerCompleted").with(CustomerController::addBillingAddressToCustomerCompleted);
        router.POST().route("/addBillingAddressToCustomerCompleted").with(CustomerController.class, "addBillingAddressToCustomerCompleted");

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
        //router.POST().route("/shippingAddressCompleted").with(CheckoutController::shippingAddressCompleted);
        router.POST().route("/shippingAddressCompleted").with(CheckoutController.class, "shippingAddressCompleted");
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


    }
}
