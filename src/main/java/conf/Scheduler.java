package conf;

import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Cart;
import models.CartProduct;
import ninja.scheduler.Schedule;

import com.google.inject.Singleton;

@Singleton
public class Scheduler
{

    /**
     * Deletes unused carts.
     */
    @Schedule(delay = 3600, timeUnit = TimeUnit.SECONDS)
    public void deleteUnusedCart()
    {
        List<Cart> baskets = Cart.getAllCarts();
        // check each cart
        for (Cart basket : baskets)
        {
            // just delete if cart belongs to no customer
            if (basket.getCustomer() == null)
            {
                // delete cart, if last update is more than 3600 seconds ago
                boolean delete = true;
                List<CartProduct> basketProducts = basket.getProducts();
                // check each product of the cart
                for (CartProduct basketProduct : basketProducts)
                {
                    if (basketProduct.getLastUpdate().getTime() + (long) 3600000 > System.currentTimeMillis())
                    {
                        delete = false;
                        break;
                    }
                }
                if (delete)
                {
                    basket.delete();
                }
            }
        }
    }
}
