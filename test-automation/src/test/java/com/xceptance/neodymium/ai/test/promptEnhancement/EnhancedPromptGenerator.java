package com.xceptance.neodymium.ai.test.promptEnhancement;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFolder;
import com.xceptance.neodymium.junit5.NeodymiumTestGenerator;
import com.xceptance.neodymium.util.Neodymium;

/**
 * A showcase class demonstrating how to use the AiPromptGenerator to
 * automatically generate test scripts via
 * exploratory AI. You can execute this from your IDE to try out the new AI
 * prompt generation feature.
 */
// @Browser("Chrome_1500x1000")
@Browser("Chrome_1500x1000_headless")
@DataFolder("generated")
public class EnhancedPromptGenerator {

        public static String system_context = """
                        - On the page the "Buy Here" button will lead to the Product Page and will NOT add the product to the cart!
                        - After adding a product to the cart ALWAYS RELOAD the page.
                        - To reach the cart page ALWAYS open the mini cart, by clicking the cart ICON.
                        """;

        @NeodymiumTestGenerator
        public void placeGuestOrder() {
                Neodymium.ai().generatePrompt("""
                                Add a product to the cart and place an order.
                                """, system_context);

        }

        @NeodymiumTestGenerator
        public void placeRegisteredOrder() {
                Neodymium.ai().generatePrompt("""
                                Register a new user
                                Log in with this user
                                Add a product to the cart and place an order.
                                go to the my account page
                                delete the user
                                """, system_context);

        }

        @NeodymiumTestGenerator
        public void orderHistory() {
                Neodymium.ai().generatePrompt("""
                                Register a new user
                                Log in with this user
                                Add a product to the cart and place an order.
                                go to the my account page
                                check if the placed order is visible inside the order history
                                delete the user
                                """, system_context);

        }

        @NeodymiumTestGenerator
        public void searchAndVerifyResults()
        {
                Neodymium.ai().generatePrompt("""
                                Search for a specific product name, verify the results list contains that product,
                                and ensure the price is displayed.
                                """, system_context);
        }

        @NeodymiumTestGenerator
        public void userRegistrationAndLogin() {
                Neodymium.ai().generatePrompt("""
                                Create a new customer account, log out, and then log back in
                    using the newly created credentials. Delete the account afterwards.
                                """, system_context);
        }

        @NeodymiumTestGenerator
        public void manageCartQuantities() {
                Neodymium.ai().generatePrompt("""
                                Add a product to the cart, increase the quantity, remove the item,
                                and verify the cart is empty.
                                """, system_context);
        }

        @NeodymiumTestGenerator
        public void navigateProductCategories() {
                Neodymium.ai().generatePrompt("""
                                Navigate through the main category menu and subcategories to ensure
                                products load correctly for each section.
                                """, system_context);
        }

        @NeodymiumTestGenerator
        public void verifyProductDetails() {
                Neodymium.ai().generatePrompt("""
                                Open a product detail page and verify that the description,
                                price and add to cart button are visible and not broken.
                                """, system_context);
        }

        @NeodymiumTestGenerator
        public void deleteAllItemsFromCart() {
                Neodymium.ai().generatePrompt(
                                """
                                                Add three different item to the cart, open the cart page, remove all the products and validate that the cart is empty.
                                                """,
                                system_context);
        }

        @NeodymiumTestGenerator
        public void pageOnCategory() {
                Neodymium.ai().generatePrompt("""
                                In a category view use the paging'.
                                """, system_context);
        }

        @NeodymiumTestGenerator
        public void testFormValidations() {
                Neodymium.ai().generatePrompt("""
                                Attempt to submit the checkout form with empty required fields
                                and verify that appropriate error messages appear.
                                """, system_context);
        }
}
