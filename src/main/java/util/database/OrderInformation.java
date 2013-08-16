package util.database;

import java.util.List;
import java.util.Map;

import models.Customer;
import models.Order;
import models.Order_Product;

import com.avaje.ebean.Ebean;

public abstract class OrderInformation
{

    /**
     * Returns the order by the order's id.
     * 
     * @param id
     * @return
     */
    public static Order getOrderById(int id)
    {
        Order order = Ebean.find(Order.class, id);
        return order;
    }

    /**
     * Returns all orders, which are stored in the database.
     * 
     * @return
     */
    public static List<Order> getAllOrders()
    {
        // get all orders
        List<Order> orders = Ebean.find(Order.class).findList();
        return orders;
    }

    /**
     * Creates and returns a new order.
     * 
     * @return
     */
    public static Order createNewOrder()
    {
        // create new order
        Order order = new Order();
        // save order
        Ebean.save(order);
        // get new order by id
        int id = order.getId();
        Order newOrder = Ebean.find(Order.class, id);
        // return new order
        return newOrder;
    }

    /**
     * Adds the order to the data map.
     * 
     * @param data
     */
    public static void addOrderToMap(Order order, final Map<String, Object> data)
    {
        data.put("order", order);
    }

    public static void addProductsFromOrderToMap(Order order, final Map<String, Object> data)
    {
        // get all products of the order
        List<Order_Product> orderProducts = Ebean.find(Order_Product.class).where().eq("order", order).findList();
        // add all products of the order
        data.put("orderProducts", orderProducts);
    }

    /**
     * delete order, if no customer is set
     */
    public static void removeOrder(Order order)
    {
        if (order.getCustomer() == null)
        {
            Ebean.delete(order);
        }
    }

    public static List<Order> getAllOrdersOfCustomer(Customer customer)
    {
        return Ebean.find(Order.class).where().eq("customer", customer).findList();
    }

}
