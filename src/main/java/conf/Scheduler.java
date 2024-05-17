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

import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Cart;
import models.Customer;
import models.CartProduct;
import models.Order;
import ninja.scheduler.Schedule;

import com.avaje.ebean.Ebean;
import com.google.inject.Singleton;

/**
 * The class provides methods to do some tasks in a scheduled way. Just add the @{@link Schedule} annotation.
 * 
 * @author sebastianloob
 */
@Singleton
public class Scheduler
{

    /**
     * Deletes unused carts. A cart is unused, if it is not set to a customer and the cart was last used at least one
     * hour ago. Will be triggered every hour.
     */
    @Schedule(delay = 3600, timeUnit = TimeUnit.SECONDS)
    public void deleteUnusedCart()
    {
        // get all carts, which are stored in the database
        final List<Cart> carts = Cart.getAllCarts();
        // check each cart
        for (final Cart cart : carts)
        {
            // just delete if cart belongs to no customer
            if (cart.getCustomer() == null)
            {
                // delete cart, if last update is more than 3600 seconds ago
                boolean delete = true;
                final List<CartProduct> cartProducts = cart.getProducts();
                // check each product of the cart
                for (final CartProduct cartProduct : cartProducts)
                {
                    if (cartProduct.getLastUpdate().getTime() + 3600000 > System.currentTimeMillis())
                    {
                        delete = false;
                        break;
                    }
                }
                if (delete)
                {
                    cart.delete();
                }
            }
        }
    }

    /**
     * Deletes pending orders, that have been existing for more than a day inside the database. Will be triggered every 24 hours.
     */
    @Schedule(delay = 86400, timeUnit = TimeUnit.SECONDS) 
    public void deletePendingOrder() {
        // // Get all orders that exist in the database irrespective of which customer orders belong to.
        // final List<Order> orders = Customer.getEveryOrder();

        // // Calculate the timestamp for one day ago
        // long oneDayAgoTimestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);

        // // Iterate through each order
        // for (final Order order : orders) {
        //     // Check if the order is pending and the creation time is more than one day ago
        //     if (order.getOrderStatus().equals("Pending") && order.getLastUpdate().getTime() < oneDayAgoTimestamp) {
        //         // Delete the order
        //         Ebean.delete(order);
        //     }
        // }

        Order.deleteOldPendingOrders();
    }

}
