package controllers_backoffice;

import java.util.Optional;
import java.util.List;

import com.avaje.ebean.Ebean;


import filters.SessionUserExistFilter;
import models.Order;
import models.Customer;
import models_backoffice.User;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import util.session.SessionHandling;


/**
 * Backoffice Controller, it controls the behaviour of the backoffice.
 * Must be signed in as a backoffice user in order to access any pages within the backoffice.
 * 
 * @author kennygozali
 */

public class BackofficeController {

    private final Optional<String> language = Optional.of("en");

    /**
     * Returns the homepage of the Backoffice.
     * 
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result homepage(final Context context)
    {   
        Result result = Results.html();
        // Find all of the orders
        List<Order> orders = Ebean.find(Order.class).findList();
        // Add orders into the back office
        result.render("orders", orders);

        // Find all of the customers
        List<Customer> customers = Ebean.find(Customer.class).findList();
        // Add orders into the back office
        result.render("customers", customers);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the admin users
        List<User> users = Ebean.find(User.class).findList();
        // Add all users into the back office
        result.render("users", users);
        return result;
    }

    /**
     * Returns the view of a single admin user.
     * 
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userView(
        final Context context,
        @PathParam("userId") String userId) 
    {
        
        Result result = Results.html();
        
        // Find user with the id from the params
        User user = Ebean.find(User.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);
        
        return result;
    }
}
