package controllers_backoffice;

import java.util.Optional;
import java.util.List;
import java.util.regex.Pattern;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionUserExistFilter;
import models.Order;
import models.Customer;
import models_backoffice.User;
import models.Product;
import models.SubCategory;
import models.TopCategory;
import models.PosterSize;
import models.ProductPosterSize;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import util.session.SessionHandling;

/**
 * Backoffice Controller, it controls the behaviour of the backoffice. Must be signed in as a backoffice user in order
 * to access any pages within the backoffice.
 * 
 * @author kennygozali
 */

public class BackofficeController
{
    @Inject
    PosterConstants xcpConf;

    private final Optional<String> language = Optional.of("en");

    /**
     * Returns the homepage of the Backoffice.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result homepage(final Context context)
    {
        Result result = Results.html();

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
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userView(final Context context, @PathParam("userId") String userId)
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

    /**
     * Edit an admin user's information and credentials.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userEdit(final Context context, @PathParam("userId") String userId)
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

    /**
     * Persist the updated user into the database.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userEditComplete(final Context context, @PathParam("userId") String userId, @Param("name") final String name,
                                   @Param("firstName") final String firstName, @Param("email") final String email,
                                   @Param("password") final String password)
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

        boolean failure = false;
        // email is not valid
        if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            failure = true;
        }
        if (failure)
        {
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user/" + userId + "/edit");
        }
        // all input fields might be correct
        else
        {
            user.setName(name);
            user.setFirstName(firstName);
            user.setEmail(email);
            user.hashPasswd(password);
            // save user
            user.save();
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/posters/backoffice");
        }
    }

    /**
     * Deletes an admin user.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userDelete(final Context context, @PathParam("userId") String userId)
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

        // Don't delete if it is the same as the user
        if (currentUser.getEmail() == user.getEmail())
        {
            return Results.redirect(context.getContextPath() + "/posters/backoffice");

        }
        else
        {
            user.delete();
            return Results.redirect(context.getContextPath() + "/posters/backoffice");
        }

    }

    /**
     * List out all of the admin users.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userList(final Context context)
    {
        Result result = Results.html();

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
     * Shows the statistics of the poster website.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result statistics(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Returns the view of an order.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result orderView(final Context context, @PathParam("orderId") String orderId)
    {

        Result result = Results.html();

        // Find order with the id from the params
        Order order = Ebean.find(Order.class, orderId);
        // Render order into template
        result.render("order", order);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * List out all of the orders.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result orderList(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the orders
        List<Order> orders = Ebean.find(Order.class).findList();
        // Add orders into the back office
        result.render("orders", orders);

        return result;
    }

    /**
     * List out the catalog.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result catalog(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find topCategories with the id from the params
        List<TopCategory> topCategories = Ebean.find(TopCategory.class).findList();
        // Render topCategories into template
        result.render("topCategories", topCategories);

        return result;
    }

    /**
     * Returns the view of a single product.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productView(final Context context, @PathParam("productId") String productId)
    {

        Result result = Results.html();

        // Find product with the id from the params
        Product product = Ebean.find(Product.class, productId);
        // Render product into template
        result.render("product", product);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Edit a product's information.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productEdit(final Context context, @PathParam("productId") String productId)
    {

        Result result = Results.html();

        // Find product with the id from the params
        Product product = Ebean.find(Product.class, productId);
        // Render product into template
        result.render("product", product);

        // Find subCategories with the id from the params
        List<SubCategory> subCategories = Ebean.find(SubCategory.class).findList();
        // Render subCategories into template
        result.render("subCategories", subCategories);

        // Find topCategories with the id from the params
        List<TopCategory> topCategories = Ebean.find(TopCategory.class).findList();
        // Render topCategories into template
        result.render("topCategories", topCategories);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Persist the updated product into the database.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productEditComplete(final Context context, @PathParam("productId") String productId, @Param("name") final String name,
                                      @Param("imageURL") final String imageURL, @Param("minimumPrice") final double minimumPrice, 
                                      @Param("descriptionDetail") final String descriptionDetail,
                                      @Param("descriptionOverview") final String descriptionOverview,
                                      @Param("subCategory") final String subCategoryName,
                                      @Param("topCategory") final String topCategoryName,
                                      @Param("width1") final int width1, @Param("height1") final int height1,
                                      @Param("width2") final int width2, @Param("height2") final int height2,
                                      @Param("width3") final int width3, @Param("height3") final int height3)
    {

        Result result = Results.html();

        // Find product with the id from the params
        Product product = Ebean.find(Product.class, productId);
        // Render product into template
        result.render("product", product);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find subcategory with the subcategory name from the params
        SubCategory subCategory = Ebean.find(SubCategory.class).where().eq("name" , subCategoryName).findUnique();

        // Find subcategory with the subcategory name from the params
        TopCategory topCategory = Ebean.find(TopCategory.class).where().eq("name" , topCategoryName).findUnique();

        // Set new poster size
        PosterSize posterSize1 = new PosterSize();
        PosterSize posterSize2 = new PosterSize();
        PosterSize posterSize3 = new PosterSize();

        posterSize1.setWidth(width1);
        posterSize2.setWidth(width2);
        posterSize3.setWidth(width3);

        posterSize1.setHeight(height1);
        posterSize2.setHeight(height2);
        posterSize3.setHeight(height3);

        posterSize1.save();
        posterSize2.save();
        posterSize3.save();

        // Set new poster product size
        ProductPosterSize productPosterSize1 = new ProductPosterSize();
        ProductPosterSize productPosterSize2 = new ProductPosterSize();
        ProductPosterSize productPosterSize3 = new ProductPosterSize();

        productPosterSize1.setSize(posterSize1);
        productPosterSize2.setSize(posterSize2);
        productPosterSize3.setSize(posterSize3);

        productPosterSize1.setProduct(product);
        productPosterSize2.setProduct(product);
        productPosterSize3.setProduct(product);

        // debugging purpose, setting price to 1

        productPosterSize1.setPrice(1.0);
        productPosterSize2.setPrice(1.0);
        productPosterSize3.setPrice(1.0);



        productPosterSize1.save();
        productPosterSize2.save();
        productPosterSize3.save();


        // Update product available sizes
        List<ProductPosterSize> currentAvailableSizes = List.of(productPosterSize1, productPosterSize2, productPosterSize3); 
        product.setAvailableSizes(currentAvailableSizes);

        // all input fields might be correct

        product.setName(name);
        product.setImageURL(imageURL);
        product.setMinimumPrice(minimumPrice);
        product.setDescriptionDetail(descriptionDetail);
        product.setDescriptionOverview(descriptionOverview);
        product.setSubCategory(subCategory);
        product.setTopCategory(topCategory);
        // save product
        product.save();
        // show page to log-in
        return Results.redirect(context.getContextPath() + "/posters/backoffice/product/" + productId);

    }

    /**
     * List out all of the products.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productList(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the products
        List<Product> products = Ebean.find(Product.class).findList();
        // Add products into the back office
        result.render("products", products);

        return result;
    }

    /**
     * Returns the view of a customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerView(final Context context, @PathParam("customerId") String customerId)
    {

        Result result = Results.html();

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Edit a customer's information and credentials.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerEdit(final Context context, @PathParam("customerId") String customerId)
    {

        Result result = Results.html();

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Persist the updated customer into the database.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerEditComplete(final Context context, @PathParam("customerId") String customerId, @Param("name") final String name,
                                       @Param("firstName") final String firstName, @Param("email") final String email,
                                       @Param("password") final String password)
    {

        Result result = Results.html();

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        boolean failure = false;
        // email is not valid
        if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            failure = true;
        }
        if (failure)
        {
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user/" + customerId + "/edit");
        }
        // all input fields might be correct
        else
        {
            customer.setName(name);
            customer.setFirstName(firstName);
            customer.setEmail(email);
            customer.hashPasswd(password);
            // save customer
            customer.save();
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/posters/backoffice");
        }
    }

    /**
     * List out all of the customers.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerList(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the customers
        List<Customer> customers = Ebean.find(Customer.class).findList();
        // Add customers into the back office
        result.render("customers", customers);

        return result;
    }

    /**
     * Creates the preference page.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result preferences(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Controls the import/export operations within the backoffice.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result dataManagement(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * This response appears when a backoffice user inputted an invalid backoffice url.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result Error404(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }
}