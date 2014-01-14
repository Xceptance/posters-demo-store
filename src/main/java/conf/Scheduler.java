package conf;

import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Basket;
import models.Basket_Product;
import ninja.scheduler.Schedule;
import util.database.BasketInformation;

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
        List<Basket> baskets = BasketInformation.getAllBaskets();
        // check each cart
        for (Basket basket : baskets)
        {
            // just delete if cart belongs to no customer
            if (basket.getCustomer() == null)
            {
                // delete cart, if last update is more than 3600 seconds ago
                boolean delete = true;
                List<Basket_Product> basketProducts = basket.getProducts();
                // check each product of the cart
                for (Basket_Product basketProduct : basketProducts)
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
